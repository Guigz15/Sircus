package fr.polytech.sircus.model;

import java.io.Serializable;

/**
 * Class to reprensent the path to a media
 */
public class PathMedia  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String defaultPath;
    private String lastPath;
    /**
     * Boolean to indicate if defaultPath is a path provided by the user.
     * In this case, defaultPath take the priority on lastPath.
     */
    private boolean customPath;

    /**
     * Constructor by default
     */
    public PathMedia() {
        defaultPath = System.getProperty("user.dir") + "\\medias";
        lastPath = null;
        customPath = false;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    public String getLastPath() {
        return lastPath;
    }

    public boolean isCustomPath() {
        return customPath;
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public void setLastPath(String lastPath) {
        this.lastPath = lastPath;
    }

    public void setCustomPath(boolean customPath) {
        this.customPath = customPath;
    }
}
