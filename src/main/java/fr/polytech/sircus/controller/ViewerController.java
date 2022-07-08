package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.AbstractMedia;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.model.TypeMedia;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.Getter;

import java.io.*;
import java.net.MalformedURLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static fr.polytech.sircus.controller.Step2Controller.sortByValue;


/**
 * View window controller.
 * This class allows to manage the reading of the media and the view associated to this reading.
 */
public class ViewerController {

    // The PlayerMonitorController which create this controller
    private final PlayerMonitorController playerMonitorController;

    // The MetaSequence that id passed to the viewer
    @Getter
    private MetaSequence playingMetaSequence;

    @FXML
    private MediaView mediaView;

    @FXML
    private ImageView imageView;

    @FXML
    private StackPane stackPane;

    // This allows the playing of media
    private MediaPlayer mediaPlayer;

    // Manage the stage of the media
    private Stage viewerStage = null;

    // The timeline allowing the reading of the media with management of the time of each one
    @Getter
    private Timeline timeline = null;

    // Stores the start time of each sequence to be able to move between sequences.
    private final ArrayList<Double> sequencesStartTime;

    // Index of the current sequence index in the meta-sequence
    @Getter
    private int currentSequenceIndex;

    private int numberOfSequences;

    /**
     * Constructor of ViewerController class.
     */
    public ViewerController(Window owner, PlayerMonitorController playerMonitorController, MetaSequence playingMetaSequence) {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/viewer.fxml")));
        fxmlLoader.setController(this);

        this.playingMetaSequence = playingMetaSequence;

        try {
            Scene viewerScene = new Scene(fxmlLoader.load(), 1280, 720);
            viewerStage = new Stage();

            ObservableList<Screen> screens = Screen.getScreens();

            Rectangle2D bounds;

            if (screens.size() > 1) {
                bounds = screens.get(1).getBounds();
                viewerStage.setFullScreen(true);
            } else {
                bounds = screens.get(0).getBounds();
            }

            viewerStage.setX(bounds.getMinX());
            viewerStage.setY(bounds.getMinY());

            viewerStage.initOwner(owner);
            viewerStage.setScene(viewerScene);
            viewerStage.setResizable(true);
            viewerStage.setTitle("Viewer");
            viewerStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.playerMonitorController = playerMonitorController;
        sequencesStartTime = new ArrayList<>();
        viewerStage.setOnCloseRequest(event -> closeViewer());

        initializeMetaSequence();
    }

    /**
     * Display the media from its name.
     *
     * @param video the media containing the video that we want to display.
     */
    @FXML
    private void showVideo(AbstractMedia video) {
        File mediaFile = new File("medias/" + video.getFilename());
        try {
            Media media = new Media(mediaFile.toURI().toURL().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            mediaView.setMediaPlayer(mediaPlayer);
        }
        // If the URL is malformed, it is reported
        catch (MalformedURLException error) {
            System.out.println("URL mal formé, le chemin vers la vidéo est incorrect.");
        }
    }

    /**
     * Removes the displayed video.
     */
    @FXML
    private void removeVideo() {
        if (mediaView.getMediaPlayer() != null) {
            mediaView.getMediaPlayer().pause();
            mediaView.setMediaPlayer(null);
        }
    }

    /**
     * Display the image from its filename.
     *
     * @param media the media containing the image that we want to display.
     */
    @FXML
    private void showImage(AbstractMedia media) {
        // Try to create an InputStream with the path of the image.
        try {
            InputStream is = new FileInputStream("medias/" + media.getFilename());
            Image image = new Image(is);

            imageView.setFitWidth(image.getWidth());
            imageView.setFitHeight(image.getHeight());

            imageView.setImage(image);
            imageView.setCache(true);

            if (media.isResizable()) {
                resizeImage();
            }

            Color color = media.getBackgroundColor();
            String hexColor = String.format( "-fx-background-color: #%02X%02X%02X;",
                    (int)( color.getRed() * 255 ),
                    (int)( color.getGreen() * 255 ),
                    (int)( color.getBlue() * 255 ) );

            stackPane.setStyle(hexColor);

            playerMonitorController.loadImage(media);
        }
        // If the path is not found we display a message.
        catch (FileNotFoundException error) {
            System.out.println("Le fichier n'existe pas.");
        }
    }

    /**
     * Removes the displayed image.
     */
    @FXML
    private void removeImage() {
        imageView.setImage(null);
    }

    /**
     * Loads the meta sequences and creates the associated keyframes in the timeline.
     */
    @FXML
    private void initializeMetaSequence() {
        if (timeline != null) {
            timeline.jumpTo(timeline.getKeyFrames().get(timeline.getKeyFrames().size() - 1).getTime());
            timeline.pause();
        }

        timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);

        // Counter to count the total duration of the medias already played.
        double counterDuration = 0;
        int sequenceIndex = -1;

        numberOfSequences = playingMetaSequence.getSequencesList().size();

        if (playingMetaSequence.isMixedForever()) {
            //copy the list
            List<Sequence> copySequence = new ArrayList<>(playingMetaSequence.getSequencesList());
            //create Map to store the fixed sequences.
            HashMap<Sequence, Integer> fixedSequence = new HashMap<>();
            //find fixed sequences
            for (Sequence sequence : copySequence) {
                if (sequence.getLock()) {
                    fixedSequence.put(sequence, copySequence.indexOf(sequence));
                    playingMetaSequence.getSequencesList().remove(sequence);
                }
            }
            //Shuffle the list of sequences
            Collections.shuffle(playingMetaSequence.getSequencesList());

            //We sort the hashmap to insert elements in ascending order (on the index of insertion)
            fixedSequence = sortByValue(fixedSequence);
            //restore fixed sequences
            fixedSequence.forEach((sequence, integer) -> playingMetaSequence.getSequencesList().add(integer, sequence));
        }


        //shuffle the sequence
        for (Sequence sequence : playingMetaSequence.getSequencesList()) {
            //copy the list
            List<fr.polytech.sircus.model.Media> copyMedia = new ArrayList<>(sequence.getListMedias());
            //create Map to store the fixed medias.
            TreeMap<Integer, fr.polytech.sircus.model.Media> fixedMedia = new TreeMap<>();
            //find fixed medias
            for (int indexMedia = 0; indexMedia < copyMedia.size(); indexMedia++) {
                fr.polytech.sircus.model.Media currentMedia = copyMedia.get(indexMedia);
                if (currentMedia.isLocked()) {
                    fixedMedia.put(indexMedia, currentMedia);
                    sequence.getListMedias().remove(currentMedia);
                }
            }
            //Shuffle the list of medias
            Collections.shuffle(sequence.getListMedias());

            //restore fixed sequences
            fixedMedia.forEach((integer, media) -> sequence.getListMedias().add(integer, media));

            sequenceIndex++;
            sequencesStartTime.add(counterDuration);

            // Keyframe to notify the PlayerMonitor that we are playing a new sequence.
            int finalSequenceIndex = sequenceIndex;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                    event -> {
                        currentSequenceIndex = finalSequenceIndex;
                        playerMonitorController.sequenceChanged();
                    }));

            for (fr.polytech.sircus.model.Media media : sequence.getListMedias()) {
                if (media.getInterstim() != null) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                            event -> {
                                removeVideo();
                                showImage(media.getInterstim());
                                playerMonitorController.getResult().addViewerData(media.getInterstim().getFilename()
                                        + "\t" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
                            }));

                    // We add to the counterDuration the duration of the "interstim" currently read.
                    if (media.getInterstim().getDuration().toString().contains(".")) {
                        String[] secondPlusMillis = media.getInterstim().getDuration().toString().split("\\.");
                        counterDuration += Double.parseDouble(secondPlusMillis[0].substring(2) + "." +
                                secondPlusMillis[1].substring(0, secondPlusMillis[1].length() - 1));
                    } else {
                        counterDuration += media.getInterstim().getDuration().getSeconds();
                    }
                }

