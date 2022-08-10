package fr.polytech.sircus.utils;

import fr.polytech.sircus.model.Interstim;
import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.model.TypeMedia;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import java.awt.*;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This class manages sequence importation
 */
public class ImportSeqXML extends DefaultHandler {
    @Getter @Setter
    private Sequence seq;
    private final StringBuilder currentValue = new StringBuilder();
    private Boolean filename = false;
    private Boolean filepath = false;
    private Boolean minDuration = false;
    private Boolean maxDuration = false;
    private Boolean type = false;
    private Boolean lock = false;
    private Boolean isResizable = false;
    private Boolean backgroundColor = false;

    public ImportSeqXML() {
        super();
    }

    /**
     * Read the name in the tags of xml and the potential attributes in them
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        // reset the tag value
        currentValue.setLength(0);

        // read the name in the tag, one at the time
        if (qName.equalsIgnoreCase("sequence")) {
            // read the attributes in the tag
            String name = attributes.getValue("name");
            name = name.replace("%20", " ");
            Duration minDuration = Duration.parse(attributes.getValue("minDuration"));
            Duration maxDuration = Duration.parse(attributes.getValue("maxDuration"));
            Boolean lock = Boolean.valueOf(attributes.getValue("lock"));

            seq = new Sequence(name);
            seq.setMinDuration(minDuration);
            seq.setMaxDuration(maxDuration);
            seq.setLock(lock);
        } else if (qName.equalsIgnoreCase("listMedia")) {
            seq.setListMedias(new ArrayList<>());
        } else if (qName.equalsIgnoreCase("media")) {
            Media media = new Media();
            seq.getListMedias().add(media);
        } else if (qName.equalsIgnoreCase("filename")) {
            filename = true;
        } else if (qName.equalsIgnoreCase("filepath")) {
            filepath = true;
        } else if (qName.equalsIgnoreCase("minDuration")) {
            minDuration = true;
        } else if (qName.equalsIgnoreCase("maxDuration")) {
            maxDuration = true;
        } else if (qName.equalsIgnoreCase("type")) {
            type = true;
        } else if (qName.equalsIgnoreCase("interstim")) {
            // read the attributes in the tag
            String filename = attributes.getValue("filename");
            filename = filename.replace("%20", " ");
            String filepath = attributes.getValue("filepath");
            filepath = filepath.replace("%20", " ");
            Duration minDuration = Duration.parse(attributes.getValue("minDuration"));
            Duration maxDuration = Duration.parse(attributes.getValue("maxDuration"));
            TypeMedia type = TypeMedia.valueOf(attributes.getValue("type"));
            boolean isResizable = Boolean.parseBoolean(attributes.getValue("isResizable"));
            Color backgroundColor = Color.decode(attributes.getValue("backgroundColor"));

            // the media of the interstim is the last one in the list of the sequence
            Media media = seq.getListMedias().get(seq.getListMedias().size() - 1);

            Interstim inter = new Interstim(filename, filepath, minDuration, maxDuration, type, isResizable, backgroundColor, media);
            media.setInterstim(inter);
        } else if (qName.equalsIgnoreCase("lock")) {
            lock = true;
        } else if (qName.equalsIgnoreCase("isResizable")) {
            isResizable = true;
        } else if (qName.equalsIgnoreCase("backgroundColor")) {
            backgroundColor = true;
        }
    }

    /**
     * Read the element in between the tags of xml
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        if (filename) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setFilename(currentValue.toString().replace("%20", " "));
            filename = false;
        }
        // filepath is true if we determine in startElement that filepath was read
        else if (filepath) {
            String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            // test if the filepath exist in media
            File file;
            if(osName.contains("win")) {
                file = new File(System.getProperty("user.dir") +"\\medias\\" + currentValue.toString().replace("%20", " "));
            } else {
                file = new File(System.getProperty("user.dir") +"/medias/" + currentValue.toString().replace("%20", " "));
            }

            if(!file.exists()){
                // the filepath reference an object that doesn't exist
                seq.getListMedias().get(seq.getListMedias().size() - 1).setFilename("Base/erreur.jpg");
                filepath = false;

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Attention");
                alert.setContentText("Le fichier " + currentValue.toString().replace("%20", " ") +
                        " n'existe pas dans le dossier media.");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            } else {
                //everything is good
                seq.getListMedias().get(seq.getListMedias().size() - 1).setFilePath(currentValue.toString().replace("%20", " "));
                filepath = false;
            }
        } else if (minDuration) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setMinDuration(Duration.parse(currentValue.toString()));
            minDuration = false;
        } else if (maxDuration) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setMaxDuration(Duration.parse(currentValue.toString()));
            maxDuration = false;
        } else if (type) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setTypeMedia(TypeMedia.valueOf(currentValue.toString()));
            type = false;
        } else if (lock) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setLocked(Boolean.parseBoolean(currentValue.toString()));
            lock = false;
        } else if (isResizable) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setResizable(Boolean.parseBoolean(currentValue.toString()));
            isResizable = false;
        } else if (backgroundColor) {
            if(currentValue.toString().equals("null")){
                seq.getListMedias().get(seq.getListMedias().size() - 1).setBackgroundColor(null);
            } else {
                seq.getListMedias().get(seq.getListMedias().size() - 1).setBackgroundColor(Color.decode(currentValue.toString()));
            }
            backgroundColor = false;
        }
    }

    /**
     * Is needed for the parsing of the file
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        currentValue.append(ch, start, length);
    }
}
