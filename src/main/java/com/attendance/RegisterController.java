package com.attendance;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField     tfUsername;
    @FXML private TextField     tfFullName;
    @FXML private PasswordField tfPassword;
    @FXML private PasswordField tfConfirm;
    @FXML private ComboBox<String> cbRole;
    @FXML private Label         lblError;
    @FXML private Label         lblSuccess;

    @FXML
    private void initialize() {
        cbRole.getItems().setAll("Admin", "Student");
        cbRole.setValue("Admin");
    }

    @FXML
    private void handleRegister() {
        lblError.setText("");
        lblSuccess.setText("");

        String username = tfUsername.getText().trim();
        String fullName = tfFullName.getText().trim();
        String password = tfPassword.getText();
        String confirm  = tfConfirm.getText();
        String role     = cbRole.getValue();

        if (username.isBlank() || fullName.isBlank()
                || password.isBlank() || confirm.isBlank()) {
            showError("All fields are required.");
            return;
        }
        if (role == null || role.isBlank()) {
            showError("Please select a role.");
            return;
        }
        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            tfConfirm.clear();
            return;
        }
        if (UserStore.exists(username)) {
            showError("Username \"" + username + "\" is already taken.");
            return;
        }

        boolean ok = UserStore.register(
                username, fullName, password, role.toLowerCase(), "N/A");

        if (ok) {
            lblSuccess.setText("Account created! You can now log in.");
            tfUsername.clear();
            tfFullName.clear();
            tfPassword.clear();
            tfConfirm.clear();
            cbRole.setValue(null);
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Main.showLogin();
        } catch (Exception e) {
            showError("Could not return to login.");
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblSuccess.setText("");
    }
}
