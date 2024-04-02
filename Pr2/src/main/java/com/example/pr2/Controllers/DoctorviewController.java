package com.example.pr2.Controllers;
import com.example.pr2.Models.Appointments;
import com.example.pr2.Models.Doctor;
import com.example.pr2.Interfaces.AlertMessage;
import com.example.pr2.Models.MedicalRecord;
import com.example.pr2.Models.Prescription;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DoctorviewController implements AlertMessage {
    @FXML
    private Button addMedBtn;

    @FXML
    private TextField appIdTf;

    @FXML
    private Button clearBtn;

    @FXML
    private TableColumn<Appointments, String> col_app_date;

    @FXML
    private TableColumn<Appointments, Integer> col_app_id;

    @FXML
    private TableColumn<Appointments, String> col_app_patid;

    @FXML
    private TableColumn<Appointments, String> col_app_time;

    @FXML
    private TableColumn<MedicalRecord, Integer> col_m_app;

    @FXML
    private TableColumn<MedicalRecord, String> col_m_disease;

    @FXML
    private TableColumn<MedicalRecord, Integer> col_m_rec;

    @FXML
    private TableColumn<Prescription, String> col_p_dos;

    @FXML
    private TableColumn<Prescription, String> col_p_freq;

    @FXML
    private TableColumn<Prescription, String> col_p_med;

    @FXML
    private Button createResBtn;

    @FXML
    private DatePicker date;

    @FXML
    private TextField diseaseTf;

    @FXML
    private TextField dosageTf;

    @FXML
    private TextField freqTf;

    @FXML
    private TextField medicineTf;

    @FXML
    private Button resetDateBtn;

    @FXML
    private Button resetPrescBtn;

    @FXML
    private Button searchDateBtn;

    @FXML
    private Button searchPrescBtn;

    @FXML
    private TextField searchPrescTf;

    @FXML
    private TableView<Appointments> table_appointments;

    @FXML
    private TableView<Prescription> table_medicine;

    @FXML
    private TableView<MedicalRecord> table_results;




    private int doctorId;
    @FXML
    private Button logoutBtn;

    public void setIdAndInit(int doctorId) {
        this.doctorId=doctorId;
        init();
    }
    private ObservableList<Appointments> listA= FXCollections.observableArrayList();
    private ObservableList<MedicalRecord> listM= FXCollections.observableArrayList();
    private ObservableList<Prescription> listP= FXCollections.observableArrayList();


    public void init()
    {
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

        date.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });



        col_app_id.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAppointmentId()).asObject());
        col_app_patid.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientId()));

        listA = Appointments.getAppointmentsByDoctorId(doctorId);
        table_appointments.setItems(listA);

        col_m_app.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAppointment_id()).asObject());
        col_m_disease.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDiagnosis()));
        col_m_rec.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());

        listM = MedicalRecord.getMedicalRecordsByDoctorId(doctorId);
        table_results.setItems(listM);

        col_p_med.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedicine()));
        col_p_dos.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDosage()));
        col_p_freq.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFrequency()));

        table_appointments.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleAppointmentSelection();
            }
        });

        table_appointments.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection!=null)
                handleAppointmentSelection();
        });

        table_results.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection!=null)
                handleRecordSelection();
        });
        table_results.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleRecordSelection();
            }
        });

    }



    private void handleAppointmentSelection()
    {
        Appointments selectedAppointment = table_appointments.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            appIdTf.setText(String.valueOf(selectedAppointment.getAppointmentId()));
            diseaseTf.clear();
            medicineTf.clear();
            dosageTf.clear();
            freqTf.clear();
        }
    }
    private void handleRecordSelection()
    {
        MedicalRecord selectedRecord = table_results.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            appIdTf.setText(String.valueOf(selectedRecord.getAppointment_id()));
            diseaseTf.setText(selectedRecord.getDiagnosis());
            medicineTf.clear();
            dosageTf.clear();
            freqTf.clear();
            table_medicine.setItems(Prescription.getPrescriptionsByRecordId(selectedRecord.getId()));
        }
    }


    @FXML
    void clearFields() {
        appIdTf.clear();
        diseaseTf.clear();
        medicineTf.clear();
        dosageTf.clear();
        freqTf.clear();
    }

    @FXML
    void reset_medicine()
    {
        listP.clear();
        table_medicine.setItems(listP);
    }
    @FXML
    void search_record_med()
    {
        String record = searchPrescTf.getText();
        if(!record.matches("\\d+"))
        {
            openAlert("Only digits please!");
            return;
        }
        listP.clear();
        if(record!=null&&!record.isEmpty())
            listP.setAll(Prescription.getPrescriptionsByRecordId(Integer.parseInt(record)));
        table_medicine.setItems(listP);
        searchPrescTf.clear();
    }

    @FXML
    void search_app_date()
    {
        if(date.getValue()!=null&&!date.getEditor().getText().isEmpty())
        {
            String appDate = date.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            table_appointments.setItems(Appointments.getAppointmentsByDate(appDate));
            table_results.setItems(MedicalRecord.getMedicalRecordsByDate(appDate,doctorId));
        }
        date.setValue(null);
    }

    @FXML
    void reset_app_date()
    {
        table_appointments.setItems(listA);
        table_results.setItems(listM);
    }

    @FXML
    void addMed()
    {
        String appId=appIdTf.getText();
        String disease=diseaseTf.getText();
        String medicine=medicineTf.getText();
        String dosage=dosageTf.getText();
        String frequency=freqTf.getText();
        if(appId.isEmpty()||disease.isEmpty()||medicine.isEmpty()||dosage.isEmpty()||frequency.isEmpty()){
            openAlert("Please fill in all fields!");
            return;
        }
        if(MedicalRecord.getRecordIdByAppointmentAndDisease(Integer.parseInt(appId),disease)==-1)
        {
            openAlert("No existing medical record matches the provided information");
            return;
        }
        Doctor.addPrescriptionMedicine(MedicalRecord.getRecordIdByAppointmentAndDisease(Integer.parseInt(appId),disease),medicine,dosage,frequency);
        listP.clear();
        listP=Prescription.getPrescriptionsByRecordId(MedicalRecord.getRecordIdByAppointmentAndDisease(Integer.parseInt(appId),disease));
        table_medicine.setItems(listP);
    }
    @FXML
    void addResult()
    {
        String appId=appIdTf.getText();
        String disease=diseaseTf.getText();
        String medicine=medicineTf.getText();
        String dosage=dosageTf.getText();
        String frequency=freqTf.getText();
        if(appId.isEmpty()||disease.isEmpty()){
            openAlert("Please fill the appointment ID and the diagnosis fields!");
            return;
        }
        if(!medicine.isEmpty())
            {
                if(dosage.isEmpty()||frequency.isEmpty()){
                    openAlert("Fill the medicine related fields");
                    return;
                }
                Doctor.addMedicalRecord(Integer.parseInt(appId),disease);
                MedicalRecord medicalRecord=new MedicalRecord();
                medicalRecord.setId(MedicalRecord.maxRecordId);
                medicalRecord.setDiagnosis(disease);
                medicalRecord.setAppointment_id(Integer.parseInt(appId));
                listM.add(medicalRecord);
                table_results.setItems(listM);
                 Doctor.addPrescriptionMedicine(MedicalRecord.maxRecordId,medicine,dosage,frequency);
                 listP.clear();
                 listP=Prescription.getPrescriptionsByRecordId(MedicalRecord.maxRecordId);
                 table_medicine.setItems(listP);
            }
        else
        {
            if(MedicalRecord.getRecordIdByAppointmentAndDisease(Integer.parseInt(appId),disease)!=-1)
            {
                openAlert("There is already a medical record with this data!");
                return;
            }
            Doctor.addMedicalRecord(Integer.parseInt(appId),disease);
            MedicalRecord medicalRecord=new MedicalRecord();
            medicalRecord.setId(MedicalRecord.maxRecordId);
            medicalRecord.setDiagnosis(disease);
            medicalRecord.setAppointment_id(Integer.parseInt(appId));
            listM.add(medicalRecord);
            table_results.setItems(listM);
        }
    }

    double x,y=0;
    @FXML
    private void handleLogoutButton() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pr2/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            //make login draggable
            scene.setOnMousePressed(evt ->
            { x = evt.getSceneX(); y = evt.getSceneY(); });
            scene.setOnMouseDragged(evt ->
            {   stage.setX(evt.getScreenX()- x);
                stage.setY(evt.getScreenY()- y);
            });
            stage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void openAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
