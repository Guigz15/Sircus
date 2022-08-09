package fr.polytech.sircus.controller.PopUps;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import lombok.Getter;

/**
 * This class manages the modification meta-sequence popup
 */
public class ModifyMetaseqPopup {
    @FXML
    @Getter
    private TextField metasequenceName;
    @FXML
    @Getter
    private ButtonType modify;
    @FXML
    @Getter
    private ButtonType cancel;
}
