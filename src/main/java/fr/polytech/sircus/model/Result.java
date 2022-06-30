package fr.polytech.sircus.model;

import lombok.Getter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to store the results gathered by a meta sequence.
 */
public class Result {

    @Getter
    private Duration duration;

    @Getter
    private MetaSequence metaSequenceUsed;

    @Getter
    private List<EyeTrackerData> eyeTrackerDatas;

    @Getter
    private List<ViewerData> viewerDatas;

    @Getter
    private List<Comment> comments;

    /**
     * Default constructor of the Result class.
     */
    public Result() {
        this.eyeTrackerDatas = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.viewerDatas = new ArrayList<>();
    }

    /**
     * Full constructor of the Result class.
     *
     * @param metaSequenceUsed for the experiment.
     */
    public Result(MetaSequence metaSequenceUsed) {
        this.metaSequenceUsed = metaSequenceUsed;
        this.duration = metaSequenceUsed.getDuration();
        this.eyeTrackerDatas = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.viewerDatas = new ArrayList<>();
    }

    /**
     * Add a new comment to the result
     * @param comment The content of the comment
     */
    public void addComment(String comment) {
        this.comments.add(new Comment(comment));
    }

    public void addEyeTrackerData(String tuple) {
        this.eyeTrackerDatas.add(new EyeTrackerData(tuple));
    }

    public void addViewerData(String tuple) {
        this.viewerDatas.add(new ViewerData(tuple));
    }

    public void setMetaSequenceUsed(MetaSequence metaSequenceUsed) {
        this.metaSequenceUsed = metaSequenceUsed;
        this.duration = metaSequenceUsed.getDuration();
    }

    public void clear() {
        this.metaSequenceUsed = null;
        this.duration = Duration.ZERO;
        this.eyeTrackerDatas.clear();
        this.comments.clear();
        this.viewerDatas.clear();
    }

    public String toXML() {
        String XML = "<result>\n" +
                "<metasequenceUsed name=\"" + metaSequenceUsed.getName().replace(" ", "%20") +
                "\" minDuration=\"" + metaSequenceUsed.getMinDuration() + "\" maxDuration=\"" +
                metaSequenceUsed.getMaxDuration() + "\" duration=\"" + duration + "\" />\n" +
                "<listSequence>\n";
        for (Sequence sequence : metaSequenceUsed.getSequencesList())
           XML += sequence.toXML();
        XML += "</listSequence>\n" +
                "<listEyeTrackerData>\n";
        for (EyeTrackerData eyeTrackerData : eyeTrackerDatas)
            XML += eyeTrackerData.toXML();
        XML += "</listEyeTrackerData>\n" +
                "<listViewerData>\n";
        for (ViewerData viewerData : viewerDatas)
            XML += viewerData.toXML();
        XML += "</listViewerData>\n" +
                "<listComment>\n";
        for (Comment comment : comments)
            XML += comment.toXML();
        XML += "</listComment>\n" +
                "</result>\n";
        return XML;
    }
}