package fr.polytech.sircus.utils;

import fr.polytech.sircus.model.DataSircus;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;

import java.io.*;

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
    private static DataSircus stubData;

    /**
     * Boolean to know if we use a stub object
     */
    public static boolean useStub = false;

    /**
     * Generation of stub data
     */
    static {

        stubData = new DataSircus();

        MetaSequence stubMetaSeq1 = new MetaSequence("Meta sequence 1");
        MetaSequence stubMetaSeq2 = new MetaSequence("Meta sequence 2");

        Sequence stubSeq1 = new Sequence("Sequence 1");
        Sequence stubSeq2 = new Sequence("Sequence 2");
        Sequence stubSeq3 = new Sequence("Sequence 3");
        Sequence stubSeq4 = new Sequence("Sequence 4");

        stubMetaSeq1.addSequence(stubSeq1);
        stubMetaSeq1.addSequence(stubSeq2);
        stubMetaSeq2.addSequence(stubSeq3);
        stubMetaSeq2.addSequence(stubSeq4);

        stubData.saveMetaSeq(stubMetaSeq1);
        stubData.saveMetaSeq(stubMetaSeq2);
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
            } catch(ClassNotFoundException ex) {
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

