package com.example.pr2.Controllers;

import com.example.pr2.Interfaces.AlertMessage;
import com.example.pr2.Models.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdmissionServicesviewController implements AlertMessage {

    @FXML
    private DatePicker ad_date_picker;

    @FXML
    private Button addBtn;

    @FXML
    private Button addBtnS;

    @FXML
    private TextField adIdTf;

    @FXML
    private Button clearBtn;

    @FXML
    private TableColumn<Admission, String> col_ad_date1;

    @FXML
    private TableColumn<Admission, String> col_ad_date2;

    @FXML
    private TableColumn<Admission, String> col_ad_disease;

    @FXML
    private TableColumn<Admission, String> col_ad_doc;

    @FXML
    private TableColumn<Admission, Integer> col_ad_id;

    @FXML
    private TableColumn<Admission, String> col_ad_pat;

    @FXML
    private TableColumn<Admission, Integer> col_ad_room;

    @FXML
    private TableColumn<Room, Integer> col_room_charge;

    @FXML
    private TableColumn<Room, Integer> col_room_id;

    @FXML
    private TableColumn<Room, String> col_room_type;

    @FXML
    private TableColumn<Service, Integer> col_ser_charge;

    @FXML
    private TableColumn<Service, String> col_ser_name;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button deleteBtnS;

    @FXML
    private DatePicker dis_date_picker;

    @FXML
    private TextField diseaseTf;

    @FXML
    private ComboBox<String> doctorComboBox;

    @FXML
    private ComboBox<String> patientComboBox;

    @FXML
    private TextField priceTf;

    @FXML
    private Button resetBtn;

    @FXML
    private ComboBox<Integer> roomCb;

    @FXML
    private Button searchRoomBtn;

    @FXML
    private TextField search_roomTf;

    @FXML
    private ComboBox<String> serviceCb;

    @FXML
    private TableView<Admission> table_admissions;

    @FXML
    private TableView<Room> table_room;

    @FXML
    private TableView<Service> table_services;

    @FXML
    private Button updateBtn;

    @FXML
    private Button updateBtnS;
    ObservableList<Admission> listA= FXCollections.observableArrayList();
    ObservableList<Room> listR= FXCollections.observableArrayList();
    private boolean isDListenerActive=true;
    private boolean isPListenerActive=true;
    private boolean isSListenerActive=true;




    public void initialize() {

        populateComboBoxWithAllPatients();
        populateComboBoxWithAllDoctors();

        resetRoomCb();

        populateComboBoxWithAllServices();

        ChangeListener<String> serviceCbListener = (observable, oldValue, newValue) -> {
            if (isSListenerActive)
            {
                if(newValue==null||newValue.isEmpty()) {
                    populateComboBoxWithAllServices();
            } else {
                    updateComboBoxWithFilteredServices(newValue);
            }
            }
        };

        ChangeListener<String> doctorCbListener = (observable, oldValue, newValue) -> {
            if (isDListenerActive) {
                if (newValue == null || newValue.isEmpty()) {
                    populateComboBoxWithAllDoctors();
                } else {
                    updateComboBoxWithFilteredDoctors(newValue);
                }
            }
        };

        doctorComboBox.getEditor().textProperty().addListener(doctorCbListener);

        ChangeListener<String> patientCbListener = (observable, oldValue, newValue) -> {
            if (isPListenerActive) {
                if (newValue == null || newValue.isEmpty()) {
                    populateComboBoxWithAllPatients();
                } else {
                    updateComboBoxWithFilteredPatients(newValue);
                }
            }
        };

        patientComboBox.getEditor().textProperty().addListener(patientCbListener);

        serviceCb.getEditor().textProperty().addListener(serviceCbListener);

        col_ad_date1.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAdmissionDate()));
        col_ad_id.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        col_ad_date2.setCellValueFactory(data-> new SimpleStringProperty(data.getValue().getDischargeDate()));
        col_ad_disease.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisease()));
        col_ad_doc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDoctor()));
        col_ad_pat.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatient()));
        col_ad_room.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRoomId()).asObject());

        col_room_type.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        col_room_id.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRoomId()).asObject());
        col_room_charge.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getChargesPerDay()).asObject());

        col_ser_charge.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCharge()).asObject());
        col_ser_name.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        listA=Admission.getAllAdmissions();
        table_admissions.setItems(listA);
        listR=Room.getRoomsByType(null);
        table_room.setItems(listR);


        table_admissions.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleAdmissionsSelection();
            }
        });

        table_admissions.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleAdmissionsSelection();
            }
        });

        table_services.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleServicesSelection();
            }
        });
        table_services.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleServicesSelection();
            }
        });

        ad_date_picker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });

        dis_date_picker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });
    }

    private void handleAdmissionsSelection()
    {
        Admission selectedAdmission = table_admissions.getSelectionModel().getSelectedItem();
        if (selectedAdmission != null) {

            patientComboBox.getEditor().setText(selectedAdmission.getPatient());

            doctorComboBox.getEditor().setText(selectedAdmission.getDoctor());

            roomCb.setValue(selectedAdmission.getRoomId());

            diseaseTf.setText(selectedAdmission.getDisease());
            ad_date_picker.setValue(LocalDate.parse(selectedAdmission.getAdmissionDate()));
            if(selectedAdmission.getDischargeDate()!=null)
                dis_date_picker.setValue(LocalDate.parse(selectedAdmission.getDischargeDate()));
            else
                dis_date_picker.setValue(null);
            adIdTf.setText(String.valueOf(selectedAdmission.getId()));
            ObservableList<Service> list=Service.getServiceDetailsByAdmissionId(selectedAdmission.getId());
            table_services.setItems(list);
            priceTf.setText(String.valueOf(selectedAdmission.getMedicalBill()));
            ObservableList<Room> room = FXCollections.observableArrayList();
            room.add(Room.getRoomsById(selectedAdmission.getRoomId()));
            table_room.setItems(room);
        }
    }
    private void handleServicesSelection()
    {
        Service selectedService = table_services.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
            isSListenerActive=false;
            serviceCb.getEditor().setText(selectedService.getName());
            isSListenerActive=true;
        }
    }
    @FXML
    private void handleAddAdmissionClick() {
        if (!areFieldsFilled()) {
            return;
        }
        String admissionDischarge,admissionDate;
        if(dis_date_picker.getValue()==null)
            admissionDischarge = null;
        else
            admissionDischarge = dis_date_picker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        admissionDate = ad_date_picker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String admissionDisease = diseaseTf.getText();
        String selectedPatient =  patientComboBox.getValue();
        String selectedDoctor =  doctorComboBox.getValue();
        int selectedRoom= roomCb.getValue();

        Admission newAdmission = new Admission(selectedRoom,selectedPatient,selectedDoctor,admissionDisease,admissionDate,admissionDischarge);

        if (newAdmission.addAdmission()) {
            priceTf.setText(String.valueOf(newAdmission.getMedicalBill()));
            table_room.setItems(listR);
            refreshAdmissionTable();
        }
        else {
            openAlert("No admission was added");
        }
    }
    @FXML
    private void handleUpdateAdmissionClick() {
        if (!areFieldsFilled()) return;

        Admission selectedAdmission = table_admissions.getSelectionModel().getSelectedItem();
        if (selectedAdmission != null) {
            String admissionDischarge;
            if(dis_date_picker.getValue()!=null)
                admissionDischarge = dis_date_picker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            else
                admissionDischarge = null;

            String admissionDate = ad_date_picker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String admissionDisease = diseaseTf.getText();
            String selectedPatient =  patientComboBox.getValue();
            String selectedDoctor =  doctorComboBox.getValue();
            int selectedRoom= roomCb.getValue();

            if(!Admission.isAdmissionBeforeDischarge(admissionDate,admissionDischarge))
            {
                openAlert("Discharge date is before admission date!");
                return;
            }

            if(selectedRoom!=selectedAdmission.getRoomId())
            {
                ObservableList<Room> rooms=Room.getRoomsByTime(admissionDate,admissionDischarge);
                boolean ok=false;
                for (Room r : rooms) {
                    if(r.getRoomId()==selectedRoom) {
                        ok=true;
                    }
                }
                if(!ok)
                {
                    openAlert("Room is not available!");
                }
            }

            selectedAdmission.setAdmissionDate(admissionDate);
            selectedAdmission.setDoctor(selectedDoctor);
            selectedAdmission.setPatient(selectedPatient);
            selectedAdmission.setRoomId(selectedRoom);
            selectedAdmission.setDisease(admissionDisease);
            selectedAdmission.setDischargeDate(admissionDischarge);


            if (selectedAdmission.updateAdmission(selectedAdmission.getId())) {
                priceTf.setText(String.valueOf(Admission.updateMedicalBill(selectedAdmission.getId())));
                refreshAdmissionTable();
            }
        }
    }
    @FXML
    private void handleDeleteAdmissionClick() {
        Admission selectedAdmission = table_admissions.getSelectionModel().getSelectedItem();

        if (selectedAdmission != null) {
            int admissionId = selectedAdmission.getId();
              if(!selectedAdmission.hasReferences(admissionId)) {
                  if (selectedAdmission.deleteAdmission(admissionId)) {
                      table_room.setItems(listR);
                      listA.remove(selectedAdmission);
                      table_admissions.setItems(listA);
                      priceTf.clear();
                  } else {
                      openAlert("No admission was deleted");
                  }
              }
              else {
                  openAlert("Cannot delete the admission because it has services!");
              }
            }

    }
    @FXML
    private void handleAddServiceClick() {
        if (serviceCb.getValue()==null) {
            openAlert("No service provided");
            return;
        }
        if(adIdTf.getText()==null) {
            openAlert("No admission selected");
            return;
        }
        int adId=Integer.parseInt(adIdTf.getText());
        if (Service.addAdmissionService(adId,serviceCb.getValue())) {
            openAlert("Admission service added successfully!");
            priceTf.setText(String.valueOf(Admission.updateMedicalBill(adId)));
            refreshAdmissionTable();
            ObservableList<Service> list=Service.getServiceDetailsByAdmissionId(adId);
            table_services.setItems(list);
            serviceCb.getEditor().clear();
        }
        else {
            openAlert("No admission service was added");
        }
    }
    @FXML
    private void handleUpdateServiceClick() {
        if (serviceCb.getValue()==null||serviceCb.getValue().isEmpty()) {
            openAlert("No service provided");
            return;
        }
        if(adIdTf.getText()==null) {
            openAlert("No admission selected");
            return;
        }
        Service selectedService = table_services.getSelectionModel().getSelectedItem();
        int adId=Integer.parseInt(adIdTf.getText());
        if (selectedService != null) {
            if (Service.updateAdmissionService(adId,selectedService.getName(),serviceCb.getValue())) {
                openAlert("Admission service updated successfully!");
                priceTf.setText(String.valueOf(Admission.updateMedicalBill(adId)));
                refreshAdmissionTable();
                ObservableList<Service> list=Service.getServiceDetailsByAdmissionId(adId);
                table_services.setItems(list);
                serviceCb.getEditor().clear();
            }
            else
                openAlert("Service does not exist.");

        } else {
             openAlert("No service was selected");
        }
    }
    @FXML
    private void handleDeleteServiceClick() {
        Service selectedService = table_services.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
            if(adIdTf.getText()!=null) {
                int adId = Integer.parseInt(adIdTf.getText());
                if (Service.deleteAdmissionService(adId, selectedService.getName())) {
                    openAlert("Admission service deleted successfully!");
                    priceTf.setText(String.valueOf(Admission.updateMedicalBill(adId)));
                    refreshAdmissionTable();
                    ObservableList<Service> list = Service.getServiceDetailsByAdmissionId(adId);
                    table_services.setItems(list);
                    serviceCb.getEditor().clear();
                }
            } else {
                openAlert("No service was deleted");
            }
        }
        else
            openAlert("No service was selected");

    }

    private void populateComboBoxWithAllServices() {
        isSListenerActive=false;
        ObservableList<String> allServiceNames = Service.getAllServiceNames();
        serviceCb.setValue(null);
        serviceCb.setItems(allServiceNames);
        isSListenerActive=true;
    }

    private void updateComboBoxWithFilteredServices(String searchText) {
        isSListenerActive=false;
        ObservableList<String> filteredServices = Service.searchServiceNames(searchText);
        if(!searchText.isEmpty())
            serviceCb.setValue(searchText);
        serviceCb.setItems(filteredServices);
        isSListenerActive=true;
    }
    private void populateComboBoxWithAllPatients() {
        isPListenerActive=false;
        ObservableList<String> allPatients = Patient.getAllPatientNames();
        patientComboBox.setValue(null);
        patientComboBox.setItems(allPatients);
        isPListenerActive=true;
    }

    private void updateComboBoxWithFilteredPatients(String searchText) {
        isPListenerActive=false;
        ObservableList<String> filteredPatients = Patient.searchPatientNames(searchText);
        if(!searchText.isEmpty())
            patientComboBox.setValue(searchText);
        patientComboBox.setItems(filteredPatients);
        isPListenerActive=true;
    }
    private void populateComboBoxWithAllDoctors() {
        isDListenerActive=false;
        ObservableList<String> allDoctors = Doctor.getAllDoctorNames();
        doctorComboBox.setValue(null);
        doctorComboBox.setItems(allDoctors);
        isDListenerActive=true;
    }

    private void updateComboBoxWithFilteredDoctors(String searchText) {
        isDListenerActive=false;
        ObservableList<String> filteredDoctors = Doctor.searchDoctorsBySplitName(searchText);
        if(!searchText.isEmpty())
            doctorComboBox.setValue(searchText);
        doctorComboBox.setItems(filteredDoctors);
        isDListenerActive=true;
    }
    @FXML
    private void search_rooms()
    {
        String searchName=search_roomTf.getText();
        LocalDate admissionLocalDate = ad_date_picker.getValue();
        LocalDate dischargeLocalDate = dis_date_picker.getValue();

        if (admissionLocalDate != null && dischargeLocalDate != null) {
            String admissionDate = ad_date_picker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String dischargeDate = dis_date_picker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if(!searchName.isEmpty())
                table_room.setItems(Room.getRoomsByTypeAndTime(searchName,admissionDate,dischargeDate));
            else
                table_room.setItems(Room.getRoomsByTime(admissionDate,dischargeDate));
        } else if(admissionLocalDate!=null)
            {
                String admissionDate = ad_date_picker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if(!searchName.isEmpty())
                    table_room.setItems(Room.getRoomsByTypeAndTime(searchName,admissionDate,null));
                else
                    table_room.setItems(Room.getRoomsByTime(admissionDate,null));
            }
        else
            table_room.setItems(Room.getRoomsByType(searchName));
        search_roomTf.clear();
    }
    private void resetRoomCb()
    {
        ObservableList<Room> allRooms = Room.getRoomsByType(null);
        ObservableList<Integer> allRoomsIds= FXCollections.observableArrayList();
        for (Room p : allRooms) {
            allRoomsIds.add(p.getRoomId());
        }
        roomCb.setItems(allRoomsIds);
    }
    @FXML
    private void reset_rooms()
    {
        table_room.setItems(listR);
    }
    @FXML
    private void clearFields() {

        dis_date_picker.setValue(null);
        ad_date_picker.setValue(null);
        diseaseTf.clear();
        doctorComboBox.valueProperty().setValue("");
        patientComboBox.valueProperty().setValue("");
        roomCb.getSelectionModel().clearSelection();
        adIdTf.clear();
        priceTf.clear();
        serviceCb.getEditor().setText("");
        reset_rooms();
        table_services.getItems().clear();
    }
    private void refreshAdmissionTable() {
        listA.clear();
        listA.addAll(Admission.getAllAdmissions());
        table_admissions.setItems(listA);
    }
    private boolean areFieldsFilled() {
        boolean areFilled = true;
        String disease = diseaseTf.getText();
        if (ad_date_picker.getValue()==null || disease.isEmpty() || roomCb.getValue()==null ||
                patientComboBox.getValue() == null || doctorComboBox.getValue() == null) {
            areFilled = false;
            openAlert("Please fill in all fields.");
        }
        return areFilled;
    }

    public void openAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
