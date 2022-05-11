package fr.polytech.sircus.utils;

import fr.polytech.sircus.model.*;
import javafx.scene.paint.Color;

import java.io.*;
import java.time.Duration;

/**
 * Class that allow the serialization et deserialization of objects easily
 */
public final class Serializer {

    /**
     * File containing the information on the meta sequences
     */
    private static final File DATA_FILE = new File("data.sr");

    /**
     * Stub meta sequence to test
     */
    private static final DataSircus stubData;

    /**
     * Boolean to know if we use a stub object
     */
    public static boolean useStub = true;

    /**
     * Generation of stub data
     */
    static {

        stubData = new DataSircus();

        MetaSequence stubMetaSeq1 = new MetaSequence("Meta sequence 1");
        MetaSequence stubMetaSeq2 = new MetaSequence("Meta sequence 2");
        MetaSequence stubMetaSeq3 = new MetaSequence("Meta sequence 3");

        Sequence sequence1 = new Sequence("Sequence 1");
        Sequence sequence2 = new Sequence("Sequence 2");
        Sequence sequence3 = new Sequence("Sequence 3");

        Media media1 = new Media("Media 1", "mos01.jpg", Duration.ofSeconds(1), TypeMedia.PICTURE, null);
        Media media2 = new Media("Media 2", "mos02.jpg", Duration.ofSeconds(1), TypeMedia.PICTURE, null);
        Media media3 = new Media("Media 3", "mosob01.jpg", Duration.ofSeconds(1), TypeMedia.PICTURE, null);
        Media media4 = new Media("Media 4", "vis03.jpg", Duration.ofSeconds(1), TypeMedia.PICTURE, null);
        Media media5 = new Media("Media 5", "vismos01.jpg", Duration.ofSeconds(5), TypeMedia.PICTURE, null);
        Media media6 = new Media("Media 6", "visob02.jpg", Duration.ofSeconds(5), TypeMedia.PICTURE, null);
        Media media7 = new Media("Media 7", "croix.jpg", Duration.ofSeconds(4), TypeMedia.PICTURE, null);
        Media media8 = new Media("Media 8", "ob01.jpg", Duration.ofSeconds(10), TypeMedia.PICTURE, null);

        media1.setBackgroundColor(Color.AQUA);
        media2.setBackgroundColor(Color.GREENYELLOW);

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
        sequence1.addMedia(media1);
        sequence1.addMedia(media2);
        sequence1.addMedia(media3);
        sequence2.addMedia(media4);
        sequence2.addMedia(media5);

        stubMetaSeq1.addSequence(sequence1);
        stubMetaSeq2.addSequence(sequence2);
        stubMetaSeq3.addSequence(sequence1);
        stubMetaSeq3.addSequence(sequence2);
        stubMetaSeq3.addSequence(sequence2);
        stubMetaSeq3.addSequence(sequence2);

        stubData.saveMetaSeq(stubMetaSeq1);
        stubData.saveMetaSeq(stubMetaSeq2);
        stubData.saveMetaSeq(stubMetaSeq3);

        stubData.addLocationToList("Hôpital Bretonneau");
        stubData.addLocationToList("Hôpital Trousseau");

        stubData.addEyeTrackerToList("Tobii 1");
        stubData.addEyeTrackerToList("Tobii 2");
        stubData.addEyeTrackerToList("Tobii 3");
    }

    /**
     * Allows to facilitate the reading of an object from a file
     * @param file File to read
     * @param stub Stub object if the file doesn't exist
     * @return Object read or stub if the file doesn't exist
     * @throws IOException The file in not accessible
     */
    private static Object readObject(File file, Object stub) throws IOException {
        if(useStub) {
            return stub;
        }

        Object obj = stub;

        // Read in the file only if it exists
        if(file.exists()) {
            try {
                ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
                obj = input.readObject();
                input.close();
            } catch(FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * Allows to facilitate the writing of a file
     * @param file File to write in
     * @param object Object to write
     * @throws IOException The file in not accessible
     */
    private static void writeObject(File file, Object object) throws IOException {
        if(!file.isDirectory()) {
            try {
                ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
                output.writeObject(object);
                output.close();
            } catch(FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Allow to read the data of the application from a file
     * If the file doesn't exist, a stub data is return
     * @return DataCircus read
     * @throws IOException The file in not accessible
     */
    public static DataSircus readDataCircus() throws IOException {
        return (DataSircus) readObject(DATA_FILE, stubData);
    }

    /**
     * Allow to save the information of the application in a file
     * @param dataSircus Object that represent the data to record
     * @throws IOException The file in not accessible
     */
    public static void writeDataCircus(DataSircus dataSircus) throws IOException {
        writeObject(DATA_FILE, dataSircus);
    }
}

