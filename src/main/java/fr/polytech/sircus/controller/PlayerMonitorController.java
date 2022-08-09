package fr.polytech.sircus.controller;

import animatefx.animation.AnimationFX;
import animatefx.animation.Bounce;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.controller.PopUps.ParticipantCalibrationPopup;
import fr.polytech.sircus.model.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages the interface used when the exam is in progress (player_monitor).
 */
public class PlayerMonitorController {

    @Getter @Setter
    private MetaSequence metaSequenceToRead;
    @FXML
    private ProgressBar seqProgressBarFX;
    private TimelineProgressBar seqProgressBar;
    @FXML
    private ProgressBar metaSeqProgressBarFX;
    private TimelineProgressBar metaSeqProgressBar;
    @FXML
    private ListView<Comment> commentListView;
    @FXML
    private TextArea commentTextArea;
    @FXML
    private Button commentButton;
    @FXML
    private Circle blueCircle;
    @FXML
    private Circle redCircle;
    @FXML
    private Circle greenCircle;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button backButton;
    @FXML
    private Button forwardButton;
    @FXML
    private Pane cameraPane;
    private Webcam webcam = null;
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
    @FXML
    private Button previous;
    @FXML
    private Button participantCalibration;

    /**
     * indicates if it's the first lecture or after a reset
     */
    private boolean firstPlay;

    /**
     * The Result to fill.
     */
    @Getter
    private Result result;

    // Eye tracker process
    private Process process;

    // Viewer manager
    private ViewerController viewer;
    private Boolean viewerPlayingState;
    private FontIcon playIcon;
    private FontIcon pauseIcon;

