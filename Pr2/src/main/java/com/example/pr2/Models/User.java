package com.example.pr2.Models;
import com.example.pr2.Interfaces.*;
import javafx.scene.control.Alert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User extends DBConnection implements AlertMessage{
    private int userId;
    protected String firstname, lastname, username, password, type;
    public String getFirst_name() {
        return firstname;
    }

    public User(String firstname, String lastname, String username, String password, String type) {
        this();
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public void setFirst_name(String first_name) {
        this.firstname = first_name;
    }

    public String getLast_name() {
        return lastname;
    }

    public void setLast_name(String last_name) {
        this.lastname = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public User() {
        super();
        userId=-1;
        firstname=lastname=password=username=type=null;
    }
    public boolean isUserInDatabase(String username, String password) {
        boolean userExists = false;
        PreparedStatement statement;
        ResultSet resultSet;
        try {
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    userExists = true;
                }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return userExists;
    }
    public boolean isUsernameInDatabase(String username) {
        String query = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    public boolean checkIfNameExists() {
        String query = "SELECT * FROM users WHERE first_name = ? AND last_name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2, lastname);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    public int getMaxUserId() {
        String query = "SELECT MAX(user_id) AS max_id FROM users";

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
    public void populateUserAttributes(int userId) {
        String query = "SELECT first_name, last_name, username, password, type FROM users WHERE user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.userId = userId;
                    setFirst_name(resultSet.getString("first_name"));
                    setLast_name(resultSet.getString("last_name"));
                    setPassword(resultSet.getString("password"));
                    setUsername(resultSet.getString("username"));
                    setType(resultSet.getString("type"));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    public boolean checkUser()
    {
        if (checkIfNameExists()) {
            openAlert("User already exists!");
            return false;
        }
        else if(isUsernameInDatabase(username))
        {
            openAlert("Username already exists!");
            return false;
        }
        return true;
    }
    public boolean addUser() {
        if(!checkUser()) return false;

        String query = "INSERT INTO users (first_name, last_name, username, password, type,user_id) VALUES (?, ?, ?, ?, ?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2, lastname);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, type);
            preparedStatement.setInt(6, getMaxUserId()+1);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    public int getUserId(String username) {
        String query = "SELECT user_id FROM users WHERE username = ? ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }
    public void openAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