                if (media.getTypeMedia() == TypeMedia.PICTURE) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                            event -> {
                                removeVideo();
                                showImage(media);
                                playerMonitorController.getResult().addViewerData(media.getFilename()
                                        + "\t" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
                            }));

                    // We add to the counterDuration the duration of the media currently read.
                    if (media.getDuration().toString().contains(".")) {
                        String[] secondPlusMillis = media.getDuration().toString().split("\\.");
                        counterDuration += Double.parseDouble(secondPlusMillis[0].substring(2) + "." +
                                secondPlusMillis[1].substring(0, secondPlusMillis[1].length() - 1));
                    } else {
                        counterDuration += media.getDuration().getSeconds();
                    }
                }
                else if (media.getTypeMedia() == TypeMedia.VIDEO) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                            event -> {
                                removeImage();
                                showVideo(media);
                            }));

                    // We add to the counterDuration the duration of the media currently read.
                    counterDuration += media.getDuration().getSeconds();
                }
            }
        }

        // We add 0.1s at the end in order to trigger this event even
        // if there is only an empty meta-sequence (event should be triggered at 0s)
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration + 0.1),
                event -> {
                    removeVideo();
                    removeImage();

                    playerMonitorController.replayMetaSequence();
                }
        ));

        // The end of the playback is added to the sequencesStartTime
        // This allows the button passing a media to trigger the end of the playback of the meta-sequence
        // if we are playing the last media.
        sequencesStartTime.add(counterDuration);
    }

    /**
     * Pause playback.
     */
    @FXML
    public void pauseViewer() {
        timeline.pause();
        if (mediaView.getMediaPlayer() != null) {
            mediaPlayer.pause();
        }
    }

    /**
     * Starts playback from scratch if the meta-sequence has never been started. Otherwise, restarts the media playback.
     */
    @FXML
    public void playViewer() {
        timeline.play();
        if (mediaView.getMediaPlayer() != null) {
            mediaPlayer.play();
        }
    }

    /**
     * Jumps the timeline to the next sequence.
     */
    public void nextSequence() {
        if (currentSequenceIndex < numberOfSequences - 1) {
            currentSequenceIndex++;
            timeline.jumpTo(new Duration(sequencesStartTime.get(currentSequenceIndex) * 1000));
        } else {
            timeline.jumpTo(new Duration(sequencesStartTime.get(sequencesStartTime.size() - 1) * 1000));
        }
    }

    /**
     * Jumps the timeline to the previous sequence.
     */
    public void previousSequence() {
        if (currentSequenceIndex > 0) {
            currentSequenceIndex--;
            timeline.jumpTo(new Duration(sequencesStartTime.get(currentSequenceIndex) * 1000));
        } else {
            timeline.jumpTo(new Duration(0));
        }
    }

    /**
     * Returns to the beginning of the meta sequence.
     */
    public void resetMetaSequence() {
        timeline.jumpTo(new Duration(0));
        currentSequenceIndex = 0;
    }

    /**
     * Quit the viewer
     */
    @FXML
    public void closeViewer() {
        playerMonitorController.clearImage();
        playerMonitorController.closeViewer();
        timeline.pause();
        timeline = null;
        viewerStage.close();
    }

    /**
     * Method to resize the imageview in order to maximize width and/or height.
     */
    private void resizeImage() {
        if (imageView.getImage() != null) {
            double ratio = Math.min(viewerStage.getWidth() / imageView.getFitWidth(), viewerStage.getHeight() / imageView.getFitHeight());

            imageView.setFitWidth(imageView.getFitWidth() * ratio);
            imageView.setFitHeight(imageView.getFitHeight() * ratio);
        }
    }
}
