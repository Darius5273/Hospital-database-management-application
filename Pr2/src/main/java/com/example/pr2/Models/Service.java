package com.example.pr2.Models;

import com.example.pr2.Interfaces.AlertMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Service extends DBConnection implements AlertMessage {
    private String name;
    private int charge;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public static ObservableList<String> getAllServiceNames() {
        ObservableList<String> serviceNames = FXCollections.observableArrayList();
        String query = "SELECT service_name FROM services";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String serviceName = resultSet.getString("service_name");
                    serviceNames.add(serviceName);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return serviceNames;
    }

    public static ObservableList<String> searchServiceNames(String searchString) {
        ObservableList<String> filteredServiceNames = FXCollections.observableArrayList();
        String query = "SELECT service_name FROM services WHERE service_name LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + searchString + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String serviceName = resultSet.getString("service_name");
                    filteredServiceNames.add(serviceName);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return filteredServiceNames;
    }
    public static ObservableList<Service> getServiceDetailsByAdmissionId(int admissionId) {
        ObservableList<Service> admissionServices = FXCollections.observableArrayList();
        String query = "SELECT services.service_name, services.service_charge " +
                "FROM admission_services " +
                "INNER JOIN services ON admission_services.service_id = services.service_id " +
                "WHERE admission_services.admission_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, admissionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String serviceName = resultSet.getString("service_name");
                    int serviceCharge = resultSet.getInt("service_charge");

                    Service service = new Service();
                    service.setName(serviceName);
                    service.setCharge(serviceCharge);

                    admissionServices.add(service);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return admissionServices;
    }
    public static int getServiceIdByName(String serviceName) {
        String query = "SELECT service_id FROM services WHERE service_name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, serviceName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("service_id");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return -1;
    }

    public static boolean addAdmissionService(int admissionId, String serviceName) {
        int serviceId=getServiceIdByName(serviceName);
        if (serviceId==-1) {
            return false;
        }

        String query = "INSERT INTO admission_services (admission_id, service_id) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, admissionId);
            preparedStatement.setInt(2, serviceId);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    public static boolean updateAdmissionService(int admissionId, String oldServiceName, String newServiceName) {
        int oldServiceId = getServiceIdByName(oldServiceName);
        int newServiceId = getServiceIdByName(newServiceName);

        if (newServiceId == -1) {
            return false;
        }

        String query = "UPDATE admission_services SET service_id = ? WHERE admission_services_id=(SELECT admission_services_id " +
                "FROM admission_services WHERE admission_id = ? AND service_id = ? LIMIT 1)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, newServiceId);
            preparedStatement.setInt(2, admissionId);
            preparedStatement.setInt(3, oldServiceId);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    public static boolean deleteAdmissionService(int admissionId, String serviceName) {
        int serviceId = getServiceIdByName(serviceName);
        if (serviceId == -1) {
            return false;
        }

        String query = "DELETE FROM admission_services WHERE admission_services_id=(SELECT admission_services_id "+
                "FROM admission_services WHERE admission_id = ? AND service_id = ? LIMIT 1)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, admissionId);
            preparedStatement.setInt(2, serviceId);

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
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