    @FXML
    public void initialize() {
        initTimers();
        this.result = new Result();
        this.viewer = null;

        this.viewerPlayingState = false;

        this.playIcon = new FontIcon("fa-play");
        this.playIcon.setIconSize(15);

        this.pauseIcon = new FontIcon("fa-pause");
        this.pauseIcon.setIconSize(15);

        this.stopButton.setDisable(true);
        this.forwardButton.setDisable(true);
        this.backButton.setDisable(true);

        metaSeqProgressBar = new TimelineProgressBar(metaSeqProgressBarFX, metaSeqRemaining, 0);
        seqProgressBar = new TimelineProgressBar(seqProgressBarFX, seqRemaining, 0);

        firstPlay = true;

        // To set webcam flow in camera's pane
        SwingNode swingNode = new SwingNode();
        createWebcamAndSetSwingContent(swingNode);
        cameraPane.getChildren().add(swingNode);

        // To make little circles bounce
        Bounce blueCircleBounce = new Bounce(blueCircle);
        blueCircleBounce.setCycleCount(AnimationFX.INDEFINITE);
        Bounce redCircleBounce = new Bounce(redCircle);
        redCircleBounce.setCycleCount(AnimationFX.INDEFINITE);
        redCircleBounce.setDelay(Duration.valueOf("100ms"));
        Bounce greenCircleBounce = new Bounce(greenCircle);
        greenCircleBounce.setCycleCount(AnimationFX.INDEFINITE);
        greenCircleBounce.setDelay(Duration.valueOf("200ms"));
        commentTextArea.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                blueCircleBounce.play();
                redCircleBounce.play();
                greenCircleBounce.play();
            }
        });

        // To allow text to wrap in each cell and defined context menu behavior
        commentListView.setCellFactory((ListView<Comment> param) -> new ListCell<>() {
            @Override
            protected void updateItem(Comment item, boolean empty) {
                super.updateItem(item, empty);

                ContextMenu contextMenu = new ContextMenu();
                MenuItem editItem = new MenuItem("Modifier");
                editItem.setOnAction(event -> startEdit());
                MenuItem deleteItem = new MenuItem("Supprimer");
                deleteItem.setOnAction(event -> {
                    result.getComments().remove(getIndex());
                    commentListView.getItems().remove(getItem());
                });
                contextMenu.getItems().addAll(editItem, deleteItem);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setContextMenu(null);
                } else {
                    setMinWidth(param.getWidth());
                    setMaxWidth(param.getWidth());
                    setPrefWidth(param.getWidth());

                    setWrapText(true);

                    setText(item.toString());
                    setContextMenu(contextMenu);
                }
            }

            private final TextField textField = new TextField();
            {
                textField.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
                    //if we click on enter we commit the modification
                    if (e.getCode() == KeyCode.ENTER)
                        commitEdit(getItem());

                    //if we click on escape we cancel the editing
                    if (e.getCode() == KeyCode.ESCAPE)
                        cancelEdit();
                });
            }


            @Override
            public void startEdit() {
                super.startEdit();
                textField.setText(getItem().getComment());
                setText(null);
                setGraphic(textField);
                textField.selectAll();
                textField.requestFocus();
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem().toString());
                setGraphic(null);
            }

            @Override
            public void commitEdit(Comment comment) {
                comment.setComment(textField.getText());
                result.getComments().set(getIndex(), comment);
                super.commitEdit(comment);
                setText(comment.toString());
                setGraphic(null);
            }
        });

        // Save comment in result and stop bounce
        commentButton.setOnAction(actionEvent -> {
            if (!this.commentTextArea.getText().isEmpty()) {
                this.result.addComment(this.commentTextArea.getText());
                this.commentListView.getItems().add(new Comment(this.commentTextArea.getText()));
            }
            this.commentTextArea.clear();

            blueCircleBounce.stop();
            blueCircleBounce.resetNode();
            redCircleBounce.stop();
            redCircleBounce.resetNode();
            greenCircleBounce.stop();
            greenCircleBounce.resetNode();
        });

        // To stop little circles bounce if comment text area is empty and not focused
        commentTextArea.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1 && commentTextArea.getText().isEmpty()) {
                blueCircleBounce.stop();
                blueCircleBounce.resetNode();
                redCircleBounce.stop();
                redCircleBounce.resetNode();
                greenCircleBounce.stop();
                greenCircleBounce.resetNode();
            }
        });

        // To launch python script that make participant calibration
        participantCalibration.setOnAction(actionEvent -> {
            FXMLLoader fxmlLoader = new FXMLLoader(SircusApplication.class.getClassLoader().getResource("views/popups/participant_calibration_popup.fxml"));
            DialogPane dialogPane;
            try {
                dialogPane = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ParticipantCalibrationPopup controller = fxmlLoader.getController();

            Button cancelButton = (Button) dialogPane.lookupButton(controller.getCancel());
            cancelButton.setCancelButton(true);
            Button calibrateButton = (Button) dialogPane.lookupButton(controller.getCalibrate());
            calibrateButton.setDefaultButton(true);
            calibrateButton.setStyle("-fx-background-color: #457b9d;");
            calibrateButton.setTextFill(javafx.scene.paint.Paint.valueOf("white"));

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Calibration Participant");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(participantCalibration.getScene().getWindow());
            Window window = dialogPane.getScene().getWindow();
            window.setOnCloseRequest(e -> dialog.hide());

            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.isPresent()) {
                if (clickedButton.get() == ButtonType.FINISH) {
                    Color backgroundColor = controller.getBackgroundColor().getValue();
                    Color targetColor = controller.getTargetColor().getValue();
                    ExecutorService threadPool = Executors.newWorkStealingPool();
                    threadPool.execute(() -> {
                        try {
                            String command = "python PatientCalibration.py " +
                                    controller.getTargetNumber().getValue() + " " + controller.getRandomizeTarget().isSelected() + " " +
                                    String.format("#%02X%02X%02X", (int)(backgroundColor.getRed() * 255), (int)(backgroundColor.getGreen() * 255), (int)(backgroundColor.getBlue() * 255));
                            if (controller.getTargetButton().isSelected())
                                command += " target:" + String.format("#%02X%02X%02X", (int)(targetColor.getRed() * 255), (int)(targetColor.getGreen() * 255), (int)(targetColor.getBlue() * 255));
                            if (controller.getImageButton().isSelected())
                                command += " image:" + controller.getTargetImage().getImage().getUrl().substring(6);
                            if (controller.getVideoButton().isSelected())
                                command += " video:" + controller.getTargetVideo().getMediaPlayer().getMedia().getSource();
                            Process process = Runtime.getRuntime().exec(command);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            String out;
                            while ((out = reader.readLine()) != null) {
                                System.out.println(out);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    threadPool.shutdown();
                }
            }
        });
    }

    /**
     * Instantiate webcam, create a webcam panel and convert it in a JFX object.
     * @param swingNode that welcome webcam panel
     */
    private void createWebcamAndSetSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            if (Webcam.getWebcams().size() == 1)
                webcam = Webcam.getDefault();
            else
                webcam = Webcam.getWebcams().get(1);
            webcam.setViewSize(WebcamResolution.QVGA.getSize());
            WebcamPanel panel = new WebcamPanel(webcam);
            panel.setMirrored(true);
            swingNode.setContent(panel);
        });
    }

    /**
     * Save results of the experiment in xml file.
     */
    public void saveResult() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now();
        Files.createDirectories(Path.of(SircusApplication.dataSircus.getPath().getResultPath() + localDateTime.getYear()
                + "\\" + localDateTime.getMonthValue() + "\\" + localDateTime.getDayOfMonth() + "\\" + localDateTime.getHour()
                + "\\" + localDateTime.getMinute()));

        File resultFile = new File(SircusApplication.dataSircus.getPath().getResultPath() +
                localDateTime.getYear() + "\\" + localDateTime.getMonthValue() + "\\" +
                localDateTime.getDayOfMonth() + "\\" + localDateTime.getHour()
                + "\\" + localDateTime.getMinute() + "\\" + metaSequenceToRead.getName()
                + " - " + SircusApplication.participant.getIdentifier() + ".xml");

        PrintWriter writer = new PrintWriter(resultFile);
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + result.toXML());
        writer.close();
    }

    /**
     * Go back to the previous page.
     */
    public void previousPage() throws IOException {
        if (viewer != null)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Quitter la lecture");
            alert.setHeaderText("Êtes-vous sûr de vouloir quitter la lecture ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                viewer.closeViewer();
                viewerPlayingState = false;
                resetAllClocks();
            } else {
                return;
            }
        }

        // Close the webcam if we go back to the previous page
        webcam.close();

        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/meta_seq.fxml")));
        Scene scene = new Scene(fxmlLoader.load(), previous.getScene().getWidth(), previous.getScene().getHeight());
        Step2Controller controller = fxmlLoader.getController();
        controller.getMetaListView().getSelectionModel().select(metaSequenceToRead);
        Stage stage = (Stage) previous.getScene().getWindow();
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
            this.result.addLog("Retour à la séquence " + (viewer.getCurrentSequenceIndex() + 1));
            pauseAllClocks();
            viewer.previousSequence();

            stopButton.setDisable(viewer.getCurrentSequenceIndex() == 0);

            if (viewerPlayingState) {
                viewer.pauseViewer();
                playButton.setGraphic(playIcon);
                viewerPlayingState = false;
            }
        }

        // Will be already called but updates info without having to press play button
        sequenceChanged();
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
            this.result.addLog("Avance à la séquence " + (viewer.getCurrentSequenceIndex() + 1));
            pauseAllClocks();
            viewer.nextSequence();
            stopButton.setDisable(false);

            if (viewerPlayingState) {
                viewer.pauseViewer();
                playButton.setGraphic(playIcon);
                viewerPlayingState = false;
            }
        }

        // Will be already called but updates info without having to press play button
        sequenceChanged();
    }

    /**
     * Let the user choose if he wants to replay the meta-sequence or not
     */
    public void replayMetaSequence() {

        viewer.pauseViewer();
        process.destroy();
        result.addLog("Fin de l'expérience");
        try {
            saveResult();
            result.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Meta séquence terminée");
        alert.setHeaderText("Le taux d'acquisition pour cette meta-séquence est de {placeholder}%.");
        alert.setContentText("Voulez-vous rejouer la métaséquence ?");
        alert.setOnHidden(dialogEvent -> {
            Optional<ButtonType> result = Optional.ofNullable(alert.getResult());

            if (result.isPresent()) {
                if (result.get() == ButtonType.YES) {
                    viewer.resetMetaSequence();
                    resetAllClocks();
                    firstPlay = true;

                    this.stopButton.setDisable(true);

                    playButton.setGraphic(playIcon);
                    viewerPlayingState = false;
                }
                else if (result.get() == ButtonType.NO) {
                    viewer.closeViewer();
                    webcam.close();

                    // Go to previous page to launch another sequence
                    FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/meta_seq.fxml")));
                    Scene scene;
                    try {
                        scene = new Scene(fxmlLoader.load());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Step2Controller controller = fxmlLoader.getController();
                    controller.getMetaListView().getSelectionModel().select(metaSequenceToRead);
                    Stage stage = (Stage) previous.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                }
            }
        });

        alert.show();
    }

    /**
     * Starts/Pauses the viewer or creates one if none is opened.
     */
    @FXML
    public void playViewer() {
        if (viewer == null) {
            for (Sequence sequence : metaSequenceToRead.getSequencesList()) {
                for (Media media : sequence.getListMedias()) {
                    media.setDuration(media.getRandomDuration());
                    if (media.getInterstim() != null)
                        media.getInterstim().setDuration(media.getInterstim().getRandomDuration());
                }
            }

            metaSequenceToRead.computeDuration();
            remaining.setTime(remaining.getTime().getSecond() + metaSequenceToRead.getDuration().getSeconds());

            result.setMetaSequenceUsed(metaSequenceToRead);

            viewer = new ViewerController(this.playButton.getScene().getWindow(), this, metaSequenceToRead);
            forwardButton.setDisable(viewer.getCurrentSequenceIndex() + 1 == viewer.getPlayingMetaSequence().getSequencesList().size());
        } else {
            // We are playing something, so the pause button is displayed, so we must pause the sequence
            if (viewerPlayingState) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Mettre en pause");
                alert.setHeaderText("Êtes-vous sûr de vouloir mettre la lecture en pause ?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    this.result.addLog("Mise en pause de l'expérience");
                    process.destroy();
                    viewer.pauseViewer();
                    playButton.setGraphic(playIcon);
                    viewerPlayingState = false;
                    pauseAllClocks();
                }
            } else {
                // We aren't playing something, so the play button is displayed, so we must start the sequence
                playButton.setGraphic(pauseIcon);
                viewerPlayingState = true;


                // Launch acquisition on another thread
                ExecutorService threadPool = Executors.newWorkStealingPool();
                threadPool.execute(() -> {
                    try {
                        process = Runtime.getRuntime().exec("python TobiiAcquisition.py " + metaSequenceToRead.getDuration().getSeconds());
                        Thread.sleep(3000); // Wait for eye tracker to launch
                        this.result.addLog("Lancement de l'expérience");
                        viewer.playViewer();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String out;
                        while ((out = reader.readLine()) != null) {
                            result.addEyeTrackerData(out);
                        }
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

                // If it's the first lecture or after a reset
                if (firstPlay){
                    stopButton.setDisable(false);
                    setCounterLabel(numMetaSeqLabel, SircusApplication.dataSircus.getMetaSequencesList().indexOf(metaSequenceToRead) + 1, SircusApplication.dataSircus.getMetaSequencesList().size());

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
        alert.getDialogPane().applyCss();
        Node graphic = alert.getDialogPane().getGraphic();

        CheckBox saveOption = new CheckBox("Voulez-vous sauvegarder les données ?");
        alert.setDialogPane(new DialogPane() {
            @Override
            protected Node createDetailsButton() {
                return saveOption;
            }
        });

        alert.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        alert.setContentText("Êtes-vous sûr de vouloir arrêter la méta-séquence et la réinitialiser ?");
        alert.getDialogPane().setExpandableContent(new Group());
        alert.getDialogPane().setExpanded(true);
        alert.getDialogPane().setGraphic(graphic);
        alert.setTitle("Réinitialiser la méta-séquence");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.YES) {
                viewer.resetMetaSequence();
                resetAllClocks();
                firstPlay = true;
                process.destroy();
                this.result.addLog("Arrêt et réinitialisation de l'expérience");

                if (saveOption.isSelected()) {
                    try {
                        saveResult();
                        this.result.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                this.stopButton.setDisable(true);

                if (viewerPlayingState) {
                    viewer.pauseViewer();
                    playButton.setGraphic(playIcon);
                    viewerPlayingState = false;
                }
            } else
                viewer.playViewer();
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

    /**
     * Display the image from its filename.
     *
     * @param media the media containing the image that we want to display.
     */
    public void loadImage(AbstractMedia media) throws FileNotFoundException {
        InputStream is = new FileInputStream("medias/" + media.getFilename());
        Image image = new Image(is);

        imageView.setImage(image);
        imageView.setCache(true);

        resizeImage();

        java.awt.Color color = media.getBackgroundColor();
        String hexColor = String.format( "-fx-border-color:black; -fx-border-width: 1; -fx-border-style: solid; " +
                        "-fx-background-color: #%02X%02X%02X;", color.getRed(), color.getGreen(), color.getBlue());

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
     * Update the information when the sequence changes (and by extension when the meta-sequence changes)
     */
    public void sequenceChanged(){
        remaining.setTime(getRemainingTime());

        seqDuration.setTime(0);
        seqRemaining.setTime(viewer.getPlayingMetaSequence().getSequencesList().get(viewer.getCurrentSequenceIndex()).getDuration().getSeconds());
        setCounterLabel(numSeqLabel, viewer.getCurrentSequenceIndex()+1, viewer.getPlayingMetaSequence().getSequencesList().size());
        seqProgressBar.setTotalDuration(viewer.getPlayingMetaSequence().getSequencesList().get(viewer.getCurrentSequenceIndex()).getDuration().getSeconds());

        metaSeqDuration.setTime(viewer.getPlayingMetaSequence().getDuration().getSeconds() - getRemainingTimeInMetaSeq());
        metaSeqRemaining.setTime(getRemainingTimeInMetaSeq());
        metaSeqProgressBar.setTotalDuration(viewer.getPlayingMetaSequence().getDuration().getSeconds());

        // Disable if last sequence
        forwardButton.setDisable(viewer.getCurrentSequenceIndex()+1 == viewer.getPlayingMetaSequence().getSequencesList().size());
        // Disable if first sequence
        backButton.setDisable(viewer.getCurrentSequenceIndex() == 0);
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
    private long getRemainingTimeInMetaSeq() {
        long seconds = 0;
        MetaSequence metaSeq = viewer.getPlayingMetaSequence();

        // Sum all the next sequences including the current one
        for (int seqIndex = viewer.getCurrentSequenceIndex(); seqIndex < metaSeq.getSequencesList().size(); seqIndex++){
            seconds += metaSeq.getSequencesList().get(seqIndex).getDuration().getSeconds();
        }
        // Minus what we already played in the current sequence
        seconds -= seqDuration.getTime().getSecond();

        return seconds;
    }

    /**
     * Give the remaining time before finishing all the meta-sequences
     * @return the duration in seconds
     */
    private long getRemainingTime(){
        return metaSequenceToRead.getDuration().getSeconds();
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

    /**
     * Indicate the type of TimelineClock
     */
    enum ClockType {
        INCREMENTAL,
        DECREMENTAL
    }

    /**
     * Independent controllable clock with its label
     * Inner class of PlayerMonitorController
     */
    static class TimelineClock{
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
     * Inner class of PlayerMonitorController
     */
    static class TimelineProgressBar{
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
                if (clock.getTime().toSecondOfDay() == 0){
                    progress = 0.0;
                }
                else{
                    progress = getCompletionRate(clock.getTime().toSecondOfDay(), this.totalDuration);
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
}

