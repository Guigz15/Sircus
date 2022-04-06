package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.AddLocationPopup;
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
        ObservableList<Location> locationsList = FXCollections.observableArrayList(SircusApplication.dataSircus.getLocationsList());
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
            System.out.println("None of gender selected");
        }

        if (birthDate.getValue() != null)
            birthDateChecked = true;

        if (!name.getText().isEmpty())
            nameChecked = true;

        if (!location.getSelectionModel().isEmpty())
            locationChecked = true;


        if (genderChecked && birthDateChecked && nameChecked && locationChecked) {
            metaSeqTab.setDisable(false);
            resultTab.setDisable(false);
        } else {
            Platform.runLater(() -> {
                Alert dialog = new Alert(Alert.AlertType.ERROR, "Tous les champs obligatoires (*) n'ont pas été remplis correctement.", ButtonType.OK);
                dialog.show();
            });
        }
    }

    @FXML
    private void addLocation() {
        new AddLocationPopup(this.locationAdd.getScene().getWindow());
    }
}
