package com.example.pr2.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor extends User{

    private int doctorId;
    private String specialization;
    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstName) {
        this.firstname = firstName;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastName(String lastName) {
        this.lastname = lastName;
    }

    public Doctor(int doctorId, String firstName, String lastName, String specialization) {
        this();
        this.firstname = firstName;
        this.lastname = lastName;
        this.doctorId = doctorId;
        this.specialization = specialization;
    }

    public Doctor(int doctorId, String specialization) {
        this();
        this.doctorId = doctorId;
        this.specialization = specialization;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Doctor() {
        super();
        doctorId = -1;
        specialization = null;
    }

    public boolean addDoctor() {
        String query = "INSERT INTO doctors (doctor_id, specialization) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, specialization);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public static ObservableList<Doctor> getDoctorsList() {
        ObservableList<Doctor> doctors = FXCollections.observableArrayList();
        String query = "SELECT doctors.doctor_id, users.first_name, users.last_name, doctors.specialization " +
                "FROM doctors " +
                "INNER JOIN users ON doctors.doctor_id = users.user_id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int doctorId = resultSet.getInt("doctor_id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String specialization = resultSet.getString("specialization");

                    Doctor doctor = new Doctor();
                    doctor.setDoctorId(doctorId);
                    doctor.setFirstName(firstName);
                    doctor.setLastName(lastName);
                    doctor.setSpecialization(specialization);

                    doctors.add(doctor);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return doctors;
    }

    public static ObservableList<Doctor> searchDoctorsByName(String searchString) {
        ObservableList<Doctor> filteredDoctors = FXCollections.observableArrayList();
        String[] parts = searchString.split("\\s+");
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : parts[0];

        String query = "SELECT doctors.doctor_id, users.first_name, users.last_name, doctors.specialization FROM doctors " +
                "INNER JOIN users ON doctors.doctor_id = users.user_id WHERE users.first_name LIKE ? OR users.last_name LIKE ? OR "+
                "users.last_name LIKE ? OR users.first_name LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName + "%");
            preparedStatement.setString(2, lastName + "%");
            preparedStatement.setString(3, firstName + "%");
            preparedStatement.setString(4, lastName + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int doctorId = resultSet.getInt("doctor_id");
                    String doctorFirstName = resultSet.getString("first_name");
                    String doctorLastName = resultSet.getString("last_name");
                    String specialization = resultSet.getString("specialization");

                    Doctor doctor = new Doctor(doctorId, doctorFirstName, doctorLastName, specialization);
                    filteredDoctors.add(doctor);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return filteredDoctors;
    }
    public static int searchDoctorIdByName(String fullName) {
        String[] parts = fullName.split("\\s+");
        String firstName = parts[0];
        String lastName = parts.length>1?parts[1]:parts[0];
        int id=-1;
        int isUnique=0;

        String query = "SELECT doctors.doctor_id FROM doctors " +
                "INNER JOIN users ON doctors.doctor_id = users.user_id WHERE users.first_name = ? AND users.last_name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    id= resultSet.getInt("doctor_id");
                    isUnique++;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
        if(isUnique==1)
            return id;
        else
            return -1;
    }

    public static ObservableList<Doctor> searchDoctorsByDepartment(String department) {
        ObservableList<Doctor> doctors = FXCollections.observableArrayList();

        String query = "SELECT doctors.doctor_id, users.first_name, users.last_name, doctors.specialization FROM doctors " +
                "INNER JOIN users ON doctors.doctor_id = users.user_id WHERE doctors.specialization LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + Character.toUpperCase(department.charAt(0))+department.substring(1).toLowerCase() + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int doctorId = resultSet.getInt("doctor_id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String specialization = resultSet.getString("specialization");

                    Doctor doctor = new Doctor(doctorId,firstName, lastName, specialization);
                    doctor.setDoctorId(doctorId);
                    doctors.add(doctor);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return doctors;
    }
    public static ObservableList<String> searchDoctorsBySplitName(String searchString) {
        ObservableList<String> filteredDoctors = FXCollections.observableArrayList();
        String[] parts = searchString.split("\\s+");
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : parts[0];

        String query = "SELECT doctors.doctor_id, users.first_name, users.last_name, doctors.specialization FROM doctors " +
                "INNER JOIN users ON doctors.doctor_id = users.user_id WHERE users.first_name LIKE ? OR users.last_name LIKE ? OR "+
                "users.last_name LIKE ? OR users.first_name LIKE ?";;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName + "%");
            preparedStatement.setString(2, lastName + "%");
            preparedStatement.setString(3, firstName + "%");
            preparedStatement.setString(4, lastName + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String doctorFirstName = resultSet.getString("first_name");
                    String doctorLastName = resultSet.getString("last_name");
                    filteredDoctors.add(doctorFirstName + " " + doctorLastName);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return filteredDoctors;
    }
    public static ObservableList<String> getAllDoctorNames() {
        ObservableList<String> filteredDoctors = FXCollections.observableArrayList();

        String query = "SELECT doctors.doctor_id, users.first_name, users.last_name, doctors.specialization FROM doctors " +
                "INNER JOIN users ON doctors.doctor_id = users.user_id";;


        PreparedStatement preparedStatement= null;
        try {
            preparedStatement = connection.prepareStatement(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String doctorFirstName = resultSet.getString("first_name");
                    String doctorLastName = resultSet.getString("last_name");
                    filteredDoctors.add(doctorFirstName + " " + doctorLastName);
                }
            }
         catch (SQLException e) {
             System.err.println(e.getMessage());
         }
        return filteredDoctors;
    }

    public static void addMedicalRecord(int appointmentId, String diagnosis) {
        MedicalRecord.setNextMaxRecordId();

        String insertQuery = "INSERT INTO medical_records (record_id, appointment_id, diagnosis) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, MedicalRecord.maxRecordId);
            preparedStatement.setInt(2, appointmentId);
            preparedStatement.setString(3, diagnosis);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    public static void addPrescriptionMedicine(int recordId, String medicine, String dosage, String frequency) {
        String insertPrescriptionMedicineQuery = "INSERT INTO prescriptions_medicines (prescription_id, medicine, dosage, frequency, record_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement2 = connection.prepareStatement(insertPrescriptionMedicineQuery)) {
            preparedStatement2.setInt(1, Prescription.getMaxPrescriptionId()+1);
            preparedStatement2.setString(2, medicine);
            preparedStatement2.setString(3, dosage);
            preparedStatement2.setString(4, frequency);
            preparedStatement2.setInt(5, recordId);
            preparedStatement2.executeUpdate();
        }
     catch (SQLException e) {
        System.err.println(e.getMessage());
    }
    }

}
