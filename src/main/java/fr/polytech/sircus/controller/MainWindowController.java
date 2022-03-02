package fr.polytech.sircus.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.Period;

/**
 * This class manages the main window (information tab)
 */
public class MainWindowController {

    @FXML
    private Tab informationTab;
    @FXML
    private Tab metaSeqTab;
    @FXML
    private Tab resultTab;

    @FXML
    private TextField id;
    @FXML
    private ToggleGroup gender; //
    @FXML
    private DatePicker birthDate; //
    @FXML
    private TextField age;
    @FXML
    private TextField ocularDom;
    @FXML
    private ToggleGroup type;
    @FXML
    private TextField name; //
    @FXML
    private Button locationAdd;
    @FXML
    private Button methodAdd;
    @FXML
    private TextField lateral;


    public MainWindowController() {}

    @FXML
    private void computeAge() {
        Period period = Period.between(birthDate.getValue(), LocalDate.now());
        age.setText(period.getYears() + "  ans");
    }

    @FXML
    private void checkContent() {
        boolean genderChecked = false;
        boolean birthDateChecked = false;
        boolean nameChecked = false;

        try {
            genderChecked = gender.getSelectedToggle().isSelected();
        } catch (NullPointerException e) {
            Platform.runLater(() -> {
                Alert dialog = new Alert(Alert.AlertType.ERROR, "Vous devez renseigner un genre pour passer à la suite.", ButtonType.OK);
                dialog.show();
            });
        }

        if (birthDate.getValue() == null) {
            Platform.runLater(() -> {
                Alert dialog = new Alert(Alert.AlertType.ERROR, "Vous devez renseigner une date de naissance pour passer à la suite.", ButtonType.OK);
                dialog.show();
            });
        } else {
            birthDateChecked = true;
        }

        if (name.getText().isEmpty()) {
            Platform.runLater(() -> {
                Alert dialog = new Alert(Alert.AlertType.ERROR, "Vous devez renseigner un nom pour passer à la suite.", ButtonType.OK);
                dialog.show();
            });
        } else {
            nameChecked = true;
        }

        if (genderChecked && birthDateChecked && nameChecked) {
            metaSeqTab.setDisable(false);
            resultTab.setDisable(false);
        }
    }
}
