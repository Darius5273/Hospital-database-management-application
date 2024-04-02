package com.example.pr2.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminviewController {
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

}
