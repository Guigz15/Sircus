package fr.polytech.sircus.utils;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

public class ImportMetaSeqXML extends DefaultHandler {
    @Getter
    @Setter
    private MetaSequence meta;
    private final StringBuilder currentValue = new StringBuilder();

    private Boolean name = false;
    private Boolean minDuration = false;
    private Boolean maxDuration = false;
    private Boolean sequence = false;


    public ImportMetaSeqXML() {
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
        if (qName.equalsIgnoreCase("metaSequence")) {
            meta = new MetaSequence();
        } else if (qName.equalsIgnoreCase("name")) {
            name = true;
        } else if (qName.equalsIgnoreCase("minDuration")) {
            minDuration = true;
        } else if (qName.equalsIgnoreCase("maxDuration")) {
            maxDuration = true;
        } else if (qName.equalsIgnoreCase("listSequence")) {
            meta.setSequencesList(new ArrayList<>());
        } else if (qName.equalsIgnoreCase("sequence")) {
            sequence = true;
        }
    }

    /**
     * Read the element in between the tags of xml
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        // name is true if we determine in startElement that name was read
        if (name) {
            meta.setName(currentValue.toString().replace("%20", " "));
            name = false;
        } else if (minDuration) {
            meta.setMinDuration(Duration.parse(currentValue.toString()));
            minDuration = false;
        } else if (maxDuration) {
            meta.setMaxDuration(Duration.parse(currentValue.toString()));
            maxDuration = false;
        } else if (sequence) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                // read and parse the xml file of the sequence reference
                SAXParser saxParser = factory.newSAXParser();
                ImportSeqXML handler = new ImportSeqXML();

                File file = new File(SircusApplication.dataSircus.getPath().getSeqPath() + currentValue.toString().replace("%20", " "));
                if(!file.exists()) {
                    // the filename reference an object that doesn't exist
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Attention");
                    alert.setContentText("Le fichier " + currentValue.toString().replace("%20", " ") +
                            " n'existe pas dans le dossier sequence.");
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK.");
                        }
                    });
                } else{
                    saxParser.parse(file, handler);
                    // add the new sequence in the metaSequence
                    meta.getSequencesList().add(handler.getSeq());
                    meta.computeMinMaxDurations();
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
            sequence = false;
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
