package fr.polytech.sircus.utils;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Locale;

public class ImportSeqXML extends DefaultHandler {
    @Getter
    @Setter
    private Sequence seq;
    private final StringBuilder currentValue = new StringBuilder();

    private Boolean name = false;
    private Boolean filename = false;
    private Boolean duration = false;
    private Boolean type = false;
    private Boolean isInterstim = false;
    private Boolean interstim = false;
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
            Duration duration = Duration.parse(attributes.getValue("duration"));
            Boolean lock = Boolean.valueOf(attributes.getValue("lock"));

            seq = new Sequence(name);
            seq.setDuration(duration);
            seq.setLock(lock);
        } else if (qName.equalsIgnoreCase("listMedia")) {
            seq.setListMedias(new ArrayList<>());
        } else if (qName.equalsIgnoreCase("media")) {
            MediaDeprecated media = new MediaDeprecated();
            seq.getListMedias().add(media);
        } else if (qName.equalsIgnoreCase("name")) {
            name = true;
        } else if (qName.equalsIgnoreCase("filename")) {
            filename = true;
        } else if (qName.equalsIgnoreCase("duration")) {
            duration = true;
        } else if (qName.equalsIgnoreCase("type")) {
            type = true;
        } else if (qName.equalsIgnoreCase("isInterstim")) {
            isInterstim = true;
        } else if (qName.equalsIgnoreCase("interstim")) {
            if (seq.getListMedias().size() == 1) {
                if (seq.getListMedias().get(0).getIsInterstim()) {
                    // the media has an interstim, it is the previous one
                    interstim = true;
                } else {
                    seq.getListMedias().get(seq.getListMedias().size() - 1).setInterStim(null);
                }
            } else {
                if (seq.getListMedias().get(seq.getListMedias().size() - 2).getIsInterstim()) {
                    // the media has an interstim, it is the previous one
                    interstim = true;
                } else {
                    seq.getListMedias().get(seq.getListMedias().size() - 1).setInterStim(null);
                }
            }
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
        // name is true if we determine in startElement that name was read
        if (name) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setName(currentValue.toString().replace("%20", " "));
            name = false;
        } else if (filename) {
            String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            // test if the filename exist in media
            File file;
            if(osName.contains("win")) {
                file = new File(System.getProperty("user.dir") +"\\medias\\" + currentValue.toString().replace("%20", " "));
            } else {
                file = new File(System.getProperty("user.dir") +"/medias/" + currentValue.toString().replace("%20", " "));
            }

            if(!file.exists()){
                // the filename reference an object that doesn't exist
                seq.getListMedias().get(seq.getListMedias().size() - 1).setFilename("erreur.jpg");
                filename = false;

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Attention");
                alert.setContentText("Le fichier " + currentValue.toString().replace("%20", " ") +
                        " n'existe pas dans le dossier media.");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });

                // TODO: in etape 2, the sequence and the metaSequence need to be
                //  in red when they have an error
            } else {
                //everything is good
                seq.getListMedias().get(seq.getListMedias().size() - 1).setFilename(currentValue.toString().replace("%20", " "));
                filename = false;
            }
        } else if (duration) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setDuration(Duration.parse(currentValue.toString()));
            duration = false;
        } else if (type) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setType(TypeMedia.valueOf(currentValue.toString()));
            type = false;
        } else if (isInterstim) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setIsInterstim(Boolean.valueOf(currentValue.toString()));
            isInterstim = false;
        } else if (interstim) {
            Media inter;
            if (seq.getListMedias().size() == 1) {
                //inter = seq.getListMedias().get(1);
            } else {
                //inter = seq.getListMedias().get(seq.getListMedias().size() - 2);
            }
            //seq.getListMedias().get(seq.getListMedias().size() - 1).setInterStim(inter);
            interstim = false;
        } else if (lock) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setLock(Boolean.valueOf(currentValue.toString()));
            lock = false;
        } else if (isResizable) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setResizable(Boolean.parseBoolean(currentValue.toString()));
            isResizable = false;
        } else if (backgroundColor) {
            seq.getListMedias().get(seq.getListMedias().size() - 1).setBackgroundColor(Color.valueOf(currentValue.toString()));
            backgroundColor = false;
        }
    }

    /**
     * Is needed for the parsing of the file
     */
    @Override
    public void characters(char ch[], int start, int length) {
        currentValue.append(ch, start, length);
    }
}
