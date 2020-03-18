package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeServiceIntegrationTest {
    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private EmployeService employeService;

    @BeforeEach
    public void setup() {
        employeRepository.deleteAll();
    }

    @Test
    public void testCalculPerformanceCommercial() throws EmployeException {
        employeRepository.deleteAll();
        //Given
        Employe employe1 = new Employe();
        employe1.setMatricule("C12345");
        employe1.setPerformance(1);

        Employe employe2 = new Employe();
        employe2.setMatricule("C34567");
        employe2.setPerformance(2);

        Employe employe3 = new Employe();
        employe3.setMatricule("C56789");
        employe3.setPerformance(3);

        Employe employe4 = new Employe();
        employe4.setMatricule("C98765");
        employe4.setPerformance(5);

        employeRepository.save(employe1);
        employeRepository.save(employe2);
        employeRepository.save(employe3);
        employeRepository.save(employe4);

        // employe test et jeux de test
        String matricule = employe3.getMatricule();
        long caTraite = 11000l;
        long objectifCa = 10000l;

        //When
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        //Then
        Employe employeAfter = employeRepository.findByMatricule(matricule);
        //CaTraité est entre 5% et 20% par rapport à l'objectif et performance calculée > moyenne (2.75) => performance = 3+1+1
        Assertions.assertThat(employeAfter.getPerformance()).isEqualTo(5);
    }

}
