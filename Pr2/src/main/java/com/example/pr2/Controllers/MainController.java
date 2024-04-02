package com.example.pr2.Controllers;

import com.example.pr2.Models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private Label welcomeText;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button loginBtn;
    @FXML
    private TextField usernametf;
    @FXML
    private TextField passtf;
    @FXML
    private Label invalid;
    @FXML
    private Button Register;
    @FXML
    protected void onLoginButtonClick()
    {
        var login= new User();
        String username = usernametf.getText();
        String password = passtf.getText();
        if (login.isUserInDatabase(username, password)) {
            invalid.setVisible(false);
            var user=new User();
            user.populateUserAttributes(user.getUserId(username));
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.close();
            if((user.getType()).equals("doctor"))
            {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/pr2/doctor-view.fxml"));
                Scene scene = null;
                try {
                    Parent root = fxmlLoader.load();
                    DoctorviewController doctorViewController = fxmlLoader.getController();
                    doctorViewController.setIdAndInit(user.getUserId(username));
                    scene = new Scene(root);
                } catch (IOException e) {
                    System.err.println(e);
                }
                stage.setTitle("Doctor");
                stage.setScene(scene);
            }
            else if((user.getType()).equals("admin"))
            {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/pr2/admin-view.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
                stage.setTitle("Admin");
                stage.setScene(scene);
            }
            else if((user.getType()).equals("receptionist"))
            {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/pr2/receptionist-view.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
                stage.setTitle("Receptionist");
                stage.setScene(scene);
            }

            stage.show();
        } else {
            invalid.setVisible(true);
        }
    }

    public void cancelButtonOnAction()
    {
        usernametf.clear();
        passtf.clear();
        invalid.setVisible(false);
    }
    public void registerButtonOnAction() throws IOException {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/pr2/register.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Registration");
        stage.setScene(scene);
        stage.show();
    }
}