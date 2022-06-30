package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Locale;

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
    @Getter @Setter
    private String resultPath;

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
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        // windows
        if(osName.contains("win")){
            defaultPath = System.getProperty("user.dir") + "\\medias";
            seqPath = System.getProperty("user.dir") + "\\sequences\\";
            metaPath = System.getProperty("user.dir") + "\\metaSequence\\";
            resultPath = System.getProperty("user.dir") + "\\result\\";
        }
        // others
        else {
            defaultPath = System.getProperty("user.dir") + "/medias";
            seqPath = System.getProperty("user.dir") + "/sequences/";
            metaPath = System.getProperty("user.dir") + "/metaSequence/";
            resultPath = System.getProperty("user.dir") + "/result/";
        }

        lastPath = null;
        customPath = false;
    }
}
