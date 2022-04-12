package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object that contain all the meta sequences to simplify the serialization
 */
public class DataSircus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Getter @Setter
    private List<MetaSequence> metaSequencesList;
    @Getter @Setter
    private List<String> locationsList;
    @Getter @Setter
    private List<String> eyeTrackerList;
    @Getter @Setter
    private PathMedia path;

    /**
     * Constructor
     */
    public DataSircus() {
        this.metaSequencesList = new ArrayList<>();
        this.locationsList = new ArrayList<>();
        this.eyeTrackerList = new ArrayList<>();
        this.path = new PathMedia();
    }

    /**
     * Add a meta sequence to the list of meta sequences
     * @param metaSequence the new meta sequence
     */
    public void saveMetaSeq(MetaSequence metaSequence) {
        this.metaSequencesList.add(metaSequence);
    }

    /**
     * Add a location to the list of locations
     * @param location new location to add
     */
    public void addLocationToList(String location) {
        if(!this.locationsList.contains(location))
            this.locationsList.add(location);
    }

    public void addEyeTrackerToList(String eyeTrackerName) {
        if (!this.eyeTrackerList.contains(eyeTrackerName))
            this.eyeTrackerList.add(eyeTrackerName);
    }
}
