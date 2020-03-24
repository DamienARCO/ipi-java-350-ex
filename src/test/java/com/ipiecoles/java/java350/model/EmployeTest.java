package com.ipiecoles.java.java350.model;

import com.ipiecoles.java.java350.exception.EmployeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

public class EmployeTest {

    /* ------------------------------ TEST getAnneeEmbauche ------------------------------ */


    // dateEmbauche avec date 2 ans avant aujourd'hui =>  2 années d'ancienneté
    @Test
    public void testGetAnneeEmbaucheMoinsDeuxAns() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(2));

        //When
        Integer nbAnnees = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnnees).isEqualTo(2);
    }

    // dateEmbauche dans le futur => 0
    @Test
    public void testGetAnneeEmbauchePlusDeuxAns() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(2));

        //When
        Integer nbAnnees = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnnees).isEqualTo(0);
    }

    // dateEmbauche aujourd'hui => 0
    @Test
    public void testGetAnneeEmbaucheAujourdhui() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());

        //When
        Integer nbAnnees = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnnees).isEqualTo(0);
    }

    // dateEmbauche indéfinie => 0
    @Test
    public void testGetAnneeEmbaucheNull() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);

        //When
        Integer nbAnnees = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnnees).isEqualTo(0);
    }

    /* ------------------------------ TEST getPrimeAnnuelle ------------------------------ */

    @ParameterizedTest(name = "Le matricule {0} avec une performance de {1}, une ancienneté de {2} ans " +
            "à temps partiel de {3} obtient une prime annuelle de {4}")
    @CsvSource({
            "'M12345',, 2, 1, 1900",
            "'M23456',, 2, 0.75, 1425",
            "'M23456',1, 2, 0.75, 1425",
            "'M23456',, 3, 0.75, 1500",
            "'C12345', 1, 2, 1, 1200",
            "'C23456', 1, 0, 1, 1000",
            "'C23456', 1, 0, 0.75, 750",
            "'T12345', 2, 0, 1, 2300"
    })
    public void testGetPrimeAnnuelle(String matricule, Integer performance, Integer nbAnciennete, Double tempsPartiel, Double prime) {
        //Given
        Employe employe = new Employe();
        employe.setMatricule(matricule);
        employe.setPerformance(performance);
        employe.setDateEmbauche(LocalDate.now().minusYears(nbAnciennete));
        employe.setTempsPartiel(tempsPartiel);

        // When Then
        Assertions.assertThat(employe.getPrimeAnnuelle()).isEqualTo(prime);
    }

    /* ------------------------------ TEST augmenterSalaire ------------------------------ */

    @ParameterizedTest(name = "Un salaire de {0}€ augmenté de {1} vaut {2}€")
    @CsvSource({
            "1000, 0.1, 1100",
            "2000, 0.1, 2200",
            "1000, 0, 1000",
            "1000, 1, 2000",
            "0, 1, 0",
    })
    public void testAugmenterSalaire(Double salaire, Double pourcentage, Double salaireAugmentée) throws EmployeException {
        //Given
        Employe employe = new Employe();
        employe.setSalaire(salaire);

        //When
        employe.augmenterSalaire(pourcentage);

        //Then
        Assertions.assertThat(employe.getSalaire()).isEqualTo(salaireAugmentée);
    }

    @Test
    public void testAugmenterSalairePoucentageNegatif() {
        //Given
        Employe employe = new Employe();
        employe.setSalaire(1000.0);

        //When & Then
        Assertions.assertThatThrownBy(() -> {
            employe.augmenterSalaire(-0.1);
        }).isInstanceOf(EmployeException.class).hasMessage("Le pourcentage d'augmentation ne peut pas être négatif !");
    }

    /* ------------------------------ TEST getNbRtt ------------------------------ */

    @ParameterizedTest(name = "En {0}, le cadre au taux d activité de {1} aura {2} jours de repos.")
    @CsvSource({
            "2019, 1.0, 8",
            "2019, 0.5, 4",
            "2021, 1.0, 11",
            "2022, 1.0, 10",
            "2028, 1.0, 8",
            "2032, 1.0, 12",
            "2033, 1.0, 10",
            "2034, 1.0, 8"
    })
    public void testGetNbRtt(Integer date, Double tpsPartiel, Integer nbRtt) {
        Employe employe = new Employe();
        employe.setTempsPartiel(tpsPartiel);

        Integer res = employe.getNbRtt(LocalDate.of(date, 1, 1));

        Assertions.assertThat(res).isEqualTo(nbRtt);
    }
}
