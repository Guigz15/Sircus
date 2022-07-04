package fr.polytech.sircus.model;

import lombok.Getter;

import java.time.LocalTime;

public class EyeTrackerData {
    @Getter
    private final String leftEyeCoordinates;

    @Getter
    private final String rightEyeCoordinates;

    @Getter
    private final String leftPupilDiameter;

    @Getter
    private final String rightPupilDiameter;

    @Getter
    private final LocalTime time;

    public EyeTrackerData(String tuple) {
        String[] dataSplit = tuple.split("\t");
        this.leftEyeCoordinates = dataSplit[0].substring(10);
        this.leftPupilDiameter = dataSplit[1].substring(21);
        this.rightEyeCoordinates = dataSplit[2].substring(11);
        this.rightPupilDiameter = dataSplit[3].substring(22);
        this.time = LocalTime.parse(dataSplit[4].substring(6));
    }

    public String toXML() {
        return "<eyeTrackerData leftEyeCoordinates=\"" + this.leftEyeCoordinates + "\" leftEyePupilDiameter=\"" +
                this.leftPupilDiameter + "\" rightEyeCoordinates=\"" + this.rightEyeCoordinates +
                "\" rightEyePupilDiameter=\"" + this.rightPupilDiameter + " \" time=\"" + this.time + "\" />\n";
    }
}
