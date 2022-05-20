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
 * This class represents a media (picture or video).
 */
public class Media extends AbstractMedia implements Serializable {

    /**
     * The interstim to be displayed before the media.
     */
    @Getter @Setter
    private Interstim interstim;

    public Media(String filename, Duration duration) {
        this(filename, duration, true, false, Color.WHITE, null);
    }

    public Media(String filename, Duration duration, boolean isLocked, boolean isResizable, Color backgroundColor, Interstim interstim) {
        this.filename = filename;
        this.duration = duration;
        this.isLocked = isLocked;
        this.isResizable = isResizable;
        this.backgroundColor = backgroundColor;
        this.interstim = interstim;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Media media = (Media) o;
        return Objects.equals(interstim, media.interstim);
    }

    /**
     * Override of the writeObject method to handle the background color attribute which is not serializable.
     */
    protected void writeObject(ObjectOutputStream oos) throws IOException {
        super.writeObject(oos);
        oos.writeObject(interstim);
    }

    /**
     * Override of the readObject method to handle the background color attribute which is not serializable.
     */
    protected void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        super.readObject(ois);
        interstim = (Interstim) ois.readObject();
    }
}
