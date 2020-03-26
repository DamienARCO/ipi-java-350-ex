package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeRepositoryIntegrationTest {
    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    public void setup() {
        employeRepository.deleteAll();
    }

    @Test
    public void testAvgPerformanceWhereMatriculeStartsWith() {
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

        Employe employe5 = new Employe();
        employe5.setMatricule("T87654");
        employe5.setPerformance(5);

        employeRepository.save(employe1);
        employeRepository.save(employe2);
        employeRepository.save(employe3);
        employeRepository.save(employe4);
        employeRepository.save(employe5);

        Double avg = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");

        Assertions.assertThat(avg).isEqualTo(2.75);
    }
}
