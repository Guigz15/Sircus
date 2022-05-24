package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MediaDeprecated;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Result;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

/**
 * Manages the interface used when the exam is in progress (player_monitor).
 */
public class PlayerMonitorController{

    @FXML
    private ProgressBar seqProgressBarFX;
    private TimelineProgressBar seqProgressBar;

    @FXML
    private ProgressBar metaSeqProgressBarFX;
    private TimelineProgressBar metaSeqProgressBar;

    @FXML
    private GridPane playerMonitor;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private Button playButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button backButton;

    @FXML
    private Button forwardButton;

    @FXML
    private StackPane previewPane;

    @FXML
    private MediaView mediaView;

    @FXML
    private ImageView imageView;

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
     * indicates if it's the first lecture or after a reset
     */
    private boolean firstPlay;

    /**
     * The Result to fill.
     */
    private Result result;

    // Viewer manager
    private ViewerController viewer;
    private Boolean viewerPlayingState;
    private FontIcon playIcon;
    private FontIcon pauseIcon;
    private MetaSequence metaSequence;

    @FXML
    public void initialize() {
        initTimers();
        this.result = new Result();
        // TODO: replace the used meta sequence by the one selected during the previous step
        this.metaSequence = SircusApplication.dataSircus.getMetaSequencesList().get(0);
        this.viewer = null;

        this.viewerPlayingState = false;

        this.playIcon = new FontIcon("fa-play");
        this.playIcon.setIconSize(15);

        this.pauseIcon = new FontIcon("fa-pause");
        this.pauseIcon.setIconSize(15);

        this.stopButton.setDisable(true);
        this.forwardButton.setDisable(true);
        this.backButton.setDisable(true);

        metaSeqProgressBar = new TimelineProgressBar(metaSeqProgressBarFX,
                metaSeqRemaining,
                0);
        seqProgressBar = new TimelineProgressBar(seqProgressBarFX,
                seqRemaining,
                0);

        firstPlay = true;
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
        if (viewer != null)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Mettre en pause");
            alert.setHeaderText("Êtes-vous sûr de vouloir mettre la lecture en pause ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                viewer.closeViewer();
                viewerPlayingState = false;
                resetAllClocks();
            } else {
                return;
            }
        }

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

            // progress bars
            metaSeqProgressBar.setTotalDuration(viewer.getPlayingMetaSequence().getDuration().getSeconds());
            seqProgressBar.setTotalDuration(viewer.getPlayingMetaSequence().getSequencesList().get(viewer.getCurrentSequenceIndex()).getDuration().getSeconds());
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

            // Clocks
            duration.pause();
            seqRemaining.setTime(viewer.getPlayingMetaSequence().getSequencesList().get(viewer.getCurrentSequenceIndex()).getDuration().getSeconds());
            metaSeqRemaining.setTime(getRemainingTimeInMetaSeq());
            setCounterLabel(numSeqLabel, viewer.getCurrentSequenceIndex()+1, viewer.getPlayingMetaSequence().getSequencesList().size());

            // progress bars
            metaSeqProgressBar.setTotalDuration(viewer.getPlayingMetaSequence().getDuration().getSeconds());
            seqProgressBar.setTotalDuration(viewer.getPlayingMetaSequence().getSequencesList().get(viewer.getCurrentSequenceIndex()).getDuration().getSeconds());
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

                if (stopButton.disableProperty().get()) {
                    this.stopButton.setDisable(false);
                    this.forwardButton.setDisable(false);
                    this.backButton.setDisable(false);
                }

                viewer.playViewer();
                playButton.setGraphic(pauseIcon);
                viewerPlayingState = true;

                // If it's the first lecture or after a reset
                if (firstPlay){
                    // Clocks
                    seqDuration.setTime(0);
                    seqRemaining.setTime(viewer.getPlayingMetaSequence().getSequencesList().get(viewer.getCurrentSequenceIndex()).getDuration().getSeconds());
                    metaSeqDuration.setTime(0);
                    metaSeqRemaining.setTime(getRemainingTimeInMetaSeq());
                    setCounterLabel(numSeqLabel, viewer.getCurrentSequenceIndex()+1, viewer.getPlayingMetaSequence().getSequencesList().size());

                    // progress bars
                    metaSeqProgressBar.setTotalDuration(viewer.getPlayingMetaSequence().getDuration().getSeconds());
                    seqProgressBar.setTotalDuration(viewer.getPlayingMetaSequence().getSequencesList().get(viewer.getCurrentSequenceIndex()).getDuration().getSeconds());
                    firstPlay = false;
                }

                // Clocks
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
            resetAllClocks();
            firstPlay = true;

            this.stopButton.setDisable(true);

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
        pauseAllClocks();
        viewer = null;
        viewerPlayingState = false;
        playButton.setGraphic(playIcon);

        this.stopButton.setDisable(true);
        this.forwardButton.setDisable(true);
        this.backButton.setDisable(true);
    }

    public void loadImage(MediaDeprecated media) throws FileNotFoundException {
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
        if (seconds < 0)
            seconds = 0;
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
        if (seconds < 0)
            seconds = 0;
        time = LocalTime.of((int)(seconds / 3600),
                (int)((seconds % 3600) / 60),
                (int)(seconds % 60));
        timeLabel.setText(time.format(dtf));
    }
}

/**
 * Independent controllable ProgressBar
 */
class TimelineProgressBar{
    @Getter @Setter
    private ProgressBar progressBar;
    @Getter @Setter
    private Timeline timeline;
    @Getter @Setter
    private double progress;
    @Setter
    private long totalDuration;

    /**
     * Main constructor
     * @param pb progress bar to assign
     * @param clock clock of remaining time
     * @param totalDuration time when the progressbar should be full
     */
    public TimelineProgressBar(ProgressBar pb, TimelineClock clock, long totalDuration){
        progressBar = pb;
        progress = 0.0;
        this.totalDuration = totalDuration;

        // Update progress bar and progress attribute depending on clock parameter twice a second
        timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            if (clock.getTime().getSecond() == 0){
                progress = 0.0;
            }
            else{
                progress = getCompletionRate(clock.getTime().getSecond(), this.totalDuration);
            }
            progressBar.setProgress(progress);
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Give a completion rate between 0 and 1 (1 is complete)
     * @param remaining time remaining in seconds
     * @param total total amount of time
     * @return completion rate between 0 and 1
     */
    public double getCompletionRate(long remaining, long total){
        return (total - remaining) / (double)total;
    }
}