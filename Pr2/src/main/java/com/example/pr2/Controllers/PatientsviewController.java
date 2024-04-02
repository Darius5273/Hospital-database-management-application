package com.example.pr2.Controllers;

import com.example.pr2.Interfaces.AlertMessage;
import com.example.pr2.Models.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PatientsviewController implements AlertMessage {
    @FXML
    private Button addBtn;
    @FXML
    private Button appointmentsBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private TextField addressTf;

    @FXML
    private DatePicker birth_date;

    @FXML
    private TableColumn<Patient,String> col_address;

    @FXML
    private TableColumn<Patient,String> col_date_of_birth;

    @FXML
    private TableColumn<Patient,String> col_first_name;

    @FXML
    private TableColumn<Patient,String> col_gender;

    @FXML
    private TableColumn<Patient,String> col_id;

    @FXML
    private TableColumn<Patient,String> col_last_name;

    @FXML
    private TableColumn<Patient,String> col_phone;
    @FXML
    private TextField ssnTf;

    @FXML
    private Button deleteBtn;

    @FXML
    private TextField firstNameTf;

    @FXML
    private TextField genderTf;

    @FXML
    private TextField lastNameTf;

    @FXML
    private TextField phoneNumberTf;

    @FXML
    private TableView<Patient> table_patients;

    @FXML
    private Button updateBtn;

    ObservableList<Patient> listP= FXCollections.observableArrayList();
    public void initialize()
    {
        col_first_name.setCellValueFactory( new PropertyValueFactory<Patient,String>("firstName"));
        col_last_name.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));
        col_address.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));
        col_id.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        col_date_of_birth.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateOfBirth()));
        col_gender.setCellValueFactory(data->new SimpleStringProperty(data.getValue().getGender()));
        col_phone.setCellValueFactory(data->new SimpleStringProperty(data.getValue().getPhoneNumber()));
        listP=Patient.getAllPatients();
        table_patients.setItems(listP);


        table_patients.setOnMouseClicked(event -> {
        if (event.getClickCount() == 1) {
            handlePatientSelection();
        }
    });
        table_patients.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handlePatientSelection();
            }
        });
    }

    private void handlePatientSelection()
    {
        Patient selectedPatient = table_patients.getSelectionModel().getSelectedItem();
        ssnTf.setText(selectedPatient.getId());
        firstNameTf.setText(selectedPatient.getFirstName());
        lastNameTf.setText(selectedPatient.getLastName());
        genderTf.setText(selectedPatient.getGender());
        phoneNumberTf.setText(selectedPatient.getPhoneNumber());
        addressTf.setText(selectedPatient.getAddress());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateOfBirthString = selectedPatient.getDateOfBirth();
        LocalDate dateOfBirth = LocalDate.parse(dateOfBirthString, dateFormatter);
        birth_date.setValue(dateOfBirth);
    }
    @FXML
    private void handleAddButtonClick() {
        if(!areFieldsFilled()) return;
        String ssn=ssnTf.getText();
        String firstName = firstNameTf.getText();
        String lastName = lastNameTf.getText();
        String dateOfBirth = birth_date.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String gender = genderTf.getText();
        String phoneNumber = phoneNumberTf.getText();
        String address = addressTf.getText();

        Patient newPatient = new Patient(ssn, firstName, lastName, dateOfBirth, gender, phoneNumber, address);
        if (newPatient.addPatient()) {
            listP.add(newPatient);
            table_patients.setItems(listP);
            clearFields();
        }
    }

    @FXML
    private void handleUpdateButtonClick() {
        if(!areFieldsFilled()) return;
        Patient selectedPatient = table_patients.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {

            String ssn=ssnTf.getText();
            String firstName = firstNameTf.getText();
            String lastName = lastNameTf.getText();
            String dateOfBirth = birth_date.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String gender = genderTf.getText();
            String phoneNumber = phoneNumberTf.getText();
            String address = addressTf.getText();

            boolean isSSNChanged = ! selectedPatient.getId().equals(ssn);
            boolean isFirstNameChanged = !selectedPatient.getFirstName().equals(firstName);
            boolean isGenderChanged = !selectedPatient.getGender().equals(gender);
            boolean isLastNameChanged = !selectedPatient.getLastName().equals(lastName);
            boolean isPhoneNumberChanged= !selectedPatient.getPhoneNumber().equals(phoneNumber);

            selectedPatient.setId(ssn);
            selectedPatient.setFirstName(firstName);
            selectedPatient.setLastName(lastName);
            selectedPatient.setDateOfBirth(dateOfBirth);
            selectedPatient.setGender(gender);
            selectedPatient.setPhoneNumber(phoneNumber);
            selectedPatient.setAddress(address);

            boolean isValidUpdate = true;
            if(isSSNChanged)
            {
                if(!Patient.isValidSSN(ssn))
                {
                    openAlert("Invalid SSN!");
                    isValidUpdate=false;
                }
            }
            if (isFirstNameChanged||isLastNameChanged) {
                if (!Patient.isUniqueNameCombination(firstName, lastName)) {
                    openAlert("Name combination already exists!");
                    isValidUpdate = false;
                }
                if(!Patient.isValidNameFormat(firstName,lastName))
                {
                    openAlert("Invalid name format! First and last name should contain only characters and start with an uppercase letter.");
                    isValidUpdate = false;
                }
            }
            if (isGenderChanged) {
                if (!Patient.isValidGender(gender)) {
                    openAlert("Gender should be male or female!");
                    isValidUpdate = false;
                }
            }
            if(isPhoneNumberChanged)
            {
                if(!Patient.isValidPhoneNumber(phoneNumber)) {
                    openAlert("Phone number should contain only digits");
                    isValidUpdate = false;
                }
            }

            if (isValidUpdate && selectedPatient.updatePatient(selectedPatient.getId())) {
                refreshTable();
                clearFields();
            }
        }
    }

    @FXML
    private void handleDeleteButtonClick() {
        Patient selectedPatient = table_patients.getSelectionModel().getSelectedItem();

        if (selectedPatient != null) {
            String patientId = selectedPatient.getId();
            if(!selectedPatient.hasReferences(patientId))
                if (selectedPatient.deletePatient(patientId)) {
                    listP.remove(selectedPatient);
                    table_patients.setItems(listP);
                } else {
                openAlert("No patient was deleted");
            }
            else
                openAlert("The patient has used hospital's services");
        }
    }

    @FXML
    private void clearFields() {
        ssnTf.clear();
        firstNameTf.clear();
        lastNameTf.clear();
        birth_date.setValue(null);
        genderTf.setText("");
        phoneNumberTf.clear();
        addressTf.clear();
    }


    private void refreshTable() {
        listP.clear();
        listP.addAll(Patient.getAllPatients());
        table_patients.setItems(listP);
    }
    public void openAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean areFieldsFilled() {
        boolean areFilled = true;

        if (ssnTf.getText().isEmpty()||firstNameTf.getText().isEmpty() || lastNameTf.getText().isEmpty() || birth_date.getValue() == null ||
                genderTf.getText().isEmpty() || phoneNumberTf.getText().isEmpty() || addressTf.getText().isEmpty()) {
            areFilled = false;
            openAlert("Please fill in all fields.");
        }

        return areFilled;
    }


}
