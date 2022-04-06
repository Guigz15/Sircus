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
    private List<Location> locationsList;
    @Getter @Setter
    private PathMedia path;

    /**
     * Constructor
     */
    public DataSircus() {
        this.metaSequencesList = new ArrayList<>();
        this.path = new PathMedia();
        this.locationsList = new ArrayList<>();
    }

    /**
     * Add a location to the list of locations
     * @param location new location to add
     */
    public void addLocationToList(Location location) {
        if(!this.locationsList.contains(location)){
            this.locationsList.add(location);
        }
    }

    /**
     * Add a meta sequence to the list of meta sequences
     * @param metaSequence the new meta sequence
     */
    public void saveMetaSeq(MetaSequence metaSequence) {
        this.metaSequencesList.add(metaSequence);
    }
}
