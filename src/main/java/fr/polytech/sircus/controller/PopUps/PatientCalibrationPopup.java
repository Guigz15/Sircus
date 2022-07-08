package fr.polytech.sircus.controller.PopUps;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import lombok.Getter;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class PatientCalibrationPopup implements Initializable {
    @FXML
    @Getter
    private CheckBox randomizeTarget;
    @FXML
    @Getter
    private ColorPicker backgroundColor;
    @FXML
    @Getter
    private ComboBox<Integer> targetNumber;
    @FXML
    private RadioButton pointButton;
    @FXML
    private VBox targetBox;
    @FXML
    @Getter
    private ColorPicker targetColor;
    @FXML
    @Getter
    private Circle target;
    @FXML
    private RadioButton imageButton;
    @FXML
    private ImageView targetImage;
    @FXML
    public RadioButton videoButton;
    @FXML
    private MediaView targetVideo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Integer> targetNumberList = FXCollections.observableList(Arrays.asList(2, 5, 9));
        targetNumber.setItems(targetNumberList);
        targetNumber.getSelectionModel().select(1);

        targetColor.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            String hexColor = String.format("#%02X%02X%02X", (int)(newValue.getRed() * 255), (int)(newValue.getGreen() * 255), (int)(newValue.getBlue() * 255));
            target.setFill(Paint.valueOf(hexColor));
        });

        targetBox.visibleProperty().bind(Bindings.createBooleanBinding(() -> pointButton.isSelected(), pointButton.selectedProperty()));
        targetImage.visibleProperty().bind(Bindings.createBooleanBinding(() -> imageButton.isSelected(), imageButton.selectedProperty()));
        targetVideo.visibleProperty().bind(Bindings.createBooleanBinding(() -> videoButton.isSelected(), videoButton.selectedProperty()));
    }
}
