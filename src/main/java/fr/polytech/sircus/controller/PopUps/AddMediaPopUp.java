package fr.polytech.sircus.controller.PopUps;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.Interstim;
import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.TypeMedia;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import lombok.Getter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ResourceBundle;

/**
 * Controller of the add media section
 */
public class AddMediaPopUp implements Initializable {

    private final String MEDIAS_PATH = "medias/";
    @FXML
    private HBox newMediaBox;
    @FXML
    private RadioButton newMediaButton;
    @FXML
    private RadioButton existingMediaButton;
    @FXML
    private HBox newInterstimBox;
    @FXML
    @Getter
    private ComboBox<Media> mediasList;
    @FXML
    private Button addInterstimFile;
    @FXML
    private TextField interstimName;
    @FXML
    private TextField interstimDuration;
    @FXML
    private Button addMediaFile;
    @FXML
    private TextField mediaName;
    @FXML
    private TextField mediaDuration;
    @Getter
    private Interstim newInterstim = null;
    @Getter
    private Media newMedia = null;
    @FXML
    private ImageView interstimImage;
    @FXML
    private ImageView mediaImage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        newMediaBox.disableProperty().bind(Bindings.createBooleanBinding(() -> !newMediaButton.isSelected(), newMediaButton.selectedProperty()));
        mediaDuration.disableProperty().bind(Bindings.createBooleanBinding(() -> mediaName.getText().isEmpty(), mediaName.textProperty()));
        newInterstimBox.disableProperty().bind(Bindings.createBooleanBinding(() -> mediaName.getText().isEmpty(), mediaName.textProperty()));
        interstimDuration.disableProperty().bind(Bindings.createBooleanBinding(() -> interstimName.getText().isEmpty(), interstimName.textProperty()));
        mediasList.disableProperty().bind(Bindings.createBooleanBinding(() -> !existingMediaButton.isSelected(), existingMediaButton.selectedProperty()));

        mediaDuration.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\-"))
                mediaDuration.setText(newValue.replaceAll("[^\\d\\-]", ""));
            if (!newValue.isEmpty()) {
                newMedia.setMinDuration(Duration.ofSeconds(Long.parseLong(mediaDuration.getText().split("\\-")[0])));
                newMedia.setMaxDuration(Duration.ofSeconds(Long.parseLong(mediaDuration.getText().split("\\-")[1])));
            }
        });

        interstimDuration.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\-"))
                interstimDuration.setText(newValue.replaceAll("[^\\d\\-]", ""));
            if (!newValue.isEmpty()) {
                newInterstim.setMinDuration(Duration.ofSeconds(Long.parseLong(interstimDuration.getText().split("\\-")[0])));
                newInterstim.setMaxDuration(Duration.ofSeconds(Long.parseLong(interstimDuration.getText().split("\\-")[1])));
            }
        });

        addMediaFile.setOnAction(actionEvent -> {
            try {
                FileChooser fileChooserMedia = new FileChooser();
                fileChooserMedia.setTitle("Open file (media)");
                if (SircusApplication.dataSircus.getPath().isCustomPath()) {
                    fileChooserMedia.setInitialDirectory(new File(SircusApplication.dataSircus.getPath().getLastPath()));
                } else {
                    fileChooserMedia.setInitialDirectory(new File(SircusApplication.dataSircus.getPath().getDefaultPath()));
                }
                fileChooserMedia.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
                );
                File mediaFile = fileChooserMedia.showOpenDialog(addMediaFile.getScene().getWindow());

                Path path = Paths.get(mediaFile.getPath());
                String absoluteMediaPath = new File(MEDIAS_PATH).getAbsolutePath();

                // We compare the absolute path of the "medias" directory with the new media's one.
                // If they are not the same directory, we copy the new media to the application's "medias" directory.
                if (!absoluteMediaPath.equals(path.toString().split("\\\\" + mediaFile.getName())[0])) {
                    OutputStream os = new FileOutputStream(MEDIAS_PATH + mediaFile.getName());
                    Files.copy(path, os);
                }

                newMedia = new Media(mediaFile.getName(), Duration.ofSeconds(1), Duration.ofSeconds(1), TypeMedia.PICTURE);
                mediaName.setText(newMedia.getFilename());
                mediaDuration.setText(newMedia.getMinDuration().toString() + "-" + newMedia.getMaxDuration().toString());
                mediaImage.setImage(new Image(mediaFile.toURI().toString()));
            } catch (Exception e) {
                System.out.print("Aucun fichier sélectionné.");
            }
        });

        addInterstimFile.setOnAction(actionEvent -> {
            try {
                FileChooser fileChooserInterstim = new FileChooser();
                fileChooserInterstim.setTitle("Open file (interstim)");
                if (SircusApplication.dataSircus.getPath().isCustomPath()) {
                    fileChooserInterstim.setInitialDirectory(new File(SircusApplication.dataSircus.getPath().getLastPath()));
                } else {
                    fileChooserInterstim.setInitialDirectory(new File(SircusApplication.dataSircus.getPath().getDefaultPath()));
                }
                fileChooserInterstim.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
                );
                File interstimFile = fileChooserInterstim.showOpenDialog(addInterstimFile.getScene().getWindow());

                Path path = Paths.get(interstimFile.getPath());
                String absoluteMediaPath = new File(MEDIAS_PATH).getAbsolutePath();

                // We compare the absolute path of the "medias" directory with the new media's one.
                // If they are not the same directory, we copy the new media to the application's "medias" directory.
                if (!absoluteMediaPath.equals(path.toString().split("\\\\" + interstimFile.getName())[0])) {
                    OutputStream os = new FileOutputStream(MEDIAS_PATH + interstimFile.getName());
                    Files.copy(path, os);
                }

                newInterstim = new Interstim(interstimFile.getName(), Duration.ofSeconds(1), Duration.ofSeconds(1), TypeMedia.PICTURE, newMedia);
                interstimName.setText(newInterstim.getFilename());
                interstimDuration.setText(newInterstim.getMinDuration().toString() + "-" + newInterstim.getMaxDuration().toString());
                interstimImage.setImage(new Image(interstimFile.toURI().toString()));
            } catch (Exception e) {
                System.out.print("Aucun fichier sélectionné.");
            }
        });

        mediasList.setOnAction(actionEvent -> {
            newMedia = new Media(mediasList.getSelectionModel().getSelectedItem());
            if(newMedia.getInterstim() != null) {
                newInterstim = new Interstim(newMedia.getInterstim());
                File insterstim = new File(MEDIAS_PATH + newInterstim.getFilename());
                interstimImage.setImage(new Image(insterstim.toURI().toString()));
            } else {
                newInterstim = null;
                interstimImage.setImage(null);
            }
            File media = new File(MEDIAS_PATH + newMedia.getFilename());
            mediaImage.setImage(new Image(media.toURI().toString()));
        });
    }
}
