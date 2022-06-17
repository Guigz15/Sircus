package fr.polytech.sircus.controller;

import fr.polytech.sircus.model.AbstractMedia;
import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class is the controller of the preview timeline.
 */
public class PreviewTimeline extends AnchorPane implements Initializable {

    /**
     * L'URL vers le fichier FXML qui contient la timeline.
     */
    public static final java.net.URL FXML_RESSOURCE_URL = Objects.requireNonNull(PreviewTimeline.class.getClassLoader().getResource("views/preview-scrollpane.fxml"));

    /**
     * The size of the scroll bar.
     */
    public static final double SCROLLBAR_HEIGHT = 15;

    /**
     * The fxml loader.
     */
    @Getter
    private final FXMLLoader timelineFXMLLoader = new FXMLLoader(FXML_RESSOURCE_URL);

    /**
     * The timeline FXML object.
     */
    @FXML @Getter
    private ScrollPane timeline;

    @FXML @Getter
    private Group group;

    /**
     * The content container of the timeline.
     */
    @FXML @Getter
    private HBox timelineContent;

    /**
     * List of all the controller of items in the timeline.
     */
    @Getter
    private List<PreviewTimelineItemController> listPreviewItemController;

    /**
     * The duration of all the media in the timeline.
     */
    @Getter
    private Duration duration;


    /**
     * Constructeur par défaut
     */
    public PreviewTimeline() {

        //Initialization
        listPreviewItemController = new ArrayList<>();
        duration = Duration.ZERO;

        //Defining this class as controller
        timelineFXMLLoader.setController(this);

        //Loading FXML file
        try {
            timeline = timelineFXMLLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }


    /**
     *  Method called after injecting FXML objects from FXML objects.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Adding timeline to this objet (otherwise it will not be displayed)
        getChildren().add(timeline);

        setBackground(new Background(new BackgroundFill(Paint.valueOf("BLACK"), null, null)));

        //Defining anchor for timeline
        setTopAnchor(timeline, 0.0);
        setBottomAnchor(timeline, 0.0);
        setLeftAnchor(timeline, 0.0);
        setRightAnchor(timeline, 0.0);

        //Replace the standard scroll (vertical) with a horizontal scroll
        timeline.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() != 0) {
                event.consume();
                timeline.setHvalue(timeline.getHvalue() - event.getDeltaY() / timelineContent.getWidth());
            }
        });

        //timelineContent.setBorder(new Border(new BorderStroke(Paint.valueOf("BLUE"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        timelineContent.maxHeightProperty().bind(timeline.heightProperty().subtract(SCROLLBAR_HEIGHT));
        timelineContent.prefHeightProperty().bind(timeline.heightProperty().subtract(SCROLLBAR_HEIGHT));
    }


    /**
     * Method to clear all media in timeline
     */
    public void removeAllMedia() {
        listPreviewItemController.clear();
        timelineContent.getChildren().clear();
        duration = Duration.ZERO;
    }

    public void clear(){
        removeAllMedia();
    }


    /**
     * Method to add a media in the timeline.
     * @param media The media to add.
     */
    public void addMedia(Media media){
        Sequence sequence = new Sequence(media.getFilename());
        sequence.addMedia(media);
        addSequence(sequence);
    }


    /**
     * Method to add a list of media.
     * @param mediaList The list of media we want to add.
     */
    public void addListMedia(List<Media> mediaList){
        Sequence sequence = new Sequence("");
        sequence.setListMedias(mediaList);
        addSequence(sequence);
    }


    /**
     * Method to add all media in a Sequence.
     * @param sequence The sequence for which we want to add the media
     */
    public void addSequence(Sequence sequence){

        MetaSequence metaSequence = new MetaSequence(sequence.getName());
        metaSequence.addSequence(sequence);

        addMetaSequence(metaSequence);
    }


    /**
     * Method to add all media in a MetaSequence.
     * @param metaSequence The meta sequence for which we want to add the media
     */
    public void addMetaSequence(MetaSequence metaSequence) {

        List<Media> listMedia;
        AbstractMedia media;
        int max;

        for (Sequence sequence : metaSequence.getSequencesList()) {
            listMedia = sequence.getListMedias();

            //Hide the content of the timeline
            timelineContent.setOpacity(0);

            for (Media currentMedia : listMedia) {

                media = currentMedia;
                max = 1;

                if (currentMedia.getInterstim() != null) {
                    media = currentMedia.getInterstim();
                    max = 2;
                }

                for (int i = 0; i < max; ++i) {

                    if (i == 1) {
                        media = currentMedia;
                    }

                    FXMLLoader itemLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("views/preview-item.fxml")));

                    VBox item;
                    PreviewTimelineItemController controllerChild;

                    try {
                        //Loading item
                        item = itemLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }


                    if (item == null || timelineContent == null) continue;


                    //Editing item
                    controllerChild = itemLoader.getController();
                    duration = duration.plus(media.getDuration());
                    controllerChild.setFromMedia(media);
                    controllerChild.setTime(duration);

                    //Entering : there is already a media in the timeline
                    //        => sets the good border for the ruler of this previous item
                    if (!listPreviewItemController.isEmpty()) {
                        listPreviewItemController.get(listPreviewItemController.size() - 1).setDefaultRulerBorder();
                    }

                    controllerChild.setTimeline(this);

                    listPreviewItemController.add(controllerChild);
                    controllerChild.setFinalRulerBorder();


                    timelineContent.getChildren().add(item);
                }
            }

            //Show the content of the timeline
            assert timelineContent != null;
            timelineContent.setOpacity(1);
        }
    }



    /**
     * Getter of the list of media
     * @param mediaList The list of media
     */
    public void setMediaList(List<Media> mediaList){
        clear();
        addListMedia(mediaList);
    }

    /**
     * This method replace the content of the timeline with the content of the meta sequence.
     * @param sequence The sequence we want to set in the timeline.
     */
    public void setSequence(Sequence sequence){
        clear();
        addSequence(sequence);
    }

    /**
     * This method replace the content of the timeline with the content of the meta sequence.
     * @param metaSequence The meta sequence we want to set in the timeline.
     */
    public void setMetaSequence(MetaSequence metaSequence){
        clear();
        addMetaSequence(metaSequence);
    }
}
