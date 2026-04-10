package com.example.groupactivitycapstone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminDashboard extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-dashboard.fxml"));
        Scene scene = new Scene(loader.load(), 500, 500);

        stage.setTitle("Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}