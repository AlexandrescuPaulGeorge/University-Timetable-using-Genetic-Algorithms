package com.licenta.aplicatie;

import com.licenta.aplicatie.models.Teacher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TeacherTest {

    @Test
    public void testBuilderPattern() {
        // Arrange
        String expectedFirstname = "";
        String expectedLastname = "";
        String expectedEmail = "";


        Teacher teacher = Teacher.builder()
                .firstname(expectedFirstname)
                .lastname(expectedLastname)
                .email(expectedEmail)
                .build();


        assertEquals(expectedFirstname, teacher.getFirstname());
        assertEquals(expectedLastname, teacher.getLastname());
        assertEquals(expectedEmail, teacher.getEmail());
    }
}

