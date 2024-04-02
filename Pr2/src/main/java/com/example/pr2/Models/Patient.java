package com.example.pr2.Models;

import com.example.pr2.Interfaces.AlertMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient extends DBConnection implements AlertMessage{
    private String id;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String address;

    public Patient(String ssn,String firstName, String lastName, String dateOfBirth, String gender, String phoneNumber, String address) {
        this();
        id=ssn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Patient() {
        super();
        id=null;
        firstName=lastName=dateOfBirth=address=phoneNumber=gender=null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean checkPatient()
    {
        if(!isValidSSN(id)) {
            openAlert("Invalid SSN!");
            return false;
        }
        if (!isValidNameFormat(firstName, lastName)) {
            openAlert("Invalid name format! First and last name should contain only characters and start with an uppercase letter.");
            return false;
        }
        if (!isUniqueNameCombination(firstName, lastName)) {
            openAlert("Name combination already exists!");
            return false;
        }
        if(!isValidGender(gender))
        {
            openAlert("Gender should be male or female!");
            return false;
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            openAlert("Invalid phone number! It should contain only numbers.");
            return false;
        }
        return true;
    }

    public boolean addPatient() {
        if(!checkPatient()) return false;

        String query = "INSERT INTO patients (first_name, last_name, date_of_birth, gender, phone_number, address, patient_id) " +
                "VALUES (?, ?, ?, ?, ?, ?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setDate(3, java.sql.Date.valueOf(dateOfBirth));
            preparedStatement.setString(4, gender);
            preparedStatement.setString(5, phoneNumber);
            preparedStatement.setString(6, address);
            preparedStatement.setString(7,id);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                openAlert("Patient added successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean deletePatient(String patientId) {
        String query = "DELETE FROM patients WHERE patient_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, patientId);

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                openAlert("Patient deleted successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }


    public boolean updatePatient(String patientId) {
        String query = "UPDATE patients SET first_name = ?, last_name = ?, date_of_birth = ?, gender = ?, " +
                "phone_number = ?, address = ? WHERE patient_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setDate(3, java.sql.Date.valueOf(dateOfBirth));
            preparedStatement.setString(4, gender);
            preparedStatement.setString(5, phoneNumber);
            preparedStatement.setString(6, address);
            preparedStatement.setString(7, patientId);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                openAlert("Patient updated successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public static boolean isUniqueNameCombination(String firstName, String lastName) {
        String query = "SELECT * FROM patients WHERE first_name = ? AND last_name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return !resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d+");
    }
    public static boolean isValidGender(String gender) {
        return gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female");
    }

    public boolean hasReferences(String patientId) {
        String appointmentsQuery = "SELECT *  FROM appointments WHERE patient_id = ?";
        String admissionsQuery = "SELECT *  FROM admissions WHERE patient_id = ?";

        try (
                PreparedStatement appointmentsStatement = connection.prepareStatement(appointmentsQuery);
                PreparedStatement admissionsStatement = connection.prepareStatement(admissionsQuery)
        ) {
            appointmentsStatement.setString(1, patientId);
            admissionsStatement.setString(1, patientId);

            try (ResultSet appointmentsResultSet = appointmentsStatement.executeQuery();
                 ResultSet admissionsResultSet = admissionsStatement.executeQuery()) {

                if (appointmentsResultSet.next() || admissionsResultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public static ObservableList<Patient> getAllPatients() {
        ObservableList<Patient> patients = FXCollections.observableArrayList();
        String query = "SELECT * FROM patients";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String patientId = resultSet.getString("patient_id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String dateOfBirth = resultSet.getString("date_of_birth");
                    String gender = resultSet.getString("gender");
                    String phoneNumber = resultSet.getString("phone_number");
                    String address = resultSet.getString("address");

                    Patient patient = new Patient(patientId, firstName, lastName, dateOfBirth, gender, phoneNumber, address);
                    patient.setId(patientId);
                    patients.add(patient);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return patients;
    }
    public static boolean isValidNameFormat(String firstName, String lastName) {
        return firstName.matches("[A-Z][a-z]*(-[A-Z][a-z]*)?") && lastName.matches("[A-Z][a-z]*(-[A-Z][a-z]*)?");
    }
    public static boolean isValidSSN(String ssn)
    {
        String firstThreeDigits = ssn.substring(0, 3);
        return ssn.matches("\\d{3}-\\d{2}-\\d{4}")&&!(firstThreeDigits.equals("000") || (Integer.parseInt(firstThreeDigits) >= 666 && Integer.parseInt(firstThreeDigits) <= 900));
    }

    public static ObservableList<Patient> searchPatientsByName(String searchString) {
        ObservableList<Patient> filteredPatients = FXCollections.observableArrayList();
        String[] parts = searchString.split("\\s+");
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : parts[0];

        String query = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? OR last_name LIKE ? OR first_name LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName + "%");
            preparedStatement.setString(2, lastName + "%");
            preparedStatement.setString(3,firstName+"%");
            preparedStatement.setString(4,lastName+"%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    String patientId = resultSet.getString("patient_id");
                    String patientFirstName = resultSet.getString("first_name");
                    String patientLastName = resultSet.getString("last_name");
                    String dateOfBirth = resultSet.getString("date_of_birth");
                    String gender = resultSet.getString("gender");
                    String phoneNumber = resultSet.getString("phone_number");
                    String address = resultSet.getString("address");

                    Patient patient = new Patient(patientId, patientFirstName, patientLastName, dateOfBirth, gender, phoneNumber, address);
                    patient.setId(patientId);
                    filteredPatients.add(patient);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return filteredPatients;
    }

    public void openAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static ObservableList<String> getAllPatientNames() {
        ObservableList<String> patientNames = FXCollections.observableArrayList();
        String query = "SELECT first_name, last_name FROM patients";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");

                    patientNames.add(firstName + " " + lastName);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return patientNames;
    }
    public static ObservableList<String> searchPatientNames(String searchString) {
        ObservableList<String> patientNames = FXCollections.observableArrayList();
        String[] parts = searchString.split("\\s+");
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : parts[0];

        String query = "SELECT first_name, last_name FROM patients WHERE first_name LIKE ? OR last_name LIKE ? OR last_name LIKE ? OR first_name LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName + "%");
            preparedStatement.setString(2, lastName + "%");
            preparedStatement.setString(3, firstName + "%");
            preparedStatement.setString(4, lastName + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String patientFirstName = resultSet.getString("first_name");
                    String patientLastName = resultSet.getString("last_name");

                    patientNames.add(patientFirstName + " " + patientLastName);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return patientNames;
    }
    public static String getPatientIdFromFullName(String fullName) {
        String[] parts = fullName.split("\\s+");
        String firstName = parts[0];
        String lastName = parts[1];
        String patientId = null;
        int isUnique=0;

        String query = "SELECT patient_id FROM patients WHERE first_name = ? AND last_name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    patientId = resultSet.getString("patient_id");
                    isUnique++;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
        if(isUnique==1)
            return patientId;
        else
            return null;
    }

}

