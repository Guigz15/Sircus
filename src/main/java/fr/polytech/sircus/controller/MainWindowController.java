package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.AddLocationPopup;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Popup;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class manages the main window (information tab)
 */
public class MainWindowController implements Initializable {

    @FXML
    private ComboBox<String> eyeTracker;
    @FXML
    private Button admin;
    @FXML
    private TextField id;
    @FXML
    private ToggleGroup gender;
    @FXML
    private DatePicker birthDate;
    @FXML
    private TextField age;
    @FXML
    private TextField name;
    @FXML
    private TextField forename;
    @FXML
    private Button locationAdd;
    @FXML
    private ComboBox<String> location;
    @FXML
    private ComboBox<String> method;
    @FXML
    private Button next;


    public MainWindowController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // EyeTracker Combobox
        ObservableList<String> eyeTrackersList = FXCollections.observableArrayList(SircusApplication.dataSircus.getEyeTrackerList());
        eyeTracker.setItems(eyeTrackersList);

        // Location Combobox
        ObservableList<String> locationsList = FXCollections.observableArrayList(SircusApplication.dataSircus.getLocationsList());
        location.setItems(locationsList);

        // Method Combobox
        ObservableList<String> methodsList = FXCollections.observableArrayList();
        method.setItems(methodsList);

        next.disableProperty().bind(Bindings.createBooleanBinding(() -> id.getText().trim().isEmpty(), id.textProperty())
                .or(Bindings.createBooleanBinding(() -> gender.getSelectedToggle() == null, gender.selectedToggleProperty()))
                .or(Bindings.createBooleanBinding(() -> birthDate.getValue() == null, birthDate.valueProperty()))
                .or(Bindings.createBooleanBinding(() -> name.getText().trim().isEmpty(), name.textProperty()))
                .or(Bindings.createBooleanBinding(() -> forename.getText().trim().isEmpty(), forename.textProperty()))
                .or(Bindings.createBooleanBinding(() -> location.getValue() == null, location.valueProperty()))
        );
    }

    @FXML
    private void computeAge() {
        Period period = Period.between(birthDate.getValue(), LocalDate.now());
        age.setText(period.getYears() + "  ans");
    }

    @FXML
    private void addLocation() {
        new AddLocationPopup(this.locationAdd.getScene().getWindow());
    }

    @FXML
    private void handleConnection(Event event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/login_popup.fxml"));
            DialogPane dialogPane = fxmlLoader.load();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Connexion administrateur");

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.get() == ButtonType.FINISH) {
                System.out.println("Connexion réussie");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
