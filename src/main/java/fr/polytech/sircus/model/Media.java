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

    public Media(String filename, Duration duration, TypeMedia typeMedia) {
        this(filename, duration, typeMedia, true, false, Color.WHITE, null);
    }

    public Media(String filename, Duration duration, TypeMedia typeMedia, boolean isLocked, boolean isResizable, Color backgroundColor, Interstim interstim) {
        this.filename = filename;
        this.duration = duration;
        this.typeMedia = typeMedia;
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
