package fr.polytech.sircus.controller.PopUps;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import lombok.Getter;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ParticipantCalibrationPopup implements Initializable {
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
    @Getter
    private RadioButton targetButton;
    @FXML
    private VBox targetBox;
    @FXML
    @Getter
    private ColorPicker targetColor;
    @FXML
    @Getter
    private Circle target;
    @FXML
    @Getter
    private RadioButton imageButton;
    @FXML
    private VBox imageBox;
    @FXML
    @Getter
    private ImageView targetImage;
    @FXML
    private Button addImage;
    @FXML
    private VBox videoBox;
    @FXML
    @Getter
    public RadioButton videoButton;
    @FXML
    @Getter
    private MediaView targetVideo;
    @FXML
    private Button addVideo;
    @FXML
    @Getter
    private ButtonType calibrate;
    @FXML
    @Getter
    private ButtonType cancel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Integer> targetNumberList = FXCollections.observableList(Arrays.asList(2, 5, 9));
        targetNumber.setItems(targetNumberList);
        targetNumber.getSelectionModel().select(1);

        targetColor.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            String hexColor = String.format("#%02X%02X%02X", (int)(newValue.getRed() * 255), (int)(newValue.getGreen() * 255), (int)(newValue.getBlue() * 255));
            target.setFill(Paint.valueOf(hexColor));
        });

        targetBox.visibleProperty().bind(Bindings.createBooleanBinding(() -> targetButton.isSelected(), targetButton.selectedProperty()));
        imageBox.visibleProperty().bind(Bindings.createBooleanBinding(() -> imageButton.isSelected(), imageButton.selectedProperty()));
        videoBox.visibleProperty().bind(Bindings.createBooleanBinding(() -> videoButton.isSelected(), videoButton.selectedProperty()));

        addImage.setOnAction(actionEvent -> {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Ajouter une image");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
                );

                File file = fileChooser.showOpenDialog(addImage.getScene().getWindow());
                if (file != null)
                    targetImage.setImage(new Image(file.toURI().toString()));
            } catch (Exception e) {
                System.out.print("Aucun fichier sélectionné.");
            }
        });

        /*addVideo.setOnAction(actionEvent -> {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Ajouter une vidéo");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov")
                );

                File file = fileChooser.showOpenDialog(addVideo.getScene().getWindow());
                if (file != null)
                    targetVideo.setMediaPlayer(new MediaPlayer(new Media(file.toURI().toString())));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print("Aucun fichier sélectionné.");
            }
        });*/
    }
}
