package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

/**
 * This class represent a viewer data, that means the filename and its displaying time
 */
public class ViewerData {

    @Getter @Setter
    private String filename;

    @Getter @Setter
    private LocalTime time;

    public ViewerData(String tuple) {
        String[] dataSplit = tuple.split("\t");
        this.filename = dataSplit[0];
        this.time = LocalTime.parse(dataSplit[1]);
    }

    public String toXML() {
        return "<viewerData filename=\"" + this.filename + "\" time=\"" + this.time + "\" />\n";
    }
}
