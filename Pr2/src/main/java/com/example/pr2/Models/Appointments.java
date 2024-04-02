package com.example.pr2.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import com.example.pr2.Interfaces.AlertMessage;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointments extends DBConnection implements AlertMessage {
    private int appointmentId;
    private String patientId;
    private int doctorId;
    private String appointmentDate;
    private String appointmentTime;

    public Appointments(String patientId, int doctorId, String appointmentDate, String appointmentTime) {
        this();
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }

    public Appointments() {
        super();
        this.appointmentId = -1;
        this.patientId = null;
        this.doctorId = -1;
        this.appointmentDate = null;
        this.appointmentTime = null;
    }
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public boolean addAppointment() {

        String query = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time,appointment_id) " +
                "VALUES (?, ?, ?, ?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, patientId);
            preparedStatement.setInt(2, doctorId);
            preparedStatement.setDate(3, java.sql.Date.valueOf(appointmentDate));
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            java.util.Date parsedDate;
            Time sqlTime = null;

            try {
                parsedDate = sdf.parse(appointmentTime);
                sqlTime = new Time(parsedDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            preparedStatement.setTime(4, sqlTime);
            preparedStatement.setInt(5,getMaxAppointmentId()+1);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                openAlert("Appointment added successfully!");
                appointmentId=getMaxAppointmentId();
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    public boolean updateAppointment(int appointmentId) {
        String query = "UPDATE appointments SET patient_id = ?, doctor_id = ?, appointment_date = ?, " +
                "appointment_time = ? WHERE appointment_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, patientId);
            preparedStatement.setInt(2, doctorId);
            preparedStatement.setDate(3, java.sql.Date.valueOf(appointmentDate));
            preparedStatement.setTime(4, convertToSqlTime(appointmentTime));
            preparedStatement.setInt(5, appointmentId);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                openAlert("Appointment updated successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean deleteAppointment(int appointmentId) {
        String query = "DELETE FROM appointments WHERE appointment_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, appointmentId);

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                openAlert("Appointment deleted successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    public static boolean isValidTimeFormat(String time) {
        String check = "([0-1][0-9]|2[0-3]):[0-5][0-9]";
        return time.matches(check);
    }
    public int getMaxAppointmentId() {
        String query = "SELECT MAX(appointment_id) AS max_id FROM appointments";

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
    public boolean hasReferences(int appointmentId) {
        String Query = "SELECT * FROM medical_records WHERE appointment_id = ?";

        try (
                PreparedStatement patientsStatement = connection.prepareStatement(Query);
        ) {
            patientsStatement.setInt(1, appointmentId);

            try (
                    ResultSet patientsResultSet = patientsStatement.executeQuery();
            ) {
                if (patientsResultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    public static ObservableList<Appointments> getAllAppointments() {
        ObservableList<Appointments> appointments = FXCollections.observableArrayList();
        String query = "SELECT * FROM appointments WHERE appointment_date >= CURRENT_DATE ORDER BY appointment_id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int appointmentId = resultSet.getInt("appointment_id");
                    String patientId = resultSet.getString("patient_id");
                    int doctorId = resultSet.getInt("doctor_id");
                    String appointmentDate = resultSet.getString("appointment_date");
                    String appointmentTime = (resultSet.getString("appointment_time")).substring(0,5);

                    Appointments appointment = new Appointments(patientId, doctorId, appointmentDate, appointmentTime);
                    appointment.setAppointmentId(appointmentId);
                    appointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return appointments;
    }
    public static Time convertToSqlTime(String appointmentTime) {
        String[] timeComponents = appointmentTime.split(":");

        int hours = Integer.parseInt(timeComponents[0]);
        int minutes = Integer.parseInt(timeComponents[1]);

        return Time.valueOf(String.format("%02d:%02d:00", hours, minutes));
    }
    public static ObservableList<Appointments> getAppointmentsByDoctorId(int doctorId) {
        ObservableList<Appointments> appointmentsList = FXCollections.observableArrayList();

        String query = "SELECT * FROM appointments WHERE doctor_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Appointments appointment = new Appointments();
                    appointment.setAppointmentId(resultSet.getInt("appointment_id"));
                    appointment.setPatientId(resultSet.getString("patient_id"));
                    appointment.setDoctorId(resultSet.getInt("doctor_id"));
                    appointment.setAppointmentDate(resultSet.getString("appointment_date"));
                    appointment.setAppointmentTime(resultSet.getString("appointment_time"));

                    appointmentsList.add(appointment);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return appointmentsList;
    }
    public static ObservableList<Appointments> getAppointmentsByDate(String date) {
        ObservableList<Appointments> appointmentsList = FXCollections.observableArrayList();

        String query = "SELECT * FROM appointments WHERE appointment_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, java.sql.Date.valueOf(date));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Appointments appointment = new Appointments();
                    appointment.setAppointmentId(resultSet.getInt("appointment_id"));
                    appointment.setPatientId(resultSet.getString("patient_id"));
                    appointment.setDoctorId(resultSet.getInt("doctor_id"));
                    appointment.setAppointmentDate(resultSet.getString("appointment_date"));
                    appointment.setAppointmentTime(resultSet.getString("appointment_time"));

                    appointmentsList.add(appointment);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return appointmentsList;
    }
    public static ObservableList<String> getUnavailableDates(int doctorId) {
        ObservableList<String> unavailableDates = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT appointment_date FROM appointments WHERE doctor_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String appointmentDate = resultSet.getString("appointment_date");
                    if (areAllTimeSlotsBooked(doctorId, appointmentDate)) {
                        unavailableDates.add(appointmentDate);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return unavailableDates;
    }

    private static boolean areAllTimeSlotsBooked(int doctorId, String appointmentDate) throws SQLException {
        String query = "SELECT COUNT(*) AS total_slots FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setDate(2, Date.valueOf(appointmentDate));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int totalSlots = resultSet.getInt("total_slots");
                    return totalSlots >= 24; //The total number of slots available for a day
                }
            }
        }
        return false;
    }
    public static boolean isTimeSlotAvailable(int doctorId, String appointmentDate, LocalTime time) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM appointments " +
                "WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setDate(2, Date.valueOf(appointmentDate));
            preparedStatement.setTime(3, Time.valueOf(time));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count") == 0;
                }
            }
        }
        return false;
    }




    public void openAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
