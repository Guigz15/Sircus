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
 * This class represents an interstim.
 * An interstim is a neutral image used to recalibrate the patient's view.
 */
public class Interstim extends AbstractMedia implements Serializable {

    /**
     * The media to which the interstim is linked to.
     */
    @Getter @Setter
    private Media media;

    public Interstim(String filename, Duration duration, TypeMedia typeMedia, Media media) {
        this(filename, duration, typeMedia, false, Color.WHITE, media);
    }

    public Interstim(String filename, Duration duration, TypeMedia typeMedia, boolean isResizable, Color backgroundColor, Media media) {
        this.filename = filename;
        this.duration = duration;
        this.typeMedia = typeMedia;
        this.isLocked = true;
        this.isResizable = isResizable;
        this.backgroundColor = backgroundColor;
        this.media = media;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Interstim interstim = (Interstim) o;
        return Objects.equals(media, interstim.media);
    }

    /**
     * Override of the writeObject method to handle the background color attribute which is not serializable.
     */
    protected void writeObject(ObjectOutputStream oos) throws IOException {
        super.writeObject(oos);
        oos.writeObject(media);
    }

    /**
     * Override of the readObject method to handle the background color attribute which is not serializable.
     */
    protected void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        super.readObject(ois);
        media = (Media) ois.readObject();
    }
}
