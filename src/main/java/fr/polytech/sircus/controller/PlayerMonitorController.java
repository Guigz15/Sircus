package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Result;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Manages the interface used when the exam is in progress (player_monitor)
 */
public class PlayerMonitorController {

    @FXML
    private GridPane playerMonitor;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private Button playButton;

    /**
     * The Result to fill
     */
    private Result result;

    // Viewer manager
    private ViewerController viewer;
    private Boolean viewerPlayingState;
    private final FontIcon playIcon;
    private final FontIcon pauseIcon;
    private final MetaSequence metaSequence;

    /**
     * Default constructor
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
     * Save a comment written in the comment section with the time
     */
    @FXML
    private void addComment(){
        this.result.addComment(this.commentTextArea.getText());
        this.commentTextArea.clear();
    }

    @FXML
    public void previousSequence(MouseEvent mouseEvent) {
        viewer.previousSequence();
    }

    @FXML
    public void nextSequence(MouseEvent mouseEvent) {
        viewer.nextSequence();
    }

    @FXML
    public void playViewer(MouseEvent mouseEvent) {
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

    @FXML
    public void stopViewer(MouseEvent mouseEvent) {
        // TODO: reset everything
    }

    /**
     * Closes the viewer
     */
    public void closeViewer() {
        viewer = null;
        viewerPlayingState = false;
        playButton.setGraphic(playIcon);
    }
}
