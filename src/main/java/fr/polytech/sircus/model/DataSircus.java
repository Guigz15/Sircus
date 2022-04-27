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
    private List<String> methodsList;
    @Getter @Setter
    private List<String> eyeTrackerList;
    @Getter @Setter
    private String eyeTrackerSaved;
    @Getter @Setter
    private PathMedia path;

    /**
     * Constructor
     */
    public DataSircus() {
        this.metaSequencesList = new ArrayList<>();
        this.locationsList = new ArrayList<>();
        this.methodsList = new ArrayList<>();
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
     * @param locationToAdd new location to add
     */
    public void addLocationToList(String locationToAdd) {
        if(!this.locationsList.contains(locationToAdd))
            this.locationsList.add(locationToAdd);
    }

    public void updateLocation(String oldLocation, String newLocation) {
        if(this.locationsList.contains(oldLocation))
            this.locationsList.set(this.locationsList.indexOf(oldLocation), newLocation);
    }

    public void removeLocationFromList(String locationToRemove) {
        this.locationsList.remove(locationToRemove);
    }

    /**
     * Add a method to the list of methods
     * @param methodToAdd new method to add
     */
    public void addMethodToList(String methodToAdd) {
        if(!this.methodsList.contains(methodToAdd))
            this.methodsList.add(methodToAdd);
    }

    public void updateMethod(String oldMethod, String newMethod) {
        if(this.methodsList.contains(oldMethod))
            this.methodsList.set(this.methodsList.indexOf(oldMethod), newMethod);
    }
    public void removeMethodFromList(String methodToRemove) {
        this.methodsList.remove(methodToRemove);
    }

    /**
     * Add an eyeTracker to list of eyeTrackers
     * @param eyeTrackerName new eyeTracker to add
     */
    public void addEyeTrackerToList(String eyeTrackerName) {
        if (!this.eyeTrackerList.contains(eyeTrackerName))
            this.eyeTrackerList.add(eyeTrackerName);
    }

    public void saveEyeTracker(String eyeTrackerName) {
        if (eyeTrackerName != null)
            this.eyeTrackerSaved = eyeTrackerName;
    }
}
