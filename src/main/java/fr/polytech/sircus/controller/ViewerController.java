package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


/**
 * View window controller.
 * <p>
 * This class allows to manage the reading of the media and the view associated to this reading.
 */
public class ViewerController {

    // The MetaSequenceController which create this controller
    private final MetaSequenceController metaSequenceController;

    // The MetaSequence that id passed to the viewer
    private final MetaSequence playingMetaSequence;

    @FXML
    private MediaView mediaView;

    @FXML
    private ImageView imageView;

    // This allows the playing of media
    private MediaPlayer mediaPlayer;

    // Manage the stage of the media
    private Stage viewerStage = null;

    // The timeline allowing the reading of the media with management of the time of each one
    private Timeline timeline = null;

    // Boolean indicating if the meta-sequence has already been started once or not
    private boolean metaSequenceStarted;

    // Stores the start time of each sequence to be able to move between sequences.
    private final ArrayList<Integer> sequencesStartTime;

    private int currentSequenceIndex;

    private final int numberOfSequences;

    /**
     * Constructor of ViewerController class.
     *
     * @param owner the main Window.
     */
    public ViewerController(Window owner, MetaSequence metaSequence, MetaSequenceController metaSequenceController) {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/viewer.fxml")));
        fxmlLoader.setController(this);
        numberOfSequences = metaSequence.getSequencesList().size();

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

            viewerStage.initModality(Modality.NONE);
            viewerStage.initOwner(owner);
            viewerStage.setScene(viewerScene);
            viewerStage.setResizable(true);
            viewerStage.setTitle("Viewer");
            viewerStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playingMetaSequence = metaSequence;
        metaSequenceStarted = false;
        this.metaSequenceController = metaSequenceController;
        sequencesStartTime = new ArrayList<>();
        closingManager();
    }


    /**
     * Getter of the timeline attribute.
     *
     * @return timeline attribute.
     */
    @FXML
    public Timeline getTimeline() {
        return timeline;
    }

    /**
     * Display the media that is passed in parameter.
     *
     * @param media the media that we want to display.
     */
    @FXML
    private void showMedia(Media media) {
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaView.setMediaPlayer(mediaPlayer);
    }

    /**
     * Display the media from its name.
     *
     * @param name the name of the media that we want to display.
     */
    @FXML
    private void showMediaFromName(String name) {
        File mediaFile = new File("medias/" + name);
        try {
            Media media = new Media(mediaFile.toURI().toURL().toString());
            showMedia(media);
        }
        // If the URL is malformed, it is reported
        catch (MalformedURLException error) {
            System.out.println("URL malformée, le chemin vers la vidéo est incorrect.");
        }
    }

    /**
     * Removes the displayed media.
     */
    @FXML
    private void removeMedia() {
        if (mediaView.getMediaPlayer() != null) {
            mediaView.getMediaPlayer().pause();
            mediaView.setMediaPlayer(null);
        }
    }

    /**
     * Display the image that is passed in parameter.
     *
     * @param image the image that we want to display.
     */
    @FXML
    private void showImage(Image image) {
        imageView.setImage(image);
        imageView.setCache(true);
    }

