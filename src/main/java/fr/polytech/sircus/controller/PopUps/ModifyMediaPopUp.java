package fr.polytech.sircus.controller.PopUps;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import lombok.Getter;

public class ModifyMediaPopUp {
    @FXML
    @Getter
    private CheckBox resizeImage;
    @FXML
    @Getter
    private ColorPicker backgroundColor;
    @FXML
    @Getter
    private ButtonType modify;
    @FXML
    @Getter
    private ButtonType cancel;
}
