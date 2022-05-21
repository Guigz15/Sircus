package fr.polytech.sircus.model;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

/**
 * This class represents a media (picture or video)
 */
public class MediaDeprecated implements Serializable {

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
    private MediaDeprecated interStim;

    @Getter @Setter
    private Boolean isInterstim;

    @Getter @Setter
    private Boolean lock;

    @Getter @Setter
    private boolean isResizable;

    @Getter @Setter
    private transient Color backgroundColor;

    /**
     * The default constructor
     */
    public MediaDeprecated(){};

    /**
     * The constructor
     *
     * @param name Media's name
     * @param filename File's name
     * @param duration Media's duration
     * @param type Media's type
     * @param interStim Another media as interstim. Can be null
     */
    public MediaDeprecated(String name, String filename, Duration duration, TypeMedia type, MediaDeprecated interStim) {
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
    public MediaDeprecated(MediaDeprecated media) {
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
        if (!(o instanceof MediaDeprecated)) return false;
        MediaDeprecated media = (MediaDeprecated) o;
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
     * Override of the writeObject method to handle the background color attribute which is not serializable.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeUTF(name);
        oos.writeUTF(filename);
        oos.writeObject(duration);
        oos.writeObject(type);
        oos.writeObject(interStim);
        oos.writeBoolean(isInterstim);
        oos.writeBoolean(lock);
        oos.writeBoolean(isResizable);

        oos.writeDouble(backgroundColor.getRed());
        oos.writeDouble(backgroundColor.getGreen());
        oos.writeDouble(backgroundColor.getBlue());
        oos.writeDouble(backgroundColor.getOpacity());
    }

    /**
     * Override of the readObject method to handle the background color attribute which is not serializable.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        name = ois.readUTF();
        filename = ois.readUTF();
        duration = (Duration) ois.readObject();
        type = (TypeMedia) ois.readObject();
        interStim = (MediaDeprecated) ois.readObject();
        isInterstim = ois.readBoolean();
        lock = ois.readBoolean();
        isResizable = ois.readBoolean();
        backgroundColor = new Color(ois.readDouble(), ois.readDouble(), ois.readDouble(), ois.readDouble());
    }
}
