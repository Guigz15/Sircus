package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class AddLocationPopup implements Initializable {

    @FXML
    private ComboBox<String> country;
    @FXML
    private ComboBox<String> city;

    public AddLocationPopup(Window parent) {
        /*FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/add_location_popup.fxml" ));

        try {
            Scene dialogScene = new Scene(fxmlLoader.load(),500,160);
            Stage dialog = new Stage();

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(parent);
            dialog.setScene(dialogScene);
            dialog.setResizable(false);
            dialog.setTitle("Ajouter un lieu");
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Country Combobox
        /*ArrayList<String> countries = new ArrayList<>();
        String[] countriesCode = Locale.getISOCountries();
        for (String countryCode : countriesCode) {
            Locale country = new Locale("", countryCode);
            countries.add(country.getDisplayCountry());
        }
        ObservableList<String> countriesList = FXCollections.observableArrayList(countries);*/
        ObservableList<String> countriesList = FXCollections.observableArrayList();
        country.setItems(countriesList);

        // City Combobox
        ObservableList<String> citiesList = FXCollections.observableArrayList();
        city.setItems(citiesList);
    }
}
