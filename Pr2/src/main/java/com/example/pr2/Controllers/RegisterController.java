package com.example.pr2.Controllers;
import com.example.pr2.Models.Doctor;
import com.example.pr2.Interfaces.AlertMessage;

import com.example.pr2.Models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController implements AlertMessage {

    @FXML
    private Button back_to_login_btn;

    @FXML
    private TextField conf_passtf;

    @FXML
    private TextField first_nametf;

    @FXML
    private TextField last_nametf;

    @FXML
    private TextField passtf;

    @FXML
    private Button registerBtn;

    @FXML
    private TextField specialisttf;

    @FXML
    private ComboBox<String> typeCb;

    @FXML
    private TextField usernametf;
    @FXML
    private Label specialistLb;
    @FXML
    private void initialize()
    {
        typeCb.getItems().addAll("admin", "receptionist", "doctor");
        typeCb.setOnAction(event -> {
            String selectedItem = typeCb.getSelectionModel().getSelectedItem();

            if ("doctor".equals(selectedItem)) {
                specialistLb.setVisible(true);
                specialisttf.setVisible(true);
            }
            else
            {
                specialistLb.setVisible(false);
                specialisttf.setVisible(false);
            }
        });
        registerBtn.setOnAction(event -> onClick());
        back_to_login_btn.setOnAction(event -> loadLoginScene());
    }
    public void onClick()
    {
        String username = usernametf.getText();
        String password = passtf.getText();
        String confirmPassword = conf_passtf.getText();
        String firstName = first_nametf.getText();
        String lastName=last_nametf.getText();
        String specialist = specialisttf.getText();
        String type=typeCb.getValue();

        if (username.isEmpty() || lastName.isEmpty()|| password.isEmpty() || confirmPassword.isEmpty() || firstName.isEmpty()
                ||type==null||(specialist.isEmpty() && type.equals("doctor"))) {
            openAlert("All fields should be completed.");
        } else if (!firstName.matches("[A-Z][a-z]*(-[A-Z][a-z]*)?")||!lastName.matches("[A-Z][a-z]*(-[A-Z][a-z]*)?")) {
            openAlert("First name and last name should contain letters and start with an uppercase letter.");
        }
        else if (!password.equals(confirmPassword)) {
            openAlert("Password and confirm password do not match.");
        } else {
            if(type.equals("doctor"))
            {
                var user=new User(firstName,lastName,username,password,type);
                user.addUser();
                int doctorId=user.getUserId(username);
                var doctor=new Doctor(doctorId,specialist);
                doctor.addDoctor();
            }
            else {
                var user=new User(firstName,lastName,username,password,type);
                if(!user.addUser())
                    openAlert("User was not registered!");
            }
            openAlert("User registered successfully.");
        }
    }
    public void openAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    double x,y;
    private void loadLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pr2/login.fxml"));

            Stage stage = (Stage) back_to_login_btn.getScene().getWindow();
            var scene=new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Login");

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

}
