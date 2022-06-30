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
 * This class represents a media (picture or video).
 */
public class Media extends AbstractMedia implements Serializable {

    /**
     * The interstim to be displayed before the media.
     */
    @Getter @Setter
    private Interstim interstim;

    public Media() {
        filename = null;
        minDuration = null;
        maxDuration = null;
        duration = null;
        typeMedia = null;
        isLocked = false;
        isResizable = false;
        backgroundColor = Color.WHITE;
        interstim = null;
    }

    public Media(String filename, Duration minDuration, Duration maxDuration, TypeMedia typeMedia) {
        this(filename, minDuration, maxDuration, typeMedia, true, false, Color.WHITE, null);
    }

    public Media(String filename, Duration minDuration, Duration maxDuration, TypeMedia typeMedia, boolean isLocked, boolean isResizable, Color backgroundColor, Interstim interstim) {
        this.filename = filename;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        duration = Duration.ZERO;
        this.typeMedia = typeMedia;
        this.isLocked = isLocked;
        this.isResizable = isResizable;
        this.backgroundColor = backgroundColor;
        this.interstim = interstim;
    }

    public Media(Media media) {
        this(media.filename, media.getMinDuration(), media.getMaxDuration(), media.getTypeMedia(), media.isLocked, media.isResizable, media.getBackgroundColor(), null);
        if (media.getInterstim() != null) {
            this.interstim = new Interstim(media.getInterstim());
            this.interstim.setMedia(this);
        }
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

    /**
     * Convert a media to XML
     * @return XML
     */
    public String toXML() throws Exception {
        String XML = "<media>\n" +
                "<filename>" + filename.replace(" ", "%20") + "</filename>\n" +
                "<minDuration>" + minDuration + "</minDuration>\n" +
                "<maxDuration>" + maxDuration + "</maxDuration>\n" +
                "<type>" + typeMedia + "</type>\n";
        if(interstim == null){
            XML += "<lock>" + isLocked + "</lock>\n" +
                    "<isResizable>" + isResizable + "</isResizable>\n" +
                    "<backgroundColor>" + backgroundColor + "</backgroundColor>\n" +
                    "</media>\n";
        } else {
            XML += interstim.toXML() +
                    "<lock>" + isLocked + "</lock>\n" +
                    "<isResizable>" + isResizable + "</isResizable>\n" +
                    "<backgroundColor>" + backgroundColor + "</backgroundColor>\n" +
                    "</media>\n";
        }
        return SircusApplication.XMLFormatter(XML, 4, true);
    }
}
