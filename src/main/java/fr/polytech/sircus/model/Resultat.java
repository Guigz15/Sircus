package fr.polytech.sircus.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.List;

public class Resultat {

    @Getter
    @Setter
    private String nomMetaSequence;

    @Getter
    @Setter
    private Duration duration;

    @Getter
    @Setter
    private List<Sequence> listSequences;

    public Resultat(String nomMetaSequence, Duration duration, List<Sequence> listSequences) {
        this.nomMetaSequence = nomMetaSequence;
        this.duration = duration;
        this.listSequences = listSequences;
    }

    public Resultat() {

    }
}