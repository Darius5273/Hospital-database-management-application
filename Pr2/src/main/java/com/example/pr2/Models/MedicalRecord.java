package com.example.pr2.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicalRecord extends DBConnection{
    private String diagnosis;
    private int appointment_id;
    private int id;
    public static int maxRecordId;

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public int getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public static void setNextMaxRecordId() {
        String query = "SELECT COALESCE(MAX(record_id), 0) AS max_id FROM medical_records";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int maxId = resultSet.getInt("max_id");
                    maxRecordId = maxId + 1;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    public static ObservableList<MedicalRecord> getMedicalRecordsByDoctorId(int doctorId) {
        ObservableList<MedicalRecord> medicalRecords = FXCollections.observableArrayList();
        String query = "SELECT mr.* FROM medical_records mr " +
                "JOIN appointments a ON mr.appointment_id = a.appointment_id " +
                "WHERE a.doctor_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    MedicalRecord record = new MedicalRecord();
                    record.setId(resultSet.getInt("record_id"));
                    record.setAppointment_id(resultSet.getInt("appointment_id"));
                    record.setDiagnosis(resultSet.getString("diagnosis"));
                    medicalRecords.add(record);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return medicalRecords;
    }

    public static ObservableList<MedicalRecord> getMedicalRecordsByDate(String date, int doctorId) {
        ObservableList<MedicalRecord> medicalRecords = FXCollections.observableArrayList();
        String query = "SELECT * FROM medical_records " +
                "INNER JOIN appointments ON medical_records.appointment_id = appointments.appointment_id " +
                "WHERE appointments.appointment_date = ? AND appointments.doctor_id= ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, java.sql.Date.valueOf(date));
            preparedStatement.setInt(2,doctorId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    MedicalRecord medicalRecord = new MedicalRecord();
                    medicalRecord.setId(resultSet.getInt("record_id"));
                    medicalRecord.setAppointment_id(resultSet.getInt("appointment_id"));
                    medicalRecord.setDiagnosis(resultSet.getString("diagnosis"));
                    medicalRecords.add(medicalRecord);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return medicalRecords;
    }
    public static int getRecordIdByAppointmentAndDisease(int appointmentId, String disease) {
        int recordId = -1;

        String query = "SELECT record_id FROM medical_records WHERE appointment_id = ? AND diagnosis = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, appointmentId);
            preparedStatement.setString(2, disease);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    recordId = resultSet.getInt("record_id");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return recordId;
    }

}
