package fr.polytech.sircus.controller;

import fr.polytech.sircus.model.AbstractMedia;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

/**
 * Class representing a preview timeline item.
 */
public class PreviewTimelineItemController implements Initializable {

    /**
     * The paths to the images
     */
    private final String MEDIAS_PATH = "medias/";

    /**
     * The thickness of the image border
     */
    public static final double IMAGE_BACKGROUND_BORDER_WIDTH = 2;

    @FXML @Getter @Setter
    private VBox itemContainer;

    @FXML @Getter
    private Text time;

    @FXML @Getter @Setter
    private Text filename;

    @FXML @Getter @Setter
    private ImageView imageView;

    @FXML @Getter @Setter
    private MediaView mediaView;

    @FXML @Getter @Setter
    private HBox ruler;

    @FXML @Getter @Setter
    private VBox rulerContainer;

    @FXML @Getter @Setter
    private StackPane imageStackPane;

    @FXML @Getter @Setter
    private BorderPane imageBorder;

    @Getter @Setter
    private AbstractMedia media;

    @Getter
    private PreviewTimeline timeline;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setTimeline(PreviewTimeline timeline) {
        this.timeline = timeline;
        initializeBindings();
    }

    /**
     * Initialize the bindings of the item elements.
     */
    public void initializeBindings() {

        if(timeline == null) {

            //Make sure that the image is centered with the item
            imageStackPane.maxWidthProperty().unbind();
            imageStackPane.maxWidthProperty().bind(itemContainer.maxWidthProperty());
            imageStackPane.maxHeightProperty().unbind();
            imageStackPane.maxHeightProperty().bind(imageBorder.maxHeightProperty());


            //Make sure that the Border used for the image border fits the image size plus the border width
            imageBorder.maxHeightProperty().unbind();
            imageBorder.maxHeightProperty().bind(imageView.fitHeightProperty().add(2 * IMAGE_BACKGROUND_BORDER_WIDTH));
            imageBorder.maxWidthProperty().unbind();
            //This is used to make the border pane fit the image width plus the border width
            imageBorder.maxWidthProperty().bind(
                    imageView.xProperty().multiply(
                            imageView.fitHeightProperty().divide(imageView.yProperty())
                    ).add(2 * IMAGE_BACKGROUND_BORDER_WIDTH)
            );

            return;
        }

        //Make sure that the item fits the timeline height
        itemContainer.maxHeightProperty().bind(timeline.getTimelineContent().prefHeightProperty());
        itemContainer.prefHeightProperty().bind(timeline.getTimelineContent().prefHeightProperty());

        //Resize the image to fit the timeline height when the timeline is resized
        imageView.minHeight(50);
        imageView.fitHeightProperty().unbind();
        imageView.fitHeightProperty().bind(
                itemContainer.maxHeightProperty().subtract(rulerContainer.heightProperty()).subtract(2 * IMAGE_BACKGROUND_BORDER_WIDTH)
        );

        //Resize the video to fit the timeline height when the timeline is resized
        mediaView.minHeight(50);
        mediaView.fitHeightProperty().unbind();
        mediaView.fitHeightProperty().bind(
                itemContainer.maxHeightProperty().subtract(rulerContainer.heightProperty()).subtract(2 * IMAGE_BACKGROUND_BORDER_WIDTH)
        );
    }

    /**
     * Method that sets the standard border for the item ruler.
     */
    public void setDefaultRulerBorder() {

        ruler.setBorder(new Border(
                new BorderStroke(
                        Paint.valueOf("black"),
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(0, 0, 2, 2)
                )
        ));
    }

    /**
     * Method that sets the border of the element's rule as the last one in the timeline.
     */
    public void setFinalRulerBorder() {
        ruler.setBorder(new Border(
                new BorderStroke(
                        Paint.valueOf("black"),
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(0, 2, 2, 2)
                )
        ));

    }

    @FXML
    public void setTime(Duration duration){
        time.setText(duration.getSeconds() + "s");
    }

    /**
     * Method that sets all the properties of the item according to a media.
     * @param media The media to set the item to.
     */
    public void setFromMedia(AbstractMedia media) {

        this.media = media;

        if (!media.getFilename().contains(".mp4"))
            setImage(MEDIAS_PATH + media.getFilePath());
        else
            setVideo(MEDIAS_PATH + media.getFilePath());
        filename.setText(media.getFilename());

        Color borderColor = Color.WHITE;

        if(media.getBackgroundColor() != null) {
            java.awt.Color color = media.getBackgroundColor();
            double opacity = color.getAlpha() / 255.0;
            borderColor = Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), opacity);
        }

        imageBorder.setBorder(new Border(
                new BorderStroke(
                        borderColor,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(IMAGE_BACKGROUND_BORDER_WIDTH)
                )
        ));
    }

    /**
     * Set image in imageView with the path.
     * @param pathToImage
     */
    private void setImage(String pathToImage) {
        try {
            FileInputStream fip = new FileInputStream(pathToImage);
            imageView.setImage(new Image(fip));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setVideo(String pathToVideo) {
        File video = new File(pathToVideo);
        MediaPlayer mediaPlayer = new MediaPlayer(new Media(video.toURI().toString()));
        mediaView.setMediaPlayer(mediaPlayer);
    }
}
