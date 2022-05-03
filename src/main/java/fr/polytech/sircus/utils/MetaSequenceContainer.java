package fr.polytech.sircus.utils;

import fr.polytech.sircus.model.Media;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.model.TypeMedia;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MetaSequenceContainer implements Serializable {
    private final List<MetaSequence> metaSequences = new ArrayList<>();

    public MetaSequenceContainer() {
        Instant start = Instant.parse("2021-11-11T12:00:00.00Z");
        Instant end = Instant.parse("2021-11-11T15:00:00.00Z");

        MetaSequence metaSequence1 = new MetaSequence("Metaséquence 1");
        MetaSequence metaSequence2 = new MetaSequence("Metaséquence 2");
        MetaSequence metaSequence3 = new MetaSequence("Metaséquence 3");

        Sequence sequence1 = new Sequence("Sequence 1");
        Sequence sequence2 = new Sequence("Sequence 2");
        Sequence sequence3 = new Sequence("Sequence 3");

        Media media1 = new Media("Media 1", "mos01.jpg", Duration.ofSeconds(5), TypeMedia.PICTURE, null);
        Media media2 = new Media("Media 2", "mos02.jpg", Duration.ofSeconds(2), TypeMedia.VIDEO, null);
        Media media3 = new Media("Media 3", "mosob01.jpg", Duration.ofSeconds(1), TypeMedia.PICTURE, null);
        Media media4 = new Media("Media 4", "vis03.jpg", Duration.ofSeconds(7), TypeMedia.PICTURE, null);
        Media media5 = new Media("Media 5", "vismos01.jpg", Duration.ofSeconds(5), TypeMedia.PICTURE, null);
        Media media6 = new Media("Media 6", "visob02.jpg", Duration.ofSeconds(5), TypeMedia.PICTURE, null);
        Media media7 = new Media("Media 7", "croix.jpg", Duration.ofSeconds(4), TypeMedia.PICTURE, null);
        Media media8 = new Media("Media 8", "ob01.jpg", Duration.ofSeconds(10), TypeMedia.PICTURE, null);

        sequence1.addMedia(media1);
        sequence1.addMedia(media2);
        sequence2.addMedia(media1);
        sequence2.addMedia(media2);
        sequence2.addMedia(media3);
        sequence2.addMedia(media4);
        sequence2.addMedia(media5);
        sequence2.addMedia(media6);
        sequence2.addMedia(media7);
        sequence2.addMedia(media8);
        sequence3.addMedia(media1);
        sequence3.addMedia(media3);

        metaSequence1.addSequence(sequence1);
        metaSequence1.addSequence(sequence2);
        metaSequence1.addSequence(sequence2);
        metaSequence1.addSequence(sequence2);
        metaSequence2.addSequence(sequence2);
        metaSequence2.addSequence(sequence3);
        metaSequence3.addSequence(sequence1);
        metaSequence3.removeSequence(sequence3);

        metaSequences.add(metaSequence1);
        metaSequences.add(metaSequence2);
        metaSequences.add(metaSequence3);
    }

    public List<MetaSequence> getMetaSequences() {
        return metaSequences;
    }

    public void addMetaSequence(MetaSequence metaSequence) {
        metaSequences.add(metaSequence);
    }
}
