package com.attendance;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class    Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Attendance System");
        primaryStage.setResizable(false);
        showLogin();
        primaryStage.show();
    }

    public static void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/attendance/login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    public static void showRegister() throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/attendance/register.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    public static void showDashboard(String username) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/attendance/dashboard.fxml"));
        Scene scene = new Scene(loader.load());
        DashboardController controller = loader.getController();
        controller.initData(username);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
