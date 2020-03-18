package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @InjectMocks
    private EmployeService employeService;
    @Mock
    private EmployeRepository employeRepository;

    @ParameterizedTest(name = "Le {2} {1} {0} de niveau {3} de matricule {6} " +
            "a été embauché à temps plein pour un salaire de {7} €")
    @CsvSource({
            "'Doe', 'John', 'COMMERCIAL', 'BTS_IUT', 1.0, , 'C00001', 1825.46",
            "'Doe', 'Johnny', 'COMMERCIAL', 'BTS_IUT', 1.0, '12345', 'C12346', 1825.46",
            "'Doy', 'John', 'TECHNICIEN', 'BTS_IUT', 1.0, '23456', 'T23457', 1825.46",
    })
    public void testEmbaucheEmployeTempsPlein(
            String nom, String prenom, Poste poste, NiveauEtude niveauEtude, Double tempsPartiel,
            String lastMatricule, String matricule, Double salaire) throws EmployeException {
        //Given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(lastMatricule);
        Mockito.when(employeRepository.findByMatricule(matricule)).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, Mockito.times(1)).save(employeCaptor.capture());
        Employe employe = employeCaptor.getValue();
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getMatricule()).isEqualTo(matricule);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(tempsPartiel);
        //Si dateEmbauche est au millième de secondes.
        Assertions.assertThat(
                employe.getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        ).isEqualTo(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertThat(employe.getSalaire()).isEqualTo(salaire);
        //Performance_base = 1
        Assertions.assertThat(employe.getPerformance()).isEqualTo(1);
    }

    @Test
    public void testEmbaucheEmployeLimiteMatricule() {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.COMMERCIAL;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When & Then
        Assertions.assertThatThrownBy(() -> {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        }).isInstanceOf(EmployeException.class).hasMessage("Limite des 100000 matricules atteinte !");
    }

    @ParameterizedTest
    @CsvSource({
            "'C12345', 1, 10000, 10000, 1.0, 1",
            "'C23456', 1, 10000, 10000, 2.0, 1",
            "'C34567', 2, 10000, 10000, 1.0, 3",
            "'C45678', 2, 7000, 10000, 10.0, 1",
            "'C56789', 5, 7000, 10000, 10.0, 1",
            "'C67890', 2, 9000, 10000, 10.0, 1",
            "'C09876', 5, 9000, 10000, 10.0, 3",
            "'C98765', 5, 11000, 10000, 10.0, 6",
            "'C98765', 5, 13000, 10000, 10.0, 9",
    })
    public void testCalculPerformanceCommercial(
            String matricule, Integer performance, Long caTraite, Long objectifCa,
            Double performanceMoyenne, Integer performanceCalcule) throws EmployeException {
        //Given
        Employe employe = new Employe();
        employe.setMatricule(matricule);
        employe.setPerformance(performance);
        Mockito.when(employeRepository.findByMatricule(matricule)).thenReturn(employe);
        Mockito.when(
                employeRepository.avgPerformanceWhereMatriculeStartsWith("C")
        ).thenReturn(performanceMoyenne);

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        //Then
        ArgumentCaptor<Employe> employeCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, Mockito.times(1)).save(employeCaptor.capture());
        Employe employeAfter = employeCaptor.getValue();
        Assertions.assertThat(employeAfter.getPerformance()).isEqualTo(performanceCalcule);
    }

    @Test
    public void testCalculPerformanceCommercialWithFinByMatriculeNull() {
        Mockito.when(employeRepository.findByMatricule("C12345")).thenReturn(null);

        Assertions.assertThatThrownBy(() -> {
            employeService.calculPerformanceCommercial("C12345", 10000L, 10000L);
        }).isInstanceOf(EntityNotFoundException.class).hasMessage("Le matricule C12345 n'existe pas !");
    }

    @Test
    public void testCalculPerformanceCommercialWithMatriculeNull() {
        Assertions.assertThatThrownBy(() -> {
            employeService.calculPerformanceCommercial(null, 10000L, 10000L);
        }).isInstanceOf(EmployeException.class).hasMessage("Le matricule ne peut être null et doit commencer par un C !");
    }

    @Test
    public void testCalculPerformanceCommercialWithBadMatricule() {
        Assertions.assertThatThrownBy(() -> {
            employeService.calculPerformanceCommercial("T12345", 10000L, 10000L);
        }).isInstanceOf(EmployeException.class).hasMessage("Le matricule ne peut être null et doit commencer par un C !");
    }

    @Test
    public void testCalculPerformanceCommercialWithBadCaTraite() {
        Assertions.assertThatThrownBy(() -> {
            employeService.calculPerformanceCommercial("C12345", -100L, 10000L);
        }).isInstanceOf(EmployeException.class).hasMessage("Le chiffre d'affaire traité ne peut être négatif ou null !");
    }

    @Test
    public void testCalculPerformanceCommercialWithBadObjectifCa() {
        Assertions.assertThatThrownBy(() -> {
            employeService.calculPerformanceCommercial("C12345", 10000L, -100L);
        }).isInstanceOf(EmployeException.class).hasMessage("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
    }

}
