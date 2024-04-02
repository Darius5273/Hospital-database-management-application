package com.example.pr2.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Prescription extends DBConnection{
    private String medicine,dosage,frequency;
    private int id,record_id;

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }
    public static int getMaxPrescriptionId() {
        int maxPrescriptionId = -1;
        String query = "SELECT MAX(prescription_id) AS max_id FROM prescriptions_medicines";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    maxPrescriptionId = resultSet.getInt("max_id");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return maxPrescriptionId;
    }
    public static ObservableList<Prescription> getPrescriptionsByRecordId(int recordId) {
        ObservableList<Prescription> prescriptions = FXCollections.observableArrayList();
        String query = "SELECT * FROM prescriptions_medicines WHERE record_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, recordId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Prescription prescription = new Prescription();
                    prescription.setId(resultSet.getInt("prescription_id"));
                    prescription.setMedicine(resultSet.getString("medicine"));
                    prescription.setDosage(resultSet.getString("dosage"));
                    prescription.setFrequency(resultSet.getString("frequency"));
                    prescription.setRecord_id(resultSet.getInt("record_id"));
                    prescriptions.add(prescription);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return prescriptions;
    }
}
