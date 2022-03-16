package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;
import java.net.URL;
import java.text.Normalizer;
import java.util.*;

/**
 * This class manages the location adding popup.
 */
public class AddLocationPopup implements Initializable {

    @FXML
    private ComboBox<String> country;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addButton;
    @FXML
    private TextField city;
    @FXML
    private TextField street;
    @FXML
    private TextField streetNumber;
    @FXML
    private TextField postCode;

    private Stage popUpStage = null;


    public AddLocationPopup(Window parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/add_location_popup.fxml" ));
        fxmlLoader.setController(this);

        try {
            Scene dialogScene = new Scene(fxmlLoader.load(),500,200);
            Stage dialog = new Stage();

            popUpStage = dialog;

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(parent);
            dialog.setScene(dialogScene);
            dialog.setResizable(false);
            dialog.setTitle("Ajouter un lieu");
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Country Combobox
        ArrayList<String> countries = new ArrayList<>();
        String[] countriesCode = Locale.getISOCountries();
        for (String countryCode : countriesCode) {
            Locale country = new Locale("", countryCode);
            countries.add(country.getDisplayCountry());
        }
        // Sorting countries by taking account of accent
        countries.sort((o1, o2) -> {
            o1 = Normalizer.normalize(o1, Normalizer.Form.NFD);
            o2 = Normalizer.normalize(o2, Normalizer.Form.NFD);
            return o1.compareTo(o2);
        });
        ObservableList<String> countriesList = FXCollections.observableArrayList(countries);
        country.setItems(countriesList);
        country.getSelectionModel().select("France");
    }

    @FXML
    private void cancelAdding() {
        city.clear();
        street.clear();
        streetNumber.clear();
        postCode.clear();
        country.getSelectionModel().select("France");
        popUpStage.close();
    }

    @FXML
    public void addingLocation() {
    }
}
