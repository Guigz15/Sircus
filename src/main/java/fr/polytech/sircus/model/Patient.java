package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Period;

public class Patient {

    enum Sex { M, F }
    enum EyeDominance { Droit, Gauche, Indefini }
    enum HandLaterality { Droitier, Gaucher, Ambidextre, Indefini }

    @Getter @Setter
    private String identifier;
    @Getter @Setter
    private Sex sex;
    @Getter @Setter
    private LocalDate birthDate;
    @Getter @Setter
    private EyeDominance eyeDominance;
    @Getter @Setter
    private HandLaterality handLaterality;

    public Patient() {

    }

    public Patient(Patient patient) {
        this.identifier = patient.identifier;
        this.sex = patient.sex;
        this.birthDate = patient.birthDate;
        this.eyeDominance = patient.eyeDominance;
        this.handLaterality = patient.handLaterality;
    }

    public int computeAge() {
        Period period = Period.between(birthDate, LocalDate.now());
        return period.getYears();
    }
}
