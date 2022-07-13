package fr.polytech.sircus.controller.PopUps;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginPopup {

    @FXML
    private TextField userName;
    @FXML
    private TextField password;

    public LoginPopup() {}

    private boolean isSuperAdminUsername() {
        return userName.getText().equals("sudo");
    }

    private boolean isSuperAdminPassword() {
        return password.getText().equals("password");
    }

    private boolean isAdminUsername() {
        return userName.getText().equals("admin");
    }

    private boolean isAdminPassword() {
        return password.getText().equals("password");
    }

    public boolean isSuperAdmin() {
        return isSuperAdminUsername() && isSuperAdminPassword();
    }

    public boolean isAdmin() {
        return isAdminUsername() && isAdminPassword();
    }
}
