package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.LoginPopup;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    private HBox locationFullBox;
    @FXML
    private ComboBox<String> locations;
    @FXML
    private Button locationUpdate;
    @FXML
    private Button locationRemove;
    @FXML
    private HBox locationBox;
    @FXML
    private TextField locationField;
    private String location = null;
    @FXML
    private Button validLocButton;
    @FXML
    private Button cancelLocButton;
    @FXML
    private HBox methodFullBox;
    @FXML
    private ComboBox<String> methods;
    @FXML
    private Button methodUpdate;
    @FXML
    private Button methodRemove;
    @FXML
    private HBox methodBox;
    @FXML
    private TextField methodField;
    private String method = null;
    @FXML
    private Button validMethodButton;
    @FXML
    private Button cancelMethodButton;
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
        // EyeTracker
        ObservableList<String> eyeTrackersList = FXCollections.observableArrayList(SircusApplication.dataSircus.getEyeTrackerList());
        eyeTracker.setItems(eyeTrackersList);
        eyeTracker.getSelectionModel().select(SircusApplication.dataSircus.getEyeTrackerSaved());
        eyeTracker.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldName, String newName) {
                SircusApplication.dataSircus.saveEyeTracker(newName);
            }
        });

        // Location
        ObservableList<String> locationsList = FXCollections.observableArrayList(SircusApplication.dataSircus.getLocationsList());
        locations.setItems(locationsList);
        locationUpdate.disableProperty().bind(Bindings.createBooleanBinding(() -> locations.getValue() == null, locations.valueProperty()));
        locationRemove.disableProperty().bind(Bindings.createBooleanBinding(() -> locations.getValue() == null, locations.valueProperty()));

        // Method
        ObservableList<String> methodsList = FXCollections.observableArrayList(SircusApplication.dataSircus.getMethodsList());
        methods.setItems(methodsList);
        methodUpdate.disableProperty().bind(Bindings.createBooleanBinding(() -> methods.getValue() == null, methods.valueProperty()));
        methodRemove.disableProperty().bind(Bindings.createBooleanBinding(() -> methods.getValue() == null, methods.valueProperty()));

        next.disableProperty().bind(Bindings.createBooleanBinding(() -> id.getText().trim().isEmpty(), id.textProperty())
                .or(Bindings.createBooleanBinding(() -> sex.getSelectedToggle() == null, sex.selectedToggleProperty()))
                .or(Bindings.createBooleanBinding(() -> birthDate.getValue() == null, birthDate.valueProperty()))
                .or(Bindings.createBooleanBinding(() -> name.getText().trim().isEmpty(), name.textProperty()))
                .or(Bindings.createBooleanBinding(() -> forename.getText().trim().isEmpty(), forename.textProperty()))
                .or(Bindings.createBooleanBinding(() -> locations.getValue() == null, locations.valueProperty()))
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
        locationFullBox.setVisible(false);
        locationBox.setVisible(true);
        validLocButton.setDefaultButton(true);
        cancelLocButton.setCancelButton(true);
    }

    @FXML
    private void updateLocation() {
        locationFullBox.setVisible(false);
        locationBox.setVisible(true);
        location = locations.getSelectionModel().getSelectedItem();
        locationField.setText(location);
        validLocButton.setDefaultButton(true);
        cancelLocButton.setCancelButton(true);
    }

    @FXML
    private void validateLocation() {
        if (!locationField.getText().isEmpty()) {
            if (location != null) {
                String newlocation = locationField.getText();
                locations.getItems().set(locations.getItems().indexOf(location), newlocation);
                SircusApplication.dataSircus.updateLocation(location, newlocation);
                locations.getSelectionModel().select(newlocation);
            } else {
                String locationToAdd = locationField.getText();
                locations.getItems().add(locationToAdd);
                SircusApplication.dataSircus.addLocationToList(locationToAdd);
                locations.getSelectionModel().select(locationToAdd);
            }
            resetLocation();
        } else {
            Platform.runLater(() -> {
                Alert dialog = new Alert(Alert.AlertType.ERROR, "Vous devez renseigner un lieu.", ButtonType.OK);
                dialog.show();
            });
        }
    }

    @FXML
    private void cancelLocation() {
        resetLocation();
    }

    private void resetLocation() {
        location = null;
        locationBox.setVisible(false);
        locationField.clear();
        locationFullBox.setVisible(true);
        validLocButton.setDefaultButton(false);
        cancelLocButton.setCancelButton(false);
    }

    @FXML
    private void removeLocation() {
        if (locations.getValue() != null) {
            String locationToRemove = locations.getValue();
            locations.getItems().remove(locationToRemove);
            SircusApplication.dataSircus.removeLocationFromList(locationToRemove);
            locations.getSelectionModel().select(0);
        }
    }

    @FXML
    private void addMethod() {
        methodFullBox.setVisible(false);
        methodBox.setVisible(true);
        validMethodButton.setDefaultButton(true);
        cancelMethodButton.setCancelButton(true);
    }

    @FXML
    private void updateMethod() {
        methodFullBox.setVisible(false);
        methodBox.setVisible(true);
        method = methods.getSelectionModel().getSelectedItem();
        methodField.setText(method);
        validMethodButton.setDefaultButton(true);
        cancelMethodButton.setCancelButton(true);
    }

    @FXML
    private void validateMethod() {
        if (!methodField.getText().isEmpty()) {
            if (method != null) {
                String newMethod = methodField.getText();
                methods.getItems().set(methods.getItems().indexOf(method), newMethod);
                SircusApplication.dataSircus.updateMethod(method, newMethod);
                methods.getSelectionModel().select(newMethod);
            } else {
                String methodToAdd = methodField.getText();
                methods.getItems().add(methodToAdd);
                SircusApplication.dataSircus.addMethodToList(methodToAdd);
                methods.getSelectionModel().select(methodToAdd);
            }
            resetMethod();
        } else {
            Platform.runLater(() -> {
                Alert dialog = new Alert(Alert.AlertType.ERROR, "Vous devez renseigner une méthode.", ButtonType.OK);
                dialog.show();
            });
        }
    }

    @FXML
    private void cancelMethod() {
        resetMethod();
    }

    private void resetMethod() {
        method = null;
        methodBox.setVisible(false);
        methodField.clear();
        methodFullBox.setVisible(true);
        validMethodButton.setDefaultButton(false);
        cancelMethodButton.setCancelButton(false);
    }

    @FXML
    private void removeMethod() {
        if (methods.getValue() != null) {
            String methodToRemove = methods.getValue();
            methods.getItems().remove(methodToRemove);
            SircusApplication.dataSircus.removeMethodFromList(methodToRemove);
            methods.getSelectionModel().select(0);
        }
    }
    @FXML
    private void handleConnection() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/login_popup.fxml"));
            DialogPane dialogPane = fxmlLoader.load();
            LoginPopup controller = fxmlLoader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Connexion administrateur");

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.get() == ButtonType.FINISH && controller.checkUserName() && controller.checkPassword()) {
                SircusApplication.adminConnected = true;
                exportButton.setVisible(true);
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

    @FXML
    private void nextPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/player_monitor.fxml")));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) next.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void enableNextButton() {
        next.disableProperty().unbind();
        next.setDisable(false);
    }
}
