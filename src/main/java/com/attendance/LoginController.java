package com.attendance;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;

public class LoginController {

    @FXML private TextField     tfUsername;
    @FXML private PasswordField tfPassword;
    @FXML private Label         lblError;

    @FXML
    private void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = tfPassword.getText();

        if (username.isBlank() || password.isBlank()) {
            showError("Please enter both username and password.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                showError("Cannot connect to database. Is XAMPP running?");
                return;
            }
        } catch (Exception e) {
            showError("DB Error: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        try {
            if (UserStore.login(username, password)) {
                lblError.setText("");
                Main.showDashboard(username);

            } else {
                showError("Invalid username or password.");
                tfPassword.clear();
            }
        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToRegister() {
        try {
            Main.showRegister();
        } catch (Exception e) {
            showError("Could not open registration.");
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
    }
}
