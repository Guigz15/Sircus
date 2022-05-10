package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Result;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private Label metaSeqDurationLabel;
    private TimelineClock metaSeqDuration;

    @FXML
    private Label metaSeqRemainingLabel;
    private TimelineClock metaSeqRemaining;

    @FXML
    private Label numMetaSeqLabel;

    @FXML
    private Label seqDurationLabel;
    private TimelineClock seqDuration;

    @FXML
    private Label seqRemainingLabel;
    private TimelineClock seqRemaining;

    @FXML
    private Label numSeqLabel;

    @FXML
    private Label durationLabel;
    private TimelineClock duration;

    @FXML
    private Label remainingLabel;
    private TimelineClock remaining;

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

        initTimers();
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

    /**
     * Create all the timers of the information tab
     */
    public void initTimers(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        metaSeqDurationLabel est nul et je ne comprends pas pourquoi
        metaSeqDurationLabel.setText("test");
        metaSeqDuration  = new TimelineClock(metaSeqDurationLabel, dtf, 0, 0, 0);
        metaSeqRemaining = new TimelineClock(metaSeqDurationLabel, dtf, 0, 0, 0);
        seqDuration      = new TimelineClock(metaSeqDurationLabel, dtf, 0, 0, 0);
        seqRemaining     = new TimelineClock(metaSeqDurationLabel, dtf, 0, 0, 0);
        duration         = new TimelineClock(metaSeqDurationLabel, dtf, 0, 0, 0);
        remaining        = new TimelineClock(metaSeqDurationLabel, dtf, 0, 0, 0);

    }
}

/**
 * Independent controllable clock with its label
 */
class TimelineClock{
    @Getter @Setter
    private LocalTime time;
    @Getter @Setter
    private Timeline timeline;
    @Getter @Setter
    private Label timeLabel;

    /**
     * Constructor with init values
     * @param label label to update
     * @param dtf format of teh clock
     * @param hours starting hours value
     * @param minutes starting minutes value
     * @param seconds starting seconds value
     */
    public TimelineClock(Label label, DateTimeFormatter dtf, int hours, int minutes, int seconds){
        timeLabel = label;
        time = LocalTime.of(hours, minutes, seconds);

        // Thread executed every second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            time = time.plusSeconds(1);
            timeLabel.setText(time.format(dtf));
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}