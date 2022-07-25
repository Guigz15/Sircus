package fr.polytech.sircus.controller.PopUps;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.Getter;

public class AddAdminPopup {
    @FXML
    @Getter
    private TextField userName;
    @FXML
    @Getter
    private PasswordField password;

    public AddAdminPopup() {}
}
