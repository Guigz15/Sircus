package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Result;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Manages the interface used when the exam is in progress (player_monitor).
 */
public class PlayerMonitorController implements Initializable {

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
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

            duration.pause();
            seqRemaining.setTime(viewer.getPlayingMetaSequence().getSequencesList().get(viewer.getCurrentSequenceIndex()).getDuration().getSeconds());
            metaSeqRemaining.setTime(getRemainingTimeInMetaSeq());
            setCounterLabel(numSeqLabel, viewer.getCurrentSequenceIndex()+1, viewer.getPlayingMetaSequence().getSequencesList().size());
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

            duration.pause();
            seqRemaining.setTime(viewer.getPlayingMetaSequence().getSequencesList().get(viewer.getCurrentSequenceIndex()).getDuration().getSeconds());
            metaSeqRemaining.setTime(getRemainingTimeInMetaSeq());
            setCounterLabel(numSeqLabel, viewer.getCurrentSequenceIndex()+1, viewer.getPlayingMetaSequence().getSequencesList().size());
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
                    pauseAllClocks();
                }
            } else {
                // We aren't playing something, so the play button is displayed, so we must start the sequence
                viewer.playViewer();
                playButton.setGraphic(pauseIcon);
                viewerPlayingState = true;

                seqRemaining.setTime(viewer.getPlayingMetaSequence().getSequencesList().get(viewer.getCurrentSequenceIndex()).getDuration().getSeconds());
                metaSeqRemaining.setTime(getRemainingTimeInMetaSeq());
                setCounterLabel(numSeqLabel, viewer.getCurrentSequenceIndex()+1, viewer.getPlayingMetaSequence().getSequencesList().size());
                playAllClocks();
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
            duration.reset();

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
        resetAllClocks();
    }

    /**
     * Give the remaining time before finishing the meta-sequence
     * @return the duration in seconds
     */
    private long getRemainingTimeInMetaSeq(){
        long seconds = 0;
        MetaSequence metaSeq = viewer.getPlayingMetaSequence();

        // Sum all the next sequences including the current one
        for (int seqIndex=viewer.getCurrentSequenceIndex() ; seqIndex<metaSeq.getSequencesList().size() ; seqIndex++){
            seconds += metaSeq.getSequencesList().get(seqIndex).getDuration().getSeconds();
        }
        // Minus what we already played in the current sequence
        seconds -= seqDuration.getTime().getSecond();

        return seconds;
    }

    /**
     * Create all the timers of the information section
     */
    public void initTimers(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        metaSeqDuration  = new TimelineClock(metaSeqDurationLabel, dtf,
                0, 0, 0, ClockType.INCREMENTAL);
        metaSeqRemaining = new TimelineClock(metaSeqRemainingLabel, dtf,
                0, 0, 0, ClockType.DECREMENTAL);
        seqDuration      = new TimelineClock(seqDurationLabel, dtf,
                0, 0, 0, ClockType.INCREMENTAL);
        seqRemaining     = new TimelineClock(seqRemainingLabel, dtf,
                0, 0, 0, ClockType.DECREMENTAL);
        duration         = new TimelineClock(durationLabel, dtf,
                0, 0, 0, ClockType.INCREMENTAL);
        remaining        = new TimelineClock(remainingLabel, dtf,
                0, 0, 0, ClockType.DECREMENTAL);
    }

    /**
     * Pause all the clocks of the information section
     */
    private void pauseAllClocks(){
        duration.pause();
        remaining.pause();
        seqDuration.pause();
        metaSeqDuration.pause();
        seqRemaining.pause();
        metaSeqRemaining.pause();
    }

    /**
     * Reset all the clocks of the information section
     */
    private void resetAllClocks(){
        duration.reset();
        remaining.reset();
        seqDuration.reset();
        metaSeqDuration.reset();
        seqRemaining.reset();
        metaSeqRemaining.reset();
    }

    /**
     * Play or resume all the clocks of the information section
     */
    private void playAllClocks(){
        duration.play();
        remaining.play();
        seqDuration.play();
        metaSeqDuration.play();
        seqRemaining.play();
        metaSeqRemaining.play();
    }

    /**
     * Set a counter label
     * @param label concerned label
     * @param current actual number of the counter
     * @param total total number
     */
    private void setCounterLabel(Label label, int current, int total){
        label.setText(current + " / " + total);
    }
}

/**
 * Indicate the type of TimelineClock
 */
enum ClockType {
    INCREMENTAL,
    DECREMENTAL
}

/**
 * Independent controllable clock with its label
 */
class TimelineClock{
    @Getter
    private LocalTime time;
    @Getter @Setter
    private Timeline timeline;
    @Getter @Setter
    private Label timeLabel;
    private final DateTimeFormatter dtf;

    /**
     * Constructor with init values
     * @param label label to update
     * @param dtf format of teh clock
     * @param hours starting hours value
     * @param minutes starting minutes value
     * @param seconds starting seconds value
     * @param type indicates if the clock is incremental or decremental
     */
    public TimelineClock(Label label, DateTimeFormatter dtf, int hours, int minutes, int seconds, ClockType type){
        timeLabel = label;
        time = LocalTime.of(hours, minutes, seconds);
        this.dtf = dtf;

        // Thread executed every second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (type == ClockType.INCREMENTAL)
                time = time.plusSeconds(1);
            else if (type == ClockType.DECREMENTAL)
                time = time.plusSeconds(-1);
            timeLabel.setText(time.format(dtf));
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Pause the clock
     */
    public void pause(){
        timeline.pause();
    }

    /**
     * Play or resume the clock
     */
    public void play(){
        timeline.play();
    }

    /**
     * reset the clock and its label
     */
    public void reset(){
        timeline.pause();
        time = LocalTime.of(0, 0, 0);
        timeLabel.setText(time.format(dtf));
    }

    /**
     * Set the clock at a defined time in seconds
     * @param seconds amount of seconds
     */
    public void setTime(long seconds){
        time = LocalTime.of((int)(seconds / 3600),
                (int)((seconds % 3600) / 60),
                (int)(seconds % 60));
        timeLabel.setText(time.format(dtf));
    }

    /**
     * Set the clock to the remaining time from a reference and a deadline
     * @param reference the reference clock
     * @param deadLine the moment when the clock will reach zero
     */
    public void setRemaining(LocalTime reference, long deadLine){
        long seconds = deadLine - reference.getSecond();
        time = LocalTime.of((int)(seconds / 3600),
                (int)((seconds % 3600) / 60),
                (int)(seconds % 60));
        timeLabel.setText(time.format(dtf));
    }
}