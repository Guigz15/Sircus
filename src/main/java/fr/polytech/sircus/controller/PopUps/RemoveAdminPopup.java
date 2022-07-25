package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RemoveAdminPopup implements Initializable {
    public ComboBox<String> admins;

    public RemoveAdminPopup() {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> adminsList = FXCollections.observableArrayList(SircusApplication.dataSircus.getAdmins().keySet());
        admins.setItems(adminsList);
    }
}
