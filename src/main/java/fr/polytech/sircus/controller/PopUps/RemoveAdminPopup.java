package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import lombok.Getter;

import java.net.URL;
import java.util.ResourceBundle;

public class RemoveAdminPopup implements Initializable {
    @FXML
    @Getter
    private ComboBox<String> admins;
    @FXML
    @Getter
    public ButtonType remove;
    @FXML
    @Getter
    public ButtonType cancel;

    public RemoveAdminPopup() {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> adminsList = FXCollections.observableArrayList(SircusApplication.dataSircus.getAdmins().keySet());
        admins.setItems(adminsList);
    }
}
