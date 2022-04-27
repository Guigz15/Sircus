package fr.polytech.sircus.controller.PopUps;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginPopup {

    @FXML
    private TextField userName;
    @FXML
    private TextField password;

    public LoginPopup() {

    }

    public boolean checkUserName() {
        return userName.getText().equals("root");
    }

    public boolean checkPassword() {
        return password.getText().equals("password");
    }
}
