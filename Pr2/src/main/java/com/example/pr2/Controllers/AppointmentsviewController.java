package com.example.pr2.Controllers;

import com.example.pr2.Models.Appointments;
import com.example.pr2.Models.Doctor;
import com.example.pr2.Interfaces.AlertMessage;
import com.example.pr2.Models.Patient;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AppointmentsviewController implements AlertMessage {

    @FXML
    private TableView<Appointments> table_appointments;

    @FXML
    private TableView<Doctor> table_doctors;

    @FXML
    private TableView<Patient> table_patients;
    @FXML
    private Button addBtn;

    @FXML
    private AnchorPane appointments_AP;

    @FXML
    private Button clearBtn;

    @FXML
    private TableColumn<Patient, String > col_Patient_id;

    @FXML
    private TableColumn<Appointments, String> col_app_date;

    @FXML
    private TableColumn<Appointments, Integer> col_app_docid;

    @FXML
    private TableColumn<Appointments, Integer> col_app_id;

    @FXML
    private TableColumn<Appointments, String> col_app_patid;

    @FXML
    private TableColumn<Appointments, String> col_app_time;

    @FXML
    private TableColumn<Doctor, String> col_doc_fn;

    @FXML
    private TableColumn<Doctor, Integer> col_doc_id;

    @FXML
    private TableColumn<Doctor, String> col_doc_ln;

    @FXML
    private TableColumn<Doctor, String> col_doc_spec;

    @FXML
    private TableColumn<Patient, String> col_pat_fn;

    @FXML
    private TableColumn<Patient, String> col_pat_ln;


    @FXML
    private DatePicker date;

    @FXML
    private Button deleteBtn;

    @FXML
    private ComboBox docotrIdCb;

    @FXML
    private ComboBox patientIdCb;

    @FXML
    private Button searchDocBtn;

    @FXML
    private Button searchPatBtn;

    @FXML
    private TextField search_doctorTf;

    @FXML
    private TextField search_patientTf;

    @FXML
    private TextField search_specialisationTf1;

    @FXML
    private ComboBox timeCb;

    @FXML
    private Button updateBtn;
    @FXML
    private Button reset_pBtn;
    @FXML
    private Button reset_dBtn;

    ObservableList<Appointments> listA= FXCollections.observableArrayList();
    private ObservableList<Doctor> doctors;
    ObservableList<String> unavailableDates=FXCollections.observableArrayList();

    private ObservableList<Patient> patients;

        public void initialize() {

//            date.setDayCellFactory(picker -> new DateCell() {
//                @Override
//                public void updateItem(LocalDate date, boolean empty) {
//                    super.updateItem(date, empty);
//                    setDisable(date.isBefore(LocalDate.now()));
//                }
//            });
            setupDatePicker();

            //Set the dates that are visible according to the doctor ID value
            ChangeListener<Integer> doctorCbListener = (observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        unavailableDates.clear();
                        setupDatePicker();
                        timeCb.setItems(generateTimeSlots());
                    } else {
                        unavailableDates.clear();
                        unavailableDates.setAll(Appointments.getUnavailableDates(newValue));
                        setupDatePicker();
                        if(date.getValue()==null||date.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).isEmpty())
                            timeCb.setItems(generateTimeSlots());
                        else
                        {
                            String appointmentDate = date.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            timeCb.setItems(generateAvailableTimeSlots(newValue,appointmentDate));
                        }
                    }
            };
            docotrIdCb.valueProperty().addListener(doctorCbListener);

            timeCb.setItems(generateTimeSlots());
            ChangeListener<LocalDate> datepickerListener = (observable, oldValue, newValue) -> {
                if (newValue == null||newValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).isEmpty()||docotrIdCb.getValue()==null)
                    timeCb.setItems(generateTimeSlots());
                 else
                    timeCb.setItems(generateAvailableTimeSlots((Integer) docotrIdCb.getValue(),newValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            };
            date.valueProperty().addListener(datepickerListener);

            col_pat_fn.setCellValueFactory( new PropertyValueFactory<Patient,String>("firstName"));
            col_pat_ln.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));
            col_Patient_id.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
            col_doc_fn.setCellValueFactory(data-> new SimpleStringProperty(data.getValue().getFirstName()));
            col_doc_ln.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));
            col_doc_spec.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSpecialization()));
            col_doc_id.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDoctorId()).asObject());
            col_app_date.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAppointmentDate()));

            col_app_time.setCellValueFactory(cellData -> {
                SimpleStringProperty property = new SimpleStringProperty();
                String appointmentTimeString = cellData.getValue().getAppointmentTime();

                if (appointmentTimeString != null) {
                    String formattedTime = appointmentTimeString.substring(0, 5);
                    property.setValue(formattedTime);
                }

                return property;
            });

            col_app_id.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAppointmentId()).asObject());
            col_app_patid.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientId()));
            col_app_docid.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDoctorId()).asObject());

            listA = Appointments.getAllAppointments();
            table_appointments.setItems(listA);

            table_appointments.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    handleAppointmentsSelection();
                }
            });

            table_appointments.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    handleAppointmentsSelection();
                }
            });

            ObservableList<String> patientIds = FXCollections.observableArrayList();

            patients = Patient.getAllPatients();

            for (Patient p : patients) {
                patientIds.add(p.getId());
            }

            patientIdCb.setItems(patientIds);


            ObservableList<Integer> doctorIds = FXCollections.observableArrayList();

            doctors = Doctor.getDoctorsList();

            for (Doctor p : doctors) {
                doctorIds.add(p.getDoctorId());
            }
            docotrIdCb.setItems(doctorIds);
            table_patients.setItems(patients);
            table_doctors.setItems(doctors);
        }

        private void handleAppointmentsSelection()
        {
            Appointments selectedAppointment = table_appointments.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                date.setValue(LocalDate.parse(selectedAppointment.getAppointmentDate()));
                timeCb.setValue((selectedAppointment.getAppointmentTime()).substring(0,5));
                patientIdCb.setValue(selectedAppointment.getPatientId());
                docotrIdCb.setValue(selectedAppointment.getDoctorId());
            }
        }

    @FXML
    private void handleAddAppointmentClick() {
        if (!areFieldsFilled()) {
            return;
        }

        String appointmentDate = date.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String appointmentTime = (String) timeCb.getValue();
        String selectedPatientId = (String) patientIdCb.getValue();
        int selectedDoctorId = (int) docotrIdCb.getValue();

        Appointments newAppointment = new Appointments(selectedPatientId, selectedDoctorId, appointmentDate, appointmentTime);
        if (newAppointment.addAppointment()) {
            listA.add(newAppointment);
            table_appointments.setItems(listA);
            clearFields();
        }
    }
    @FXML
    private void handleUpdateAppointmentClick() {
        if (!areFieldsFilled()) return;

        Appointments selectedAppointment = table_appointments.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            String appointmentDate = date.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String appointmentTime = (String) timeCb.getValue();
            String selectedPatientId = (String) patientIdCb.getValue();
            int selectedDoctorId = (int)docotrIdCb.getValue();


            selectedAppointment.setAppointmentDate(appointmentDate);
            selectedAppointment.setAppointmentTime(appointmentTime);
            selectedAppointment.setPatientId(selectedPatientId);
            selectedAppointment.setDoctorId(selectedDoctorId);


            if (selectedAppointment.updateAppointment(selectedAppointment.getAppointmentId())) {
                refreshAppointmentTable();
                clearFields();
            }
        }
    }
    @FXML
    private void handleDeleteAppointmentClick() {
        Appointments selectedAppointment = table_appointments.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            int appointmentId = selectedAppointment.getAppointmentId();
            if (!selectedAppointment.hasReferences(appointmentId)) {
                if (selectedAppointment.deleteAppointment(appointmentId)) {
                    listA.remove(selectedAppointment);
                    table_appointments.setItems(listA);
                } else {
                    openAlert("No appointment was deleted");
                }
            } else {
                openAlert("The appointment cannot be cancelled!");
            }
        }
    }


    @FXML
    private void clearFields() {

        date.setValue(null);
        timeCb.getSelectionModel().clearSelection();
        docotrIdCb.getSelectionModel().clearSelection();
        patientIdCb.getSelectionModel().clearSelection();

    }
    private void refreshAppointmentTable() {
        listA.clear();
        listA.addAll(Appointments.getAllAppointments());
        table_appointments.setItems(listA);
    }
    private boolean areFieldsFilled() {
        boolean areFilled = true;
        if (date.getValue()==null || timeCb.getValue()==null || patientIdCb.getValue() == null || docotrIdCb.getValue() == null) {
            areFilled = false;
            openAlert("Please fill in all fields.");
        }
        return areFilled;
    }

    @FXML
    private void search_doctors()
    {
        String searchName=search_doctorTf.getText();
        String searchSpec=search_specialisationTf1.getText();
        if(!searchName.isEmpty())
            table_doctors.setItems(Doctor.searchDoctorsByName(searchName));
        if(!searchSpec.isEmpty())
            table_doctors.setItems(Doctor.searchDoctorsByDepartment(searchSpec));
        search_specialisationTf1.clear();
        search_doctorTf.clear();
    }
    @FXML
    private void reset_doctors()
    {
        table_doctors.setItems(doctors);
    }
    @FXML
    private void search_patients()
    {
        if(search_patientTf!=null){
            table_patients.setItems(Patient.searchPatientsByName(search_patientTf.getText()));
            search_patientTf.clear();
        }
    }
    private static  ObservableList<String> generateTimeSlots() {
        ObservableList<String> timeSlots = FXCollections.observableArrayList();
        LocalTime startTime = LocalTime.of(8, 0);

        while (startTime.isBefore(LocalTime.of(18, 0))) {
            timeSlots.add(startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            startTime = startTime.plusMinutes(25);
        }
        return timeSlots;
    }
    private ObservableList<String> generateAvailableTimeSlots(int doctorId, String appointmentDate) {
        ObservableList<String> timeSlots = FXCollections.observableArrayList();
        LocalTime startTime = LocalTime.of(8, 0);

        try {
            while (startTime.isBefore(LocalTime.of(18, 0))) {
                if (Appointments.isTimeSlotAvailable(doctorId, appointmentDate, startTime)) {
                    timeSlots.add(startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
                startTime = startTime.plusMinutes(25);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return timeSlots;
    }
    private void setupDatePicker() {
        date.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
                if (unavailableDates.contains(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                    setDisable(true);
                }
            }
        });
    }
    @FXML
    private void reset_patients()
    {
        table_patients.setItems(patients);
    }

    public void openAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
