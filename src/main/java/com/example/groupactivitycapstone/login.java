package com.example.groupactivitycapstone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class login extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader.load(), 300, 250);

        stage.setTitle("Attendance System");
        stage.setScene(scene);
        stage.show();
    }
}