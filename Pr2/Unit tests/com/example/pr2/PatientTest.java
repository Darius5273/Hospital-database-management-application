package com.example.pr2;

import com.example.pr2.Models.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
    }

    @Test
    void testCheckPatient_ValidPatient() {
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setGender("Male");
        patient.setPhoneNumber("1234567890");
        patient.setId("123-45-6789");
        assertTrue(patient.checkPatient());
    }

    @Test
    void isValidGenderTest() {
        String gender="male";
        boolean value = Patient.isValidGender(gender);
        assertTrue(value);
    }

    @Test
    void isValidNameFormatTest() {
        String firstName = "Alejandro", lastName="Smith";
        boolean value = Patient.isValidNameFormat(firstName,lastName);
        assertTrue(value);

    }

    @Test
    void isValidSSNTest() {
        String ssn="432-54-5421";
        boolean value=Patient.isValidSSN(ssn);
        assertTrue(value);
    }

}