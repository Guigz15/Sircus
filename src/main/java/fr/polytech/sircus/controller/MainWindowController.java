package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.AddLocationPopup;
import fr.polytech.sircus.controller.PopUps.LoginPopupController;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

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
    private ToggleGroup sex;
    @FXML
    private DatePicker birthDate;
    @FXML
    private TextField age;
    @FXML
    private ToggleGroup ocularDominance;
    @FXML
    private ToggleGroup laterality;
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
    private Button importButton;
    @FXML
    private Button exportButton;
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
                .or(Bindings.createBooleanBinding(() -> sex.getSelectedToggle() == null, sex.selectedToggleProperty()))
                .or(Bindings.createBooleanBinding(() -> birthDate.getValue() == null, birthDate.valueProperty()))
                .or(Bindings.createBooleanBinding(() -> name.getText().trim().isEmpty(), name.textProperty()))
                .or(Bindings.createBooleanBinding(() -> forename.getText().trim().isEmpty(), forename.textProperty()))
                .or(Bindings.createBooleanBinding(() -> location.getValue() == null, location.valueProperty()))
        );

        exportButton.setVisible(SircusApplication.adminConnected);
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
            LoginPopupController controller = fxmlLoader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Connexion administrateur");

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.get() == ButtonType.FINISH && controller.checkUserName() && controller.checkPassword()) {
                System.out.println("Connexion réussie");
                SircusApplication.adminConnected = true;
                exportButton.setVisible(true);
            } else {
                System.out.println("Connexion échouée");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exporting() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter");
        fileChooser.setInitialFileName(this.id.getText());
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());
        if (file != null) {
            try {
                PrintWriter writer = new PrintWriter(file);
                writer.println(this.id.getText());
                writer.println(((RadioButton)this.sex.getSelectedToggle()).getText());
                writer.println(this.birthDate.getValue());
                writer.println(((RadioButton)this.ocularDominance.getSelectedToggle()).getText());
                writer.println(((RadioButton)this.laterality.getSelectedToggle()).getText());
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void importing() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        File file = fileChooser.showOpenDialog(importButton.getScene().getWindow());
        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNext()) {
                    this.id.setText(scanner.next());
                    Objects.requireNonNull(getRadioButton(sex.getToggles(), scanner.next())).setSelected(true);
                    this.birthDate.setValue(LocalDate.parse(scanner.next()));
                    Objects.requireNonNull(getRadioButton(ocularDominance.getToggles(), scanner.next())).setSelected(true);
                    Objects.requireNonNull(getRadioButton(laterality.getToggles(), scanner.next())).setSelected(true);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the radioButton that has been saved in file.
     * @param radioButtons The radioButton list in toggleGroup.
     * @param value The radioButton's text saved.
     * @return The radioButton with the same text that has been saved.
     */
    private RadioButton getRadioButton(ObservableList<Toggle> radioButtons, String value) {
        for (Toggle toggle : radioButtons) {
            if (((RadioButton) toggle).getText().equals(value))
                return (RadioButton) toggle;
        }
        return null;
    }
}
