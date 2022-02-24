package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a sequence
 */
public class Sequence implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private String name;

    @Setter
    private Duration duration;

    @Getter @Setter
    private List<Media> listMedias;

    @Getter @Setter
    private Boolean lock;


    /**
     * The constructor
     *
     * @param name Sequence's name
     */
    public Sequence(String name) {
        this.name = name;
        this.duration = Duration.ZERO;
        this.listMedias = new ArrayList<>();
        this.lock = true;
    }

    /**
     * The copy constructor
     *
     * @param sequence Sequence to copy
     */
    public Sequence(Sequence sequence) {
        this.name = sequence.getName();
        this.duration = sequence.getDuration();
        this.listMedias = sequence.getListMedias();
        this.lock = sequence.getLock();
    }

    /**
     * Compute the duration of a sequence
     *
     * @return Sequence's duration
     */
    public Duration getDuration() {
        Duration duration = Duration.ofSeconds(0);
        for (Media listMedia : listMedias) {
            duration = duration.plus(listMedia.getDuration());
            if (listMedia.getInterStim() != null) {
                duration = duration.plus(listMedia.getInterStim().getDuration());
            }
        }
        return duration;
    }

    /**
     * Add a media to this sequence
     *
     * @param media Media to add
     */
    public void addMedia(Media media) {
        this.listMedias.add(media);
        this.setDuration(this.getDuration());
    }

    /**
     * To remove a media from this sequence
     *
     * @param media Media to remove
     */
    public void removeMedia(Media media) {
        if (this.listMedias.remove(media)) {
            this.setDuration(this.getDuration());
        }
    }

    /**
     * Override the method toString to display only the name
     *
     * @return Sequence's name
     */
    public String toString() {
        return name;
    }
}
