package fr.polytech.sircus.controller.PopUps;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.Getter;

/**
 * This class manages the add admin popup
 */
public class AddAdminPopup {
    @FXML
    @Getter
    private TextField userName;
    @FXML
    @Getter
    private PasswordField password;
    @FXML
    @Getter
    private ButtonType add;
    @FXML
    @Getter
    private ButtonType cancel;

    public AddAdminPopup() {}
}
