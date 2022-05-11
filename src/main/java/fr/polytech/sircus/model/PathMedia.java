package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Class to reprensent the path to a media
 */
public class PathMedia  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private String defaultPath;
    @Getter @Setter
    private String lastPath;
    @Getter @Setter
    private String seqPath;
    @Getter @Setter
    private String metaPath;

    /**
     * Boolean to indicate if defaultPath is a path provided by the user.
     * In this case, defaultPath take the priority on lastPath.
     */
    @Getter @Setter
    private boolean customPath;

    /**
     * Constructor by default
     */
    public PathMedia() {
        String osName = System.getProperty("os.name");
        // windows
        if(osName.indexOf("win") >= 0){
            defaultPath = System.getProperty("user.dir") + "\\medias";
            seqPath = System.getProperty("user.dir") + "\\sequences\\";
            metaPath = System.getProperty("user.dir") + "\\metaSequence\\";
        }
        // others
        else {
            defaultPath = System.getProperty("user.dir") + "/medias";
            seqPath = System.getProperty("user.dir") + "/sequences/";
            metaPath = System.getProperty("user.dir") + "/metaSequence/";
        }

        lastPath = null;
        customPath = false;
    }
}
