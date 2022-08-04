package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.AddAdminPopup;
import fr.polytech.sircus.controller.PopUps.LoginPopup;
import fr.polytech.sircus.controller.PopUps.RemoveAdminPopup;
import fr.polytech.sircus.model.Participant;
import fr.polytech.sircus.model.User;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class manages the main window
 */
public class MainWindowController implements Initializable {
    @FXML
    private ComboBox<String> eyeTracker;
    @FXML
    private Button tobiiCalibration;
    @FXML
    private Button addAdmin;
    @FXML
    private Button removeAdmin;
    @FXML
    private Button adminLogOut;
    @FXML
    private Button admin;
    @FXML
    private TextField id;
    @FXML
    private ToggleGroup sex;
    @FXML
    private RadioButton radioM;
    @FXML
    private RadioButton radioF;
    @FXML
    private TextField visitNumber;
    @FXML
    private DatePicker birthDate;
    @FXML
    private TextField age;
    @FXML
    private ToggleGroup ocularDominance;
    @FXML
    private ToggleGroup laterality;
    @FXML
    private ToggleGroup type;
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
    private Text adminLabel;
    @FXML
    private Button next;


    /**
     * Default constructor
     */
    public MainWindowController() {}

    /**
     * Initialize all the fields, buttons and visibility of each component
     * @param url unused parameter
     * @param resourceBundle unused parameter
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // EyeTracker
        ObservableList<String> eyeTrackersList = FXCollections.observableArrayList(SircusApplication.dataSircus.getEyeTrackerList());
        eyeTracker.setItems(eyeTrackersList);
        eyeTracker.getSelectionModel().select(SircusApplication.dataSircus.getEyeTrackerSaved());
        eyeTracker.valueProperty().addListener((observableValue, oldName, newName) -> SircusApplication.dataSircus.saveEyeTracker(newName));
        tobiiCalibration.visibleProperty().bind(Bindings.createBooleanBinding(() -> eyeTracker.getValue() != null, eyeTracker.valueProperty()));
        tobiiCalibration.setOnAction(actionEvent -> {
            try {
                Runtime.getRuntime().exec("python TobiiCalibration.py");
            } catch (IOException e) {
                throw new RuntimeException(e);
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

        // Admin mode
        adminLabel.setVisible(SircusApplication.adminConnected);
        addAdmin.setVisible(SircusApplication.superAdminConnected);
        addAdmin.setOnAction(actionEvent -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/add_admin_popup.fxml"));
                DialogPane dialogPane = fxmlLoader.load();
                AddAdminPopup controller = fxmlLoader.getController();

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setDialogPane(dialogPane);
                dialog.setTitle("Ajout d'un administrateur");
                Window window = dialogPane.getScene().getWindow();
                window.setOnCloseRequest(e -> dialog.hide());

                Button cancelButton = (Button) dialogPane.lookupButton(controller.getCancel());
                cancelButton.setCancelButton(true);
                Button addButton = (Button) dialogPane.lookupButton(controller.getAdd());
                addButton.setDefaultButton(true);
                addButton.setStyle("-fx-background-color: #457b9d;");
                addButton.setTextFill(Paint.valueOf("white"));

                Optional<ButtonType> clickedButton = dialog.showAndWait();
                if (clickedButton.isPresent())
                    if (clickedButton.get() == ButtonType.FINISH)
                        SircusApplication.dataSircus.addAdmin(controller.getUserName().getText(),
                                controller.getPassword().getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        removeAdmin.setVisible(SircusApplication.superAdminConnected);
        removeAdmin.setOnAction(actionEvent -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/remove_admin_popup.fxml"));
                DialogPane dialogPane = fxmlLoader.load();
                RemoveAdminPopup controller = fxmlLoader.getController();

                Button cancelButton = (Button) dialogPane.lookupButton(controller.getCancel());
                cancelButton.setCancelButton(true);
                Button removeButton = (Button) dialogPane.lookupButton(controller.getRemove());
                removeButton.setDefaultButton(true);
                removeButton.setStyle("-fx-background-color: #f87167;");
                removeButton.setTextFill(Paint.valueOf("white"));

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setDialogPane(dialogPane);
                dialog.setTitle("Suppression d'un administrateur");
                Window window = dialogPane.getScene().getWindow();
                window.setOnCloseRequest(e -> dialog.hide());

                Optional<ButtonType> clickedButton = dialog.showAndWait();
                if (clickedButton.isPresent())
                    if (clickedButton.get() == ButtonType.FINISH)
                        SircusApplication.dataSircus.removeAdmin(controller.getAdmins().getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        adminLogOut.setVisible(SircusApplication.adminConnected);
        adminLogOut.setOnAction(actionEvent -> {
            SircusApplication.adminConnected = false;
            SircusApplication.superAdminConnected = false;
            adminLabel.setVisible(false);
            adminLogOut.setVisible(false);
            addAdmin.setVisible(false);
            removeAdmin.setVisible(false);
        });

        id.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.isEmpty())
                id.setStyle(null);
            else
                id.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
        });

        sex.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
            if (newToggle.isSelected()) {
                radioM.getStylesheets().clear();
                radioF.getStylesheets().clear();
            }
        });

        visitNumber.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
                visitNumber.setText(newValue.replaceAll("[^\\d]", ""));
        });

        birthDate.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\/"))
                birthDate.getEditor().setText(newValue.replaceAll("[^\\d\\/]", ""));
            if (newValue.length() > 10)
                birthDate.getEditor().setText(birthDate.getEditor().getText().substring(0, 10));
            if (!newValue.isEmpty())
                birthDate.setStyle(null);
            else
                birthDate.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
        });

        // Forename and Name
        name.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*\\-"))
                name.setText(newValue.replaceAll("[^\\sa-zA-Z\\-]", ""));
            if (!newValue.isEmpty())
                name.setStyle(null);
            else
                name.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
        });

        forename.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*\\-"))
                forename.setText(newValue.replaceAll("[^\\sa-zA-Z\\-]", ""));
            if (!newValue.isEmpty())
                forename.setStyle(null);
            else
                forename.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
        });

        locations.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty())
                locations.setStyle(null);
            else
                locations.setStyle("-fx-border-color: red; -fx-border-radius: 3;");
        });

        // Initialize all components if they have been already filled
        /*
        if (SircusApplication.participant != null) {
            id.setText(SircusApplication.participant.getIdentifier());
            Objects.requireNonNull(getRadioButton(sex.getToggles(), SircusApplication.participant.getSex().name())).setSelected(true);
            visitNumber.setText(SircusApplication.participant.getVisitNumber());
            birthDate.setValue(SircusApplication.participant.getBirthDate());
            age.setText(SircusApplication.participant.computeAge() + "  ans");
            if (SircusApplication.participant.getEyeDominance() != null)
                Objects.requireNonNull(getRadioButton(ocularDominance.getToggles(), SircusApplication.participant.getEyeDominance().name())).setSelected(true);
            if (SircusApplication.participant.getHandLaterality() != null)
                Objects.requireNonNull(getRadioButton(laterality.getToggles(), SircusApplication.participant.getHandLaterality().name())).setSelected(true);
        }
        if (SircusApplication.user != null) {
            Objects.requireNonNull(getRadioButton(type.getToggles(), SircusApplication.user.getUserType().name())).setSelected(true);
            name.setText(SircusApplication.user.getLastName());
            forename.setText(SircusApplication.user.getFirstName());
        }
        if (SircusApplication.currentLocation != null)
            locations.setValue(SircusApplication.currentLocation);
        if (SircusApplication.currentMethod != null)
            methods.setValue(SircusApplication.currentMethod);*/
    }

    /**
     * Deduct and set the age
     */
    @FXML
    private void computeAge() {
        if (birthDate.getValue() != null) {
            Period period = Period.between(birthDate.getValue(), LocalDate.now());
            age.setText(period.getYears() + "  ans");
        } else
            age.setText("  ans");
    }

    /**
     * Show the components to add a new location
     */
    @FXML
    private void addLocation() {
        locationFullBox.setVisible(false);
        locationBox.setVisible(true);
        validLocButton.setDefaultButton(true);
        cancelLocButton.setCancelButton(true);
    }

    /**
     * Show the components to update a location
     */
    @FXML
    private void updateLocation() {
        locationFullBox.setVisible(false);
        locationBox.setVisible(true);
        location = locations.getSelectionModel().getSelectedItem();
        locationField.setText(location);
        validLocButton.setDefaultButton(true);
        cancelLocButton.setCancelButton(true);
    }

    /**
     * Add or update a location when validate button is pressed
     */
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
            Alert dialog = new Alert(Alert.AlertType.ERROR, "Vous devez renseigner un lieu.", ButtonType.OK);
            dialog.show();
        }
    }

    /**
     * Cancel adding or editing of a location
     */
    @FXML
    private void cancelLocation() {
        resetLocation();
    }

    /**
     * Go back to default display of the location field
     */
    private void resetLocation() {
        location = null;
        locationBox.setVisible(false);
        locationField.clear();
        locationFullBox.setVisible(true);
        validLocButton.setDefaultButton(false);
        cancelLocButton.setCancelButton(false);
    }

    /**
     * Remove selected location when remove button is pressed
     */
    @FXML
    private void removeLocation() {
        if (locations.getValue() != null) {
            Alert dialog = new Alert(Alert.AlertType.WARNING, "Voulez-vous supprimer le lieu " + locations.getValue() + " ?", ButtonType.OK, ButtonType.CANCEL);
            dialog.showAndWait();
            if (dialog.getResult() == ButtonType.OK) {
                String locationToRemove = locations.getValue();
                locations.getItems().remove(locationToRemove);
                SircusApplication.dataSircus.removeLocationFromList(locationToRemove);
                locations.getSelectionModel().select(0);
            }
        }
    }

    /**
     * Show the components to add a new classification method
     */
    @FXML
    private void addMethod() {
        methodFullBox.setVisible(false);
        methodBox.setVisible(true);
        validMethodButton.setDefaultButton(true);
        cancelMethodButton.setCancelButton(true);
    }

    /**
     * Show the components to update a classification method
     */
    @FXML
    private void updateMethod() {
        methodFullBox.setVisible(false);
        methodBox.setVisible(true);
        method = methods.getSelectionModel().getSelectedItem();
        methodField.setText(method);
        validMethodButton.setDefaultButton(true);
        cancelMethodButton.setCancelButton(true);
    }

    /**
     * Add or update a classification method when validate button is pressed
     */
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
            Alert dialog = new Alert(Alert.AlertType.ERROR, "Vous devez renseigner une méthode.", ButtonType.OK);
            dialog.show();
        }
    }

    /**
     * Cancel adding or editing of a classification method
     */
    @FXML
    private void cancelMethod() {
        resetMethod();
    }

    /**
     * Go back to default display of the classification method field
     */
    private void resetMethod() {
        method = null;
        methodBox.setVisible(false);
        methodField.clear();
        methodFullBox.setVisible(true);
        validMethodButton.setDefaultButton(false);
        cancelMethodButton.setCancelButton(false);
    }

    /**
     * Remove selected classification method when remove button is pressed
     */
    @FXML
    private void removeMethod() {
        if (methods.getValue() != null) {
            Alert dialog = new Alert(Alert.AlertType.WARNING, "Voulez-vous supprimer la méthode " + methods.getValue() + " ?", ButtonType.OK, ButtonType.CANCEL);
            dialog.showAndWait();
            if (dialog.getResult() == ButtonType.OK) {
                String methodToRemove = methods.getValue();
                methods.getItems().remove(methodToRemove);
                SircusApplication.dataSircus.removeMethodFromList(methodToRemove);
                methods.getSelectionModel().select(0);
            }
        }
    }

    /**
     * Show connection popup when admin button is pressed
     */
    @FXML
    private void handleConnection() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/login_popup.fxml"));
            DialogPane dialogPane = fxmlLoader.load();
            LoginPopup controller = fxmlLoader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Connexion opérateur");
            Window window = dialogPane.getScene().getWindow();
            window.setOnCloseRequest(e -> dialog.hide());

            Button cancelButton = (Button) dialogPane.lookupButton(controller.getCancel());
            cancelButton.setCancelButton(true);
            Button connectButton = (Button) dialogPane.lookupButton(controller.getConnect());
            connectButton.setDefaultButton(true);
            connectButton.setStyle("-fx-background-color: #457b9d;");
            connectButton.setTextFill(Paint.valueOf("white"));

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.isPresent()) {
                if (clickedButton.get() == controller.getConnect()) {
                    if (controller.isAdmin()) {
                        SircusApplication.adminConnected = true;
                    } else if (controller.isSuperAdmin()) {
                        SircusApplication.adminConnected = true;
                        SircusApplication.superAdminConnected = true;
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Nom d'utilisateur ou mot de passe incorrect.", ButtonType.OK);
                        alert.setTitle("Echec de Connexion");
                        alert.setHeaderText("Erreur");
                        alert.show();
                    }
                    adminLabel.setVisible(SircusApplication.adminConnected);
                    adminLogOut.setVisible(SircusApplication.adminConnected);
                    addAdmin.setVisible(SircusApplication.superAdminConnected);
                    removeAdmin.setVisible(SircusApplication.superAdminConnected);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    /**
     * Switch to the next page
     * @throws IOException
     */
    @FXML
    private void nextPage() throws IOException {
        if (mandatoryInfosFilled()) {
            saveInfos();
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/meta_seq.fxml")));
            Stage stage = (Stage) next.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), next.getScene().getWidth(), next.getScene().getHeight());
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Check if all mandatory field are filled and set focus on unfilled field
     * @return true if all are completed; false otherwise
     */
    private boolean mandatoryInfosFilled() {
        boolean idEmpty = false;
        boolean sexEmpty = false;
        boolean dateEmpty = false;
        boolean nameEmpty = false;
        boolean forenameEmpty = false;
        boolean locationEmpty = false;

        if (locations.getValue() == null) {
            locationEmpty = true;
            locations.requestFocus();
        }
        if (forename.getText().isEmpty()) {
            forenameEmpty = true;
            forename.requestFocus();
        }
        if (name.getText().isEmpty()) {
            nameEmpty = true;
            name.requestFocus();
        }
        if (birthDate.getValue() == null) {
            dateEmpty = true;
            birthDate.requestFocus();
        }
        if (sex.getSelectedToggle() == null) {
            sexEmpty = true;
            radioF.requestFocus();
        }
        if (id.getText().isEmpty()) {
            idEmpty = true;
            id.requestFocus();
        }

        return !idEmpty && !sexEmpty && !dateEmpty && !nameEmpty && !forenameEmpty && !locationEmpty;
    }

    /**
     * Save all fields content
     */
    private void saveInfos() {
        // Save patient infos
        SircusApplication.participant = new Participant();
        if (!id.getText().isEmpty())
            SircusApplication.participant.setIdentifier(id.getText());
        if (sex.getSelectedToggle() != null)
            SircusApplication.participant.setSex(Participant.Sex.valueOf(((RadioButton)this.sex.getSelectedToggle()).getText()));
        if (!visitNumber.getText().isEmpty())
            SircusApplication.participant.setVisitNumber(Integer.parseInt(visitNumber.getText()));
        if (birthDate.getValue() != null)
            SircusApplication.participant.setBirthDate(birthDate.getValue());
        if (ocularDominance.getSelectedToggle() != null)
            SircusApplication.participant.setEyeDominance(Participant.EyeDominance.valueOf(((RadioButton)this.ocularDominance.getSelectedToggle()).getText()));
        if (laterality.getSelectedToggle() != null)
            SircusApplication.participant.setHandLaterality(Participant.HandLaterality.valueOf(((RadioButton)this.laterality.getSelectedToggle()).getText()));

        // Save operator infos
        SircusApplication.user = new User();
        if (!name.getText().isEmpty())
            SircusApplication.user.setLastName(name.getText());
        if (!forename.getText().isEmpty())
            SircusApplication.user.setFirstName(forename.getText());

        // Save location and method
        if (locations.getValue() != null)
            SircusApplication.currentLocation = locations.getValue();
        if (methods.getValue() != null)
            SircusApplication.currentMethod = methods.getValue();
    }

    /********************************************************************
     ******** PENSER A ENLEVER CETTE METHODE POUR LE RENDU FINALE *******
     ********************************************************************/
    @FXML
    private void enableNextButton(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2) {
            SircusApplication.adminConnected = true;
            adminLabel.setVisible(true);
            adminLogOut.setVisible(true);
            id.setText("ifsvliu");
            radioM.setSelected(true);
            birthDate.setValue(LocalDate.now());
            ocularDominance.getToggles().get(0).setSelected(true);
            laterality.getToggles().get(2).setSelected(true);
            name.setText("fdfd");
            forename.setText("lkhfdlkjhgfd");
            locations.getSelectionModel().select(0);
        }
    }
}
