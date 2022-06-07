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
     * Main constructor
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

    /**
     * Change a location
     * @param oldLocation the name of the old location
     * @param newLocation the name of the new location
     */
    public void updateLocation(String oldLocation, String newLocation) {
        if(this.locationsList.contains(oldLocation))
            this.locationsList.set(this.locationsList.indexOf(oldLocation), newLocation);
    }

    /**
     * Remove a location
     * @param locationToRemove the location to be removed
     */
    public void removeLocationFromList(String locationToRemove) {
        this.locationsList.remove(locationToRemove);
    }

    /**
     * Add a classification method to the list of methods
     * @param methodToAdd new method to add
     */
    public void addMethodToList(String methodToAdd) {
        if(!this.methodsList.contains(methodToAdd))
            this.methodsList.add(methodToAdd);
    }

    /**
     * Change a classification method
     * @param oldMethod the name of the old method
     * @param newMethod the name of the new method
     */
    public void updateMethod(String oldMethod, String newMethod) {
        if(this.methodsList.contains(oldMethod))
            this.methodsList.set(this.methodsList.indexOf(oldMethod), newMethod);
    }

    /**
     * Remove a classification method
     * @param methodToRemove the method to be removed
     */
    public void removeMethodFromList(String methodToRemove) {
        this.methodsList.remove(methodToRemove);
    }

    /**
     * Add an eyeTracker to the list of eyeTrackers
     * @param eyeTrackerName new eyeTracker to add
     */
    public void addEyeTrackerToList(String eyeTrackerName) {
        if (!this.eyeTrackerList.contains(eyeTrackerName))
            this.eyeTrackerList.add(eyeTrackerName);
    }

    /**
     * Serialize an eyeTracker to set it by default
     * @param eyeTrackerName the name of the eyeTracker
     */
    public void saveEyeTracker(String eyeTrackerName) {
        if (eyeTrackerName != null)
            this.eyeTrackerSaved = eyeTrackerName;
    }
}
