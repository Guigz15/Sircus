package fr.polytech.sircus.controller;

import fr.polytech.sircus.model.Media;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.Duration;

/**
 * Sample Skeleton for 'previsualisation.fxml' Controller Class
 */
public class PreviewTimelineItemController {

    private final String MEDIAS_PATH = "medias/";

    @FXML @Getter @Setter
    private Text time;

    @FXML @Getter @Setter
    private Text filename;

    @FXML @Getter @Setter
    private ImageView imageView;

    @FXML @Getter @Setter
    private HBox ruler;

    @FXML @Getter @Setter
    private VBox rulerContainer;

    @FXML @Getter @Setter
    private Image image;


    @FXML
    public void setDefaultRulerBorder(){
        ruler.setBorder(new Border(
                new BorderStroke(
                        Paint.valueOf("black"),
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(0, 0, 2, 2)
                )
        ));
    }

    @FXML
    public void setFinalRulerBorder() {
        ruler.setBorder(new Border(
                new BorderStroke(
                        Paint.valueOf("black"),
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(0, 2, 2, 2))
        ));

    }

    @FXML
    public void setTime(Duration duration){
        time.setText(duration.getSeconds()+"s");
    }

    @FXML
    public void setFromMedia(Media media){
        String file = media.getFilename();
        setImage(MEDIAS_PATH+file);
        filename.setText(file);
    }

    @FXML
    private void setImage(String pathToImage){
        FileInputStream fip = null;

        try {
            fip = new FileInputStream(pathToImage);
            image = new Image(fip);
            imageView.setImage(image);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
