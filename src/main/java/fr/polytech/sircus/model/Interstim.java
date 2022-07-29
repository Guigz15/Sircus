package fr.polytech.sircus.model;

import fr.polytech.sircus.SircusApplication;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
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
     * Convert an interstim to XML
     * @return XML
     */
    public String toXML() throws Exception {
        String XML = "<interstim filename=\"" + filename.replace(" ", "%20") +
                "\" minDuration=\"" + minDuration + "\" maxDuration=\"" + maxDuration + "\" type=\"" + typeMedia + "\" lock=\"" +
                isLocked + "\" isResizable=\"" + isResizable + "\" backgroundColor=\"" +
                String.format("0x%02x%02x%02x", backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue())
                + "\" />\n";
        return SircusApplication.XMLFormatter(XML, 4, true);
    }

    /**
     * Convert an interstim to XML + duration for result
     * @return XML
     */
    public String toXMLForResult() throws Exception {
        String XML = "<interstim filename=\"" + filename.replace(" ", "%20") +
                "\" minDuration=\"" + minDuration + "\" maxDuration=\"" + maxDuration + "\" type=\"" + typeMedia + "\" lock=\"" +
                isLocked + "\" isResizable=\"" + isResizable + "\" backgroundColor=\"" +
                String.format("0x%02x%02x%02x", backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue())
                + "\" duration=\"" + duration + "\" />\n";
        return SircusApplication.XMLFormatter(XML, 4, true);
    }
}
