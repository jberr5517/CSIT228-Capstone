package com.example.groupactivitycapstone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StudentDashboard extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("student-dashboard.fxml"));
        Scene scene = new Scene(loader.load(), 500, 500);

        stage.setTitle("Student Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}