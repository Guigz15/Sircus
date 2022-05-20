package fr.polytech.sircus.utils;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
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
    @Getter @Setter
    private MetaSequence meta;
    private final StringBuilder currentValue = new StringBuilder();

    private Boolean name = false;
    private Boolean duration = false;
    private Boolean sequence = false;


    public ImportMetaSeqXML() {
        super();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        // reset the tag value
        currentValue.setLength(0);

        if (qName.equalsIgnoreCase("metaSequence")) {
            meta = new MetaSequence();
        }
        else if(qName.equalsIgnoreCase("name")){
            name = true;
        }
        else if(qName.equalsIgnoreCase("duration")){
            duration = true;
        }
        else if(qName.equalsIgnoreCase("listSequence")){
            meta.setSequencesList(new ArrayList<>());
        }
        else if (qName.equalsIgnoreCase("sequence")) {
            sequence = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (name) {
            meta.setName(currentValue.toString());
            name = false;
        } else if(duration) {
            meta.setDuration(Duration.parse(currentValue.toString()));
            duration = false;
        } else if(sequence) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser saxParser = factory.newSAXParser();
                ImportSeqXML handler = new ImportSeqXML();
                File file = new File(SircusApplication.dataSircus.getPath().getSeqPath() + currentValue.toString());
                saxParser.parse(file, handler);
                meta.getSequencesList().add(handler.getSeq());
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
            sequence = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        currentValue.append(ch, start, length);
    }
}
