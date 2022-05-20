package fr.polytech.sircus.controller;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import fr.polytech.sircus.model.MediaDeprecated;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

/**
 * Sample Skeleton for 'previsualisation.fxml' Controller Class
 */
public class PreviewTimeline extends AnchorPane implements Initializable {

    /**
     * L'URL vers le fichier FXML qui contient la timeline
     */
    public static final java.net.URL FXML_RESSOURCE_URL = Objects.requireNonNull(PreviewTimeline.class.getClassLoader().getResource("views/preview-scrollpane.fxml"));

    /**
     * The size of the scroll bar
     */
    private final double SCROLLBAR_HEIGHT = 15;

    /**
     * The fxml loader
     */
    @Getter
    private final FXMLLoader timelineFXMLLoader = new FXMLLoader(FXML_RESSOURCE_URL);

    /**
     * The timeline FXML object
     */
    @FXML @Getter
    private ScrollPane timeline;

    /**
     * The content container of the timeline
     */
    @FXML @Getter
    private HBox timelineContent;

    /**
     * List of all the controller of items in the timeline
     */
    @Getter
    private List<PreviewTimelineItemController> listPreviewItemController;

    /**
     * The duration of all the media in the timeline
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
     *  Method called after injecting FXML objects from FXML objects
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Adding timeline to this objet (otherwise it will not be displayed)
        getChildren().add(timeline);

        //Defining anchor for timeline
        setTopAnchor(timeline, 0.0);
        setBottomAnchor(timeline, 0.0);
        setLeftAnchor(timeline, 0.0);
        setRightAnchor(timeline, 0.0);
    }


    /**
     * Method to clear all media in timeline
     */
    public void removeAllMedia(){
        listPreviewItemController.clear();
        timelineContent.getChildren().clear();
        duration = Duration.ZERO;
    }


    /**
     * Method to add a media in the timeline
     * @param media The media to add
     */
    public void addMedia(MediaDeprecated media){

        FXMLLoader itemLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("views/preview-item.fxml")));;
        VBox item;
        PreviewTimelineItemController controllerChild;

        try {
            //Loading item
            item = itemLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //Editing item
        controllerChild = itemLoader.getController();
        duration = duration.plus(media.getDuration());
        controllerChild.setFromMedia(media);
        controllerChild.setTime(duration);

        computeSize(controllerChild);

        //Entering : there is already a media in the timeline
        //        => sets the good border for the ruler of this previous item
        if(!listPreviewItemController.isEmpty()){
            listPreviewItemController.get(listPreviewItemController.size()-1).setDefaultRulerBorder();
        }

        listPreviewItemController.add(itemLoader.getController());
        controllerChild = itemLoader.getController();
        controllerChild.setFinalRulerBorder();


        if(item == null || timelineContent == null) return;

        timelineContent.getChildren().add(item);
        computeSize(itemLoader.getController());

    }


    /**
     * Method to add a list of media
     * @param mediaList The list of media we want to add
     */
    public void addListMedia(List<MediaDeprecated> mediaList){
        for(MediaDeprecated media : mediaList){
            addMedia(media);
        }
    }


    /**
     * Method to add all media in a Sequence
     * @param sequence The sequence for which we want to add the media
     */
    public void addSequence(Sequence sequence){
        addListMedia(sequence.getListMedias());
    }


    /**
     * Method to add all media in a MetaSequence
     * @param metaSequence The meta sequence for which we want to add the media
     */
    public void addMetaSequence(MetaSequence metaSequence){
        for(Sequence sequence : metaSequence.getSequencesList()){
            addSequence(sequence);
        }
    }


    /**
     * Getter of the list of media
     * @param mediaList The list of media
     */
    public void setMediaList(List<MediaDeprecated> mediaList){
        removeAllMedia();
        addListMedia(mediaList);
    }



    /**
     * Method that binds timeline elements to fit the best to the timeline size
     */
    public void computeSize(PreviewTimelineItemController controller){

        VBox rulerContainer = controller.getRulerContainer();
        ImageView imageView = controller.getImageView();

        imageView.minHeight(50);
        controller.getImageView().setPreserveRatio(true);
        imageView.fitHeightProperty().bind(
                timeline.heightProperty().subtract(rulerContainer.heightProperty()).subtract(SCROLLBAR_HEIGHT)
        );
    }
}
