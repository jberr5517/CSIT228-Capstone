package com.example.groupactivitycapstone;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private Label lblMessage;

    @FXML
    private void handleLogin() {
        String username = tfUsername.getText();
        String password = pfPassword.getText();

        if (username.equals("admin") && password.equals("1234")) {
            lblMessage.setText("Login Successful!");
        } else {
            lblMessage.setText("Invalid Username or Password");
        }
    }
}