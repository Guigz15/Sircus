package fr.polytech.sircus.model;

import fr.polytech.sircus.SircusApplication;
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

    public Interstim(String filename, Duration minDuration, Duration maxDuration, TypeMedia typeMedia, Media media) {
        this(filename, minDuration, maxDuration, typeMedia, false, Color.WHITE, media);
    }

    public Interstim(String filename, Duration minDuration, Duration maxDuration, TypeMedia typeMedia, boolean isResizable, Color backgroundColor, Media media) {
        this.filename = filename;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.duration = Duration.ZERO;
        this.typeMedia = typeMedia;
        this.isLocked = true;
        this.isResizable = isResizable;
        this.backgroundColor = backgroundColor;
        this.media = media;
        this.media.setInterstim(this);
    }

    public Interstim(Interstim interstim) {
        this(interstim.getFilename(), interstim.getMinDuration(), interstim.getMaxDuration(), interstim.getTypeMedia(), interstim.isResizable(), interstim.getBackgroundColor(), interstim.getMedia());
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

    /**
     * Convert an interstim to XML
     * @return XML
     */
    public String toXML() throws Exception {
        String XML = "<interstim filename=\"" + filename.replace(" ", "%20") +
                "\" minDuration=\"" + minDuration + "\" maxDuration=\"" + maxDuration + "\" type=\"" + typeMedia + "\" lock=\"" +
                isLocked + "\" isResizable=\"" + isResizable + "\" backgroundColor=\"" +
                backgroundColor + "\" />\n";
        return SircusApplication.XMLFormatter(XML, 4, true);
    }
}
