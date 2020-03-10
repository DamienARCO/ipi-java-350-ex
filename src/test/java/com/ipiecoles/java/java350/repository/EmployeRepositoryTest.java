package com.ipiecoles.java.java350.repository;


import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeRepositoryTest {
    @Autowired
    EmployeRepository employeRepository;

    @BeforeEach
    public void setup() {
        employeRepository.deleteAll();
    }

    @Test
    public void testFindLastMatricule0Employes() {
        //Given

        //When
        String res = employeRepository.findLastMatricule();
        //Then
        Assertions.assertThat(res).isNull();
    }

    @Test
    public void testFindLastMatricule3Employes() {
        //Given
        Employe employe1, employe2, employe3;
        employe1 = new Employe();
        employe2 = new Employe();
        employe3 = new Employe();
        employe1.setMatricule("M00001");
        employe2.setMatricule("M00003");
        employe3.setMatricule("T00002");

        employeRepository.save(employe1);
        employeRepository.save(employe2);
        employeRepository.save(employe3);

        //When
        String res = employeRepository.findLastMatricule();

        //Then
        Assertions.assertThat(res).isEqualTo("00003");
    }
}
