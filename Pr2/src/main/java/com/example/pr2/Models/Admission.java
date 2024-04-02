package com.example.pr2.Models;

import com.example.pr2.Interfaces.AlertMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Admission extends DBConnection implements AlertMessage {
    private int id,roomId,medicalBill;
    private String patient,doctor,disease,admissionDate,dischargeDate;

    public int getMedicalBill() {
        return medicalBill;
    }

    public void setMedicalBill(int medicalBill) {
        this.medicalBill = medicalBill;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(String dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public Admission(int roomId, String patient, String doctor, String disease, String admissionDate, String dischargeDate) {
        this();
        this.roomId = roomId;
        this.patient = patient;
        this.doctor = doctor;
        this.disease = disease;
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
    }

    public Admission() {
        super();
        this.id = -1;
        this.roomId = -1;
        this.medicalBill=0;
        this.admissionDate = null;
        this.dischargeDate = null;
        this.disease=null;
        this.patient=this.doctor=null;
    }
    public int getMaxAdmissionId() {
        String query = "SELECT MAX(admission_id) AS max_id FROM admissions";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("max_id");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }
    public static boolean isAdmissionBeforeDischarge(String admissionDate, String dischargeDate) {
        if(dischargeDate==null) return true;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate admission = LocalDate.parse(admissionDate, formatter);
        LocalDate discharge = LocalDate.parse(dischargeDate, formatter);

        return admission.isBefore(discharge);
    }
    public boolean addAdmission() {
        String patientId=Patient.getPatientIdFromFullName(patient);
        int doctorId=Doctor.searchDoctorIdByName(doctor);
        if(patientId==null||doctorId==-1) {
            openAlert("Please ensure that both the patient and the doctor selected are valid!");
            return false;
        }
        if(!isAdmissionBeforeDischarge(admissionDate,dischargeDate))
        {
            openAlert("Please ensure that admission date is before discharge date");
            return false;
        }

        //Check if the room is available
        ObservableList<Room> rooms=Room.getRoomsByTime(admissionDate,dischargeDate);
        boolean ok=false;
        for (Room r : rooms) {
            if(r.getRoomId()==roomId) {
                ok=true;
            }
        }
        if(!ok)
        {
            openAlert("Room is not available!");
            return false;
        }

        String query = "INSERT INTO admissions (patient_id, attending_doctor_id, disease, room_id, admission_date, discharge_date,admission_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, patientId);
            preparedStatement.setInt(2, doctorId);
            preparedStatement.setString(3, disease);
            preparedStatement.setInt(4, roomId);
            preparedStatement.setDate(5, java.sql.Date.valueOf(admissionDate));
            if(dischargeDate!=null)
                preparedStatement.setDate(6, java.sql.Date.valueOf(dischargeDate));
            else
                preparedStatement.setNull(6, java.sql.Types.DATE);
            preparedStatement.setInt(7,getMaxAdmissionId()+1);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                id=getMaxAdmissionId();
                this.setMedicalBill(Admission.updateMedicalBill(getMaxAdmissionId())); ;
                openAlert("Admission added successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    public boolean updateAdmission(int admissionId) {
        String patientId=Patient.getPatientIdFromFullName(patient);
        int doctorId=Doctor.searchDoctorIdByName(doctor);
        if(patientId==null||doctorId==-1) {
            openAlert("Please ensure that both the patient and the doctor selected are valid!");
            return false;
        }
        String query = "UPDATE admissions SET patient_id = ?, attending_doctor_id = ?, disease = ?, room_id = ?, admission_date = ?, discharge_date = ? WHERE admission_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, patientId);
            preparedStatement.setInt(2, doctorId);
            preparedStatement.setString(3, disease);
            preparedStatement.setInt(4, roomId);
            preparedStatement.setDate(5, java.sql.Date.valueOf(admissionDate));
            if(dischargeDate!=null)
                preparedStatement.setDate(6, java.sql.Date.valueOf(dischargeDate));
            else
                preparedStatement.setNull(6, java.sql.Types.DATE);
            preparedStatement.setInt(7, admissionId);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                openAlert("Admission updated successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    public boolean deleteAdmission(int admissionId) {
        String query = "DELETE FROM admissions WHERE admission_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, admissionId);

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                openAlert("Admission deleted successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    public boolean hasReferences(int admissionId) {
        String invoiceQuery = "SELECT COUNT(*) AS count FROM admission_services WHERE admission_id = ?";

        try (PreparedStatement invoiceStatement = connection.prepareStatement(invoiceQuery)) {
            invoiceStatement.setInt(1, admissionId);

            try (ResultSet invoiceResultSet = invoiceStatement.executeQuery()) {
                if (invoiceResultSet.next()) {
                    return invoiceResultSet.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public static ObservableList<Admission> getAllAdmissions() {
        ObservableList<Admission> admissions = FXCollections.observableArrayList();
        String query = "SELECT admissions.*, patients.first_name AS patient_first_name, patients.last_name AS patient_last_name, " +
                "users.first_name AS doctor_first_name, users.last_name AS doctor_last_name " +
                "FROM admissions " + "INNER JOIN patients ON admissions.patient_id = patients.patient_id " +
                "INNER JOIN doctors ON admissions.attending_doctor_id = doctors.doctor_id " +
                "INNER JOIN users ON doctors.doctor_id = users.user_id ORDER BY admissions.admission_id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Admission admissionInfo = new Admission();
                    admissionInfo.setId(resultSet.getInt("admission_id"));
                    admissionInfo.setPatient(resultSet.getString("patient_first_name") + " " + resultSet.getString("patient_last_name"));
                    admissionInfo.setDoctor(resultSet.getString("doctor_first_name") + " " + resultSet.getString("doctor_last_name"));
                    admissionInfo.setRoomId(resultSet.getInt("room_id"));
                    admissionInfo.setDisease(resultSet.getString("disease"));
                    admissionInfo.setAdmissionDate(resultSet.getString("admission_date"));
                    admissionInfo.setDischargeDate(resultSet.getString("discharge_date"));
                    admissionInfo.setMedicalBill(resultSet.getInt("medical_bill"));
                    admissions.add(admissionInfo);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return admissions;
    }



    public void openAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static int updateMedicalBill(int admissionId) {
        int totalBill=0;
        int serviceCharges;
        String query = "SELECT discharge_date- admission_date AS days_diff, charges_day " +
                "FROM admissions " +
                "INNER JOIN rooms ON admissions.room_id = rooms.room_id " +
                "WHERE admission_id = ? AND discharge_date IS NOT NULL";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, admissionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int daysDiff = resultSet.getInt("days_diff");
                    int roomCharges = resultSet.getInt("charges_day");

                    serviceCharges = calculateServiceCharges(admissionId);

                    totalBill = (daysDiff * roomCharges) + serviceCharges;

                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        String updateQuery = "UPDATE admissions " +
                "SET medical_bill = ? "+
                "WHERE admission_id = ? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            if(totalBill!=0)
                preparedStatement.setInt(1,totalBill);
            else
                preparedStatement.setNull(1, java.sql.Types.INTEGER);
            preparedStatement.setInt(2, admissionId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
        return totalBill;
    }
    private static int calculateServiceCharges(int admissionId) {
        String serviceQuery = "SELECT SUM(service_charge) AS total_service_charge " +
                "FROM admission_services " +
                "INNER JOIN services ON admission_services.service_id = services.service_id " +
                "WHERE admission_id = ?";

        try (PreparedStatement serviceStatement = connection.prepareStatement(serviceQuery)) {
            serviceStatement.setInt(1, admissionId);

            try (ResultSet serviceResultSet = serviceStatement.executeQuery()) {
                if (serviceResultSet.next()) {
                    return serviceResultSet.getInt("total_service_charge");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }


}
