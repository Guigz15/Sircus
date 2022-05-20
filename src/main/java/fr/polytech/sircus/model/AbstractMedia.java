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
 * This class represents an abstract media (picture or video).
 */
public abstract class AbstractMedia implements Serializable {

    /**
     * Name of the file on the computer.
     */
    @Getter @Setter
    protected String filename;

    /**
     * Duration for which the media should be displayed.
     */
    @Getter @Setter
    protected Duration duration;

    /**
     * If isLocked == true, then it will stay in place in the sequence it belongs to.
     * If isLocked == false, then it's position will be randomized in the sequence with other unlocked medias.
     */
    @Getter @Setter
    protected boolean isLocked;

    /**
     * Determines if the media will be resized to take the maximum available size in the viewer.
     */
    @Getter @Setter
    protected boolean isResizable;

    /**
     * Color displayed behind the media in the viewer.
     */
    @Getter @Setter
    protected transient Color backgroundColor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractMedia)) return false;
        AbstractMedia media = (AbstractMedia) o;
        return getFilename().equals(media.getFilename()) &&
                getDuration().equals(media.getDuration()) &&
                Objects.equals(isLocked(), media.isLocked()) &&
                isResizable() == media.isResizable() &&
                getBackgroundColor() == media.getBackgroundColor();
    }

    public String toString() {
        return filename;
    }

    /**
     * Override of the writeObject method to handle the background color attribute which is not serializable.
     */
    protected void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeUTF(filename);
        oos.writeObject(duration);
        oos.writeBoolean(isLocked);
        oos.writeBoolean(isResizable);

        oos.writeDouble(backgroundColor.getRed());
        oos.writeDouble(backgroundColor.getGreen());
        oos.writeDouble(backgroundColor.getBlue());
        oos.writeDouble(backgroundColor.getOpacity());
    }

    /**
     * Override of the readObject method to handle the background color attribute which is not serializable.
     */
    protected void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        filename = ois.readUTF();
        duration = (Duration) ois.readObject();
        isLocked = ois.readBoolean();
        isResizable = ois.readBoolean();
        backgroundColor = new Color(ois.readDouble(), ois.readDouble(), ois.readDouble(), ois.readDouble());
    }
}
