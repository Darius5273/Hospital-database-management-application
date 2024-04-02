package com.example.pr2;

import com.example.pr2.Models.Admission;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdmissionTest {

    @Test
    void getMaxAdmissionId() {
        Admission admission = new Admission();
        int maxId = admission.getMaxAdmissionId();
        assertTrue(maxId >= -1, "Max admission ID should be greater than or equal to -1");

    }

    @Test
    void isAdmissionBeforeDischarge() {
        String admissionDate = "2023-12-31";
        String dischargeDate = "2024-01-01";
        assertTrue(Admission.isAdmissionBeforeDischarge(admissionDate, dischargeDate),
                "Admission date should be before discharge date");
    }
}