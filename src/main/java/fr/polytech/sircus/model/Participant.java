package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Period;

/**
 * Represent a patient with its information
 */
public class Participant {

    public enum Sex { M, F }
    public enum EyeDominance { Droit, Gauche, Ambidextre, Indéfini }
    public enum HandLaterality { Droitier, Gaucher, Ambidextre, Indéfini }

    @Getter @Setter
    private String identifier;
    @Getter @Setter
    private Sex sex;
    @Getter @Setter
    private int visitNumber;
    @Getter @Setter
    private LocalDate birthDate;
    @Getter @Setter
    private EyeDominance eyeDominance;
    @Getter @Setter
    private HandLaterality handLaterality;

    /**
     * Default constructor
     */
    public Participant() {}

    /**
     * Copy constructor
     * @param participant the patient to copy
     */
    public Participant(Participant participant) {
        this.identifier = participant.identifier;
        this.sex = participant.sex;
        this.visitNumber = participant.visitNumber;
        this.birthDate = participant.birthDate;
        this.eyeDominance = participant.eyeDominance;
        this.handLaterality = participant.handLaterality;
    }

    /**
     * Give the age of the patient
     * @return the gae of the patient
     */
    public int computeAge() {
        Period period = Period.between(birthDate, LocalDate.now());
        return period.getYears();
    }

    public String toXML() {
        return "<patient>\n" +
                "<id>" + this.identifier + "</id>\n" +
                "<sex>" + this.sex.toString() + "</sex>\n" +
                "<visitNumber>" + this.visitNumber + "</visitNumber>\n" +
                "<birthDate>" + this.birthDate.toString() + "</birthDate>\n" +
                "<eyeDominance>" + this.eyeDominance.toString() + "</eyeDominance>\n" +
                "<handLaterality>" + this.handLaterality.toString() + "</handLaterality>\n" +
                "</patient>\n";
    }
}
