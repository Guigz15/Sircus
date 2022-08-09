package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represent an event that appears during the experiment
 */
public class Log {

    @Getter @Setter
    private String log;

    @Getter @Setter
    private LocalTime time;

    public Log(String log) {
        this.log = log;
        this.time = LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
    }

    public String toXML() {
        return "<log content=\"" + this.log + "\" time=\"" + this.time + "\" />\n";
    }
}
