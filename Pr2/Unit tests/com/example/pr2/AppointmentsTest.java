package com.example.pr2;

import com.example.pr2.Models.Appointments;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentsTest {

    @Test
    void isValidTimeFormat() {
        assertTrue(Appointments.isValidTimeFormat("11:45"));
    }
}