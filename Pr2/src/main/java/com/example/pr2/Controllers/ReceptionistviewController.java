package com.example.pr2.Controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ReceptionistviewController {
    @FXML
    private BorderPane main_border;
    @FXML
    private Button logoutBtn;

    double x,y=0;
    @FXML
    private void handleLogoutButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pr2/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
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



    @FXML
    private void showPane1() {
        switchPane(loadAnchorPane("/com/example/pr2/patients-view.fxml"));
    }

    @FXML
    private void showPane2() {
        switchPane(loadAnchorPane("/com/example/pr2/appointments-view.fxml"));
    }
    @FXML
    private void showPane3() {
        switchPane(loadAnchorPane("/com/example/pr2/addmision_services-view.fxml"));
    }

    private AnchorPane loadAnchorPane(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            return loader.load();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private void switchPane(AnchorPane newPane) {
        if (newPane != null) {
            main_border.setCenter(newPane);
        }
    }
}
