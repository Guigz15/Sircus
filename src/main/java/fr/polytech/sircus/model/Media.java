package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.Duration;

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
    private Boolean lock;


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
        this.lock = true;
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
    }

    /**
     * Override the method toString to display only the name
     *
     * @return Media's name
     */
    public String toString() {
        return name;
    }
}
