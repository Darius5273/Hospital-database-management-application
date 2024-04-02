package com.example.pr2;

import java.sql.Connection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {
    double x,y=0;


    public void start(Stage stage)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        stage.setTitle("Hospital database!");
        stage.setScene(scene);
        scene.setOnMousePressed(evt ->
        { x = evt.getSceneX(); y = evt.getSceneY(); });
        scene.setOnMouseDragged(evt ->
        {   stage.setX(evt.getScreenX()- x);
            stage.setY(evt.getScreenY()- y);
        });
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}