    /**
     * Display the image from its name.
     *
     * @param name the name of the image that we want to display.
     */
    @FXML
    private void showImageFromName(String name) {
        // Try to create an InputStream with the path of the image.
        try {
            InputStream is = new FileInputStream("medias/" + name);
            Image image = new Image(is);

            imageView.setFitWidth(image.getWidth());
            imageView.setFitHeight(image.getHeight());

            showImage(image);
            centerImage();
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
     * Begin the playing of the meta-sequence that is passed in parameter.
     *
     * @param metaSequence the meta-sequence to play.
     */
    @FXML
    private void startMetaSequence(MetaSequence metaSequence) {
        timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);

        // Counter to count the total duration of the medias already played.
        int counterDuration = 0;
        int sequenceIndex = -1;

        for (Sequence sequence : metaSequence.getSequencesList()) {
            sequenceIndex++;
            sequencesStartTime.add(counterDuration);
            boolean addSequenceIndex = true;

            // For each Media in the sequence.
            for (fr.polytech.sircus.model.Media media : sequence.getListMedias()) {
                // If the media is an image.
                if (media.getType() == TypeMedia.PICTURE) {
                    if (addSequenceIndex) {
                        int finalSequenceIndex = sequenceIndex;
                        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                                event -> {
                                    removeMedia();
                                    showImageFromName(media.getName());
                                    currentSequenceIndex = finalSequenceIndex;
                                }));
                    } else {
                        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                                event -> {
                                    removeMedia();
                                    showImageFromName(media.getName());
                                }));
                    }

                    addSequenceIndex = false;

                    // We add to the counterDuration the duration of the media currently read.
                    counterDuration += media.getDuration().getSeconds();
                }
                // If the media is a video
                else if (media.getType() == TypeMedia.VIDEO) {
                    if (addSequenceIndex) {
                        int finalSequenceIndex = sequenceIndex;
                        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                                event -> {
                                    removeImage();
                                    showMediaFromName(media.getName());
                                    currentSequenceIndex = finalSequenceIndex;
                                }));
                    } else {
                        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                                event -> {
                                    removeImage();
                                    showMediaFromName(media.getName());
                                }));
                    }

                    addSequenceIndex = false;

                    // We add to the counterDuration the duration of the media currently read.
                    counterDuration += media.getDuration().getSeconds();
                }
                // If the current media has a "inter-stim".
                if (media.getInterStim() != null) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                            event -> {
                                removeMedia();
                                showImageFromName(media.getInterStim().getName());
                            }));

                    // We add to the counterDuration the duration of the "inter-stim" currently read.
                    counterDuration += media.getInterStim().getDuration().getSeconds();
                }
            }
        }


        // We add an event that removes the image or video at the end of the playback.
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                event -> {
                    removeMedia();
                    removeImage();
                    quitViewer();
                }));
        // The end of the playback is added to the listBeginningTimeMedia
        // This allows the button passing a media to trigger the end of the playback of the meta-sequence
        // if we are playing the last media.
        sequencesStartTime.add(counterDuration);

        // We start the timeline's stopwatch so that the events we have just created are triggered
        timeline.play();
    }

    /**
     * Pause playback.
     */
    @FXML
    public void pauseViewer() {
        if (timeline != null) {
            timeline.pause();
            if (mediaView.getMediaPlayer() != null) {
                mediaPlayer.pause();
            }
        }
    }

    /**
     * Starts playback from scratch if the meta-sequence has never been started. Otherwise, restarts the media playback.
     */
    @FXML
    public void playViewer() {
        if (metaSequenceStarted) {
            timeline.play();
            if (mediaView.getMediaPlayer() != null) {
                mediaPlayer.play();
            }
        } else {
            metaSequenceStarted = true;
            startMetaSequence(playingMetaSequence);
        }
    }

    /**
     * Jumps the timeline to the next sequence.
     */
    public void nextSequence() {
        if (currentSequenceIndex < numberOfSequences - 1) {
            timeline.jumpTo(new Duration(sequencesStartTime.get(currentSequenceIndex + 1) * 1000));
            currentSequenceIndex++;
        } else {
            timeline.jumpTo(new Duration(sequencesStartTime.get(sequencesStartTime.size() - 1) * 1000));
        }
    }

    /**
     * Jumps the timeline to the previous sequence.
     */
    public void previousSequence() {
        if (currentSequenceIndex > 0) {
            timeline.jumpTo(new Duration(sequencesStartTime.get(currentSequenceIndex - 1) * 1000));
            currentSequenceIndex--;
        } else {
            timeline.jumpTo(new Duration(0));
        }
    }

    /**
     * Quit the viewer
     */
    @FXML
    private void quitViewer() {
        metaSequenceController.closeViewer();
        viewerStage.close();
    }

    /**
     * The handle of this method is called when the user closes the viewer window.
     * It calls the appropriate method of the MetaSequence controller in order to
     * reset some attributes to default attributes and restart the viewer correctly.
     */
    private void closingManager() {
        viewerStage.setOnCloseRequest(event -> metaSequenceController.closeViewer());
    }

    /**
     * Method to center the imageview in the viewer.
     */
    private void centerImage() {
        if (imageView.getImage() != null) {
            double ratio = Math.min(viewerStage.getWidth() / imageView.getFitWidth(), viewerStage.getHeight() / imageView.getFitHeight());

            imageView.setFitWidth(imageView.getFitWidth() * ratio);
            imageView.setFitHeight(imageView.getFitHeight() * ratio);

            double w = (viewerStage.getWidth() - imageView.getFitWidth()) / 2;
            double h = (viewerStage.getHeight() - imageView.getFitHeight()) / 2;

            imageView.setTranslateX(w);
            imageView.setTranslateY(h);
        }
    }
}
