package fr.polytech.sircus.model;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

/**
 * This class represents a media (picture or video)
 */
public class Media implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String filename;

    @Getter @Setter
    private Duration duration;

    @Getter @Setter
    private TypeMedia type;

    @Getter @Setter
    private Media interStim;

    @Getter @Setter
    private Boolean isInterstim;

    @Getter @Setter
    private Boolean lock;

    @Getter @Setter
    private boolean isResizable;

    @Getter @Setter
    private Color backgroundColor;


    /**
     * The default constructor
     */
    public Media(){}

    /**
     * The constructor
     *
     * @param name Media's name
     * @param filename File's name
     * @param duration Media's duration
     * @param type Media's type
     */
    public Media(String name, String filename, Duration duration, TypeMedia type, Media interStim) {
        this.name = name;
        this.filename = filename;
        this.duration = duration;
        this.type = type;
        this.interStim = interStim;
        this.isInterstim = false;
        this.lock = true;
        this.isResizable = false;
        this.backgroundColor = Color.WHITE;
    }

    /**
     * The copy constructor
     *
     * @param media Media to copy
     */
    public Media(Media media) {
        this.name = media.getName();
        this.filename = media.getFilename();
        this.duration = media.getDuration();
        this.type = media.getType();
        this.interStim = media.getInterStim();
        this.lock = media.getLock();
        this.isResizable = media.isResizable();
        this.backgroundColor = media.getBackgroundColor();
    }

    /**
     * Equals method that compare two media objet.
     *
     * @param o the media objet to compare.
     * @return boolean if the current media is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Media)) return false;
        Media media = (Media) o;
        return getName().equals(media.getName()) &&
                getFilename().equals(media.getFilename()) &&
                getDuration().equals(media.getDuration()) &&
                getType() == media.getType() &&
                getInterStim().equals(media.getInterStim()) &&
                Objects.equals(getLock(), media.getLock()) &&
                isResizable() == media.isResizable() &&
                getBackgroundColor() == media.getBackgroundColor();
    }

    /**
     * Override the method toString to display only the name
     *
     * @return Media's name
     */
    public String toString() {
        return name;
    }

    /**
     * Convert a media to XML
     * @return XML
     */
    public String toXML(){
        //TODO replace the space in string by %20
        String XML = "<media>\n" +
                "<name>" + name.replace(" ", "%20") + "</name>\n" +
                "<filename>" + filename.replace(" ", "%20") + "</filename>\n" +
                "<duration>" + duration + "</duration>\n" +
                "<type>" + type + "</type>\n" +
                "<isInterstim>" + isInterstim + "</isInterstim>\n";
        if(interStim == null){
            XML += "<interstim>null</interstim>\n" +
                    "<lock>" + lock + "</lock>\n" +
                    "<isResizable>" + isResizable + "</isResizable>\n" +
                    "<backgroundColor>" + backgroundColor + "</backgroundColor>\n" +
                    "</media>\n";
        } else {
            XML += "<interstim>" + interStim.getName().replace(" ", "%20") + "</interstim>\n" +
                    "<lock>" + lock + "</lock>\n" +
                    "<isResizable>" + isResizable + "</isResizable>\n" +
                    "<backgroundColor>" + backgroundColor + "</backgroundColor>\n" +
                    "</media>\n";
        }
        return XML;
    }
}
