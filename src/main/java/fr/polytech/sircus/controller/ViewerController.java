package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.model.TypeMedia;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Objects;


/**
 * View window controller.
 * <p>
 * This class allows to manage the reading of the media and the view associated to this reading.
 */
public class ViewerController {


    //the MetaSequenceController which create this controller
    private final MetaSequenceController metaSequenceController;

    //the MetaSequence that id passed to the viewer
    private final MetaSequence playingMetaSequence;

    //The list containing the start times of the media of the current meta-sequence
    ArrayList<Integer> listBeginningTimeMedia;

    // UI components
    @FXML
    private MediaView mediaView;
    @FXML
    private ImageView imageView;

    //this allows the playing of media
    private MediaPlayer mediaPlayer;

    //manage the stage of the media
    private Stage viewerStage = null;

    //The timeline allowing the reading of the media with management of the time of each one
    private Timeline timeline = null;

    //Boolean indiquant si la méta-séquence a déjà été démarrée une fois ou pas
    private boolean metaSequenceStarted;

    /**
     * Constructor of ViewerController class.
     *
     * @param owner the main Window.
     */
    public ViewerController(Window owner, MetaSequence metaSequence, MetaSequenceController metaSequenceController) {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SircusApplication.class.getClassLoader().getResource("views/viewer.fxml")));
        fxmlLoader.setController(this);

        try {
            Scene dialogScene = new Scene(fxmlLoader.load(), 1600, 900);
            Stage dialog = new Stage();

            viewerStage = dialog;

            dialog.initModality(Modality.NONE);
            dialog.initOwner(owner);
            dialog.setScene(dialogScene);
            dialog.setResizable(true);
            dialog.setTitle("Viewer");
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playingMetaSequence = metaSequence;
        metaSequenceStarted = false;
        this.metaSequenceController = metaSequenceController;
        listBeginningTimeMedia = new ArrayList<>();
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
        // Si l'URL est malformée, on le signale
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
        // try de create a InputStream with the path of the image.
        try {
            InputStream is = new FileInputStream("medias/" + name);
            Image image = new Image(is);
            showImage(image);
        }
        // if the path is not found we display a message.
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

        for (Sequence sequence : metaSequence.getSequencesList()) {
            // For each Media in the sequence.
            for (fr.polytech.sircus.model.Media media : sequence.getListMedias()) {
                // If the media is an image.
                if (media.getType() == TypeMedia.PICTURE) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                            event -> {
                                removeMedia();
                                showImageFromName(media.getName());
                            }));
                    // The second at which the image starts is added to listBeginningTimeMedia.
                    listBeginningTimeMedia.add(counterDuration);

                    // We add to the counterDuration the duration of the media currently read.
                    counterDuration += media.getDuration().getSeconds();
                }
                //If the media is a video
                else if (media.getType() == TypeMedia.VIDEO) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                            event -> {
                                removeImage();
                                showMediaFromName(media.getName());
                                // System.out.println("Vidéo donnée.");
                            }));
                    // The second at which the video starts is added to listBeginningTimeMedia.
                    listBeginningTimeMedia.add(counterDuration);
                    // We add to the counterDuration the duration of the media currently read.
                    counterDuration += media.getDuration().getSeconds();
                }
                // If the current media has a "inter-stim".
                if (media.getInterStim() != null) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(counterDuration),
                            event -> {
                                removeMedia();
                                showImageFromName(media.getInterStim().getName());
                                // System.out.println("Image donnée.");
                            }));
                    // The second at which the "inter-stim" starts is added to listBeginningTimeMedia.
                    // We could comment this line if we don't want to go through the inter-stim with the buttons
                    // allowing to skip a media or to return to the previous media.
                    listBeginningTimeMedia.add(counterDuration);
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
                }));
        // The end of the playback is added to the listBeginningTimeMedia
        // This allows the button passing a media to trigger the end of the playback of the meta-sequence
        // if we are playing the last media.
        listBeginningTimeMedia.add(counterDuration);

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
     * Display the next media
     */
    public void nextMedia() {
        if (listBeginningTimeMedia.size() > 0) {
            // We define a buffer start date, at the first start date of the listBeginningTimeMedia (probably 0)
            Integer startDateFound = listBeginningTimeMedia.get(0);

            // For each start date of media
            for (Integer integer : listBeginningTimeMedia) {
                // We check if the starting date is higher than the current date of the timeline
                if (integer > timeline.getCurrentTime().toSeconds()) {
                    // We found the media
                    startDateFound = integer;
                    break;
                }
            }
            // javafx.util.Duration takes milliseconds in parameter of constructor, so we multiply by 1000 seconds
            timeline.jumpTo(new Duration(startDateFound * 1000));
        }
    }

    /**
     * Display the previous media
     */
    public void prevMedia() {
        if (listBeginningTimeMedia.size() > 0) {
            // We define a buffer start date, at the first start date of the listBeginningTimeMedia (probably 0)
            Integer startDateFound = listBeginningTimeMedia.get(0);

            // For each start date of media
            int i = 0;
            // This loop allows to find the index of the first media which will be launched after the current media
            while (i < listBeginningTimeMedia.size() && listBeginningTimeMedia.get(i) < timeline.getCurrentTime().toSeconds()) {
                i++;
            }

            // If "i" is greater than 0, the index "i-1" corresponds to the current media.
            // The media at index "i" corresponds to the next media.
            // So the previous media is at index "i-2".
            if (i > 1) {
                i -= 2;
                startDateFound = listBeginningTimeMedia.get(i);
            }

            // javafx.util.Duration takes milliseconds in parameter of constructor, so we multiply by 1000 seconds
            timeline.jumpTo(new Duration(startDateFound * 1000));
        }
    }

    /**
     * Quit the viewer
     */
    @FXML
    private void quitViewer() {
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
}
