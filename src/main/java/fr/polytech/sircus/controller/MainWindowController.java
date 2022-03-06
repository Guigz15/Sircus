package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.Location;
import fr.polytech.sircus.model.Method;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

/**
 * This class manages the main window (information tab)
 */
public class MainWindowController implements Initializable {

    @FXML
    private Tab informationTab;
    @FXML
    private Tab metaSeqTab;
    @FXML
    private Tab resultTab;

    @FXML
    private TextField id;
    @FXML
    private ToggleGroup gender;
    @FXML
    private DatePicker birthDate;
    @FXML
    private TextField age;
    @FXML
    private TextField ocularDom;
    @FXML
    private ToggleGroup type;
    @FXML
    private TextField name;
    @FXML
    private Button locationAdd;
    @FXML
    private Button methodAdd;
    @FXML
    private TextField lateral;
    @FXML
    private ComboBox<Location> location;
    @FXML
    private ComboBox<Method> method;


    public MainWindowController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Location Combobox
        ObservableList<Location> locationsList = FXCollections.observableArrayList();
        location.setItems(locationsList);

        // Method Combobox
        ObservableList<Method> methodsList = FXCollections.observableArrayList();
        method.setItems(methodsList);
    }

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
        boolean locationChecked = false;

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

        if (location.getSelectionModel().isEmpty()) {
            Platform.runLater(() -> {
                Alert dialog = new Alert(Alert.AlertType.ERROR, "Vous devez renseigner le lieu de diagnostic pour passer à la suite.", ButtonType.OK);
                dialog.show();
            });
        } else {
            locationChecked = true;
        }

        if (genderChecked && birthDateChecked && nameChecked && locationChecked) {
            metaSeqTab.setDisable(false);
            resultTab.setDisable(false);
        }
    }
}
