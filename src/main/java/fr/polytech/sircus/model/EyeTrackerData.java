package fr.polytech.sircus.model;

import lombok.Getter;

import java.time.LocalTime;

public class EyeTrackerData {
    @Getter
    private final String leftEye;

    @Getter
    private final String rightEye;

    @Getter
    private final LocalTime time;

    public EyeTrackerData(String tuple) {
        String[] dataSplit = tuple.split("\t");
        this.leftEye = dataSplit[0].substring(10);
        this.rightEye = dataSplit[1].substring(11);
        this.time = LocalTime.parse(dataSplit[2].substring(6));
    }

    public String toXML() {
        return "<eyeTrackerData leftEye=\"" + this.leftEye + "\" rightEye=\"" + this.rightEye + "\" time=\"" + this.time + "\" />\n";
    }
}
