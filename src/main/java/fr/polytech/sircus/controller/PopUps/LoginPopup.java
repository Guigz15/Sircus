package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginPopup {

    @FXML
    private TextField userName;
    @FXML
    private TextField password;

    public LoginPopup() {}

    public boolean isSuperAdmin() {
        return SircusApplication.dataSircus.getSuperAdmins().containsKey(userName.getText())
                && SircusApplication.dataSircus.getSuperAdmins().containsValue(password.getText());
    }

    public boolean isAdmin() {
        return SircusApplication.dataSircus.getAdmins().containsKey(userName.getText())
                && SircusApplication.dataSircus.getAdmins().containsValue(password.getText());
    }
}
