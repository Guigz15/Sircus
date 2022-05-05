package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Result;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.Objects;

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
        viewer.previousSequence();
    }

    /**
     * Go to the next sequence in the meta sequence.
     */
    @FXML
    public void nextSequence() {
        viewer.nextSequence();
    }

    /**
     * Starts/Pauses the viewer or creates one if none is opened.
     */
    @FXML
    public void playViewer() {
        if (viewer == null) {
            viewer = new ViewerController(this.playButton.getScene().getWindow(), this.metaSequence, this);
        } else {
            // If Pause button is displayed, clicking it calls appropriate function of ViewerController
            // Also change displayed icon and state variable
            if (viewerPlayingState) {
                viewer.pauseViewer();
                playButton.setGraphic(playIcon);
                viewerPlayingState = false;
            } else {
                // If Play button is displayed, click it calls appropriate function of ViewerController
                // Also change displayed icon and state variable
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
        // TODO: reset everything
    }

    /**
     * Closes the viewer.
     */
    public void closeViewer() {
        viewer = null;
        viewerPlayingState = false;
        playButton.setGraphic(playIcon);
    }
}
