package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Objects;
import java.util.Random;

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
     * Minimum duration for which the media could be displayed.
     */
    @Getter @Setter
    protected Duration minDuration;

    /**
     * Maximum duration for which the media could be displayed.
     */
    @Getter @Setter
    protected Duration maxDuration;

    /**
     * Duration for which the media would be displayed.
     */
    @Getter @Setter
    protected Duration duration;

    /**
     * The type of the media (picture or video).
     */
    @Getter @Setter
    protected TypeMedia typeMedia;

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
    protected Color backgroundColor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractMedia)) return false;
        AbstractMedia media = (AbstractMedia) o;
        return getFilename().equals(media.getFilename()) &&
                getMinDuration().equals(media.getMinDuration()) &&
                getMaxDuration().equals(media.getMaxDuration()) &&
                getTypeMedia().equals(media.getTypeMedia()) &&
                Objects.equals(isLocked(), media.isLocked()) &&
                isResizable() == media.isResizable() &&
                getBackgroundColor() == media.getBackgroundColor();
    }

    public String toString() {
        return filename;
    }

    /**
     * Get a random duration between minDuration and maxDuration of the media
     */
    public Duration getRandomDuration() {
        Random r = new Random();
        double randomDuration = r.nextDouble() * Math.abs(this.maxDuration.getSeconds() - this.minDuration.getSeconds()) + this.minDuration.getSeconds();

        BigDecimal bd = BigDecimal.valueOf(randomDuration);
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        String[] arr = String.valueOf(bd.doubleValue()).split("\\.");
        long[] longArr = new long[2];
        longArr[0] = Long.parseLong(arr[0]);

        // To format millis correctly
        if (arr[1].length() == 1)
            arr[1] += "00";
        else if (arr[1].length() == 2)
            arr[1] += "0";

        longArr[1] = Long.parseLong(arr[1]);

        return Duration.ofSeconds(longArr[0]).plus(Duration.ofMillis(longArr[1]));
    }
}
