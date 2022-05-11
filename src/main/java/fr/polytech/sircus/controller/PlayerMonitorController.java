package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Result;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

/**
 * Manages the interface used when the exam is in progress (player_monitor).
 */
public class PlayerMonitorController {

    @FXML
    private GridPane playerMonitor;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private Button playButton;

    @FXML
    private StackPane previewPane;

    @FXML
    private MediaView mediaView;

    @FXML
    private ImageView imageView;

    /**
     * The Result to fill.
     */
    private Result result;

    // Viewer manager
    private ViewerController viewer;
    private Boolean viewerPlayingState;
    private final FontIcon playIcon;
    private final FontIcon pauseIcon;
    private final MetaSequence metaSequence;

    /**
     * Default constructor.
     */
    public PlayerMonitorController(){
        this.result = new Result();
        // TODO: replace the used meta sequence by the one selected during the previous step
        this.metaSequence = SircusApplication.dataSircus.getMetaSequencesList().get(0);
        this.viewer = null;

        this.viewerPlayingState = false;

        this.playIcon = new FontIcon("fa-play");
        this.playIcon.setIconSize(15);

        this.pauseIcon = new FontIcon("fa-pause");
        this.pauseIcon.setIconSize(15);
    }

    /**
     * Save a comment written in the comment section with the time.
     */
    @FXML
    private void addComment(){
        this.result.addComment(this.commentTextArea.getText());
        this.commentTextArea.clear();
    }

    /**
     * Go back to the previous page.
     */
    public void previousPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/meta_seq.fxml")));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) playerMonitor.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Go back to the previous sequence in the meta sequence.
     */
    @FXML
    public void previousSequence() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Passer à la séquence précédente");
        alert.setHeaderText("Êtes-vous sûr de vouloir passer à la séquence précédente ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            viewer.previousSequence();

            if (viewerPlayingState) {
                viewer.pauseViewer();
                playButton.setGraphic(playIcon);
                viewerPlayingState = false;
            }
        }
    }

    /**
     * Go to the next sequence in the meta sequence.
     */
    @FXML
    public void nextSequence() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Passer à la séquence suivante");
        alert.setHeaderText("Êtes-vous sûr de vouloir passer à la séquence suivante ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            viewer.nextSequence();

            if (viewerPlayingState) {
                viewer.pauseViewer();
                playButton.setGraphic(playIcon);
                viewerPlayingState = false;
            }
        }
    }

    /**
     * Starts/Pauses the viewer or creates one if none is opened.
     */
    @FXML
    public void playViewer() {
        if (viewer == null) {
            viewer = new ViewerController(this.playButton.getScene().getWindow(), this.metaSequence, this);
        } else {
            // We are playing something, so the pause button is displayed, so we must pause the sequence
            if (viewerPlayingState) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Mettre en pause");
                alert.setHeaderText("Êtes-vous sûr de vouloir mettre la lecture en pause ?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    viewer.pauseViewer();
                    playButton.setGraphic(playIcon);
                    viewerPlayingState = false;
                }
            } else {
                // We aren't playing something, so the play button is displayed, so we must start the sequence
                viewer.playViewer();
                playButton.setGraphic(pauseIcon);
                viewerPlayingState = true;
            }
        }
    }

    /**
     * Stops the viewer and resets it.
     */
    @FXML
    public void stopViewer() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Réinitialiser la méta-séquence");
        alert.setHeaderText("Êtes-vous sûr de vouloir arrêter la méta-séquence et la réinitialiser ?");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            viewer.resetMetaSequence();

            if (viewerPlayingState) {
                viewer.pauseViewer();
                playButton.setGraphic(playIcon);
                viewerPlayingState = false;
            }
        }
    }

    /**
     * Closes the viewer.
     */
    public void closeViewer() {
        viewer = null;
        viewerPlayingState = false;
        playButton.setGraphic(playIcon);
    }

    public void loadImage(fr.polytech.sircus.model.Media media) throws FileNotFoundException {
        InputStream is = new FileInputStream("medias/" + media.getFilename());
        Image image = new Image(is);

        imageView.setFitWidth(image.getWidth());
        imageView.setFitHeight(image.getHeight());

        imageView.setImage(image);
        imageView.setCache(true);

        resizeImage();

        Color color = media.getBackgroundColor();
        String hexColor = String.format( "-fx-border-color:black; -fx-border-width: 1; -fx-border-style: solid; -fx-background-color: #%02X%02X%02X;",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );

        previewPane.setStyle(hexColor);
    }

    /**
     * Method to resize the imageview in order to set a correct size in the preview.
     */
    private void resizeImage() {
        if (imageView.getImage() != null) {
            double ratio = Math.min(previewPane.getWidth() / imageView.getFitWidth(), previewPane.getHeight() / imageView.getFitHeight());

            imageView.setFitWidth(imageView.getFitWidth() * ratio);
            imageView.setFitHeight(imageView.getFitHeight() * ratio);
        }
    }

    /**
     * Clears the image section.
     */
    public void clearImage() {
        imageView.setImage(null);
        String hexColor ="-fx-border-color:black; -fx-border-width: 1; -fx-border-style: solid; -fx-background-color: transparent;";
        previewPane.setStyle(hexColor);
    }
}
