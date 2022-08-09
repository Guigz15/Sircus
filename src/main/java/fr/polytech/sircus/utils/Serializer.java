package fr.polytech.sircus.utils;

import fr.polytech.sircus.model.DataSircus;
import java.io.*;

/**
 * Class that allow the serialization and deserialization of objects easily
 */
public final class Serializer {

    /**
     * File containing the information on the meta sequences
     */
    private static final File DATA_FILE = new File("data.ser");

    /**
     * Stub meta sequence to test
     */
    private static final DataSircus stubData;

    /**
     * Boolean to know if we use a stub object
     */
    public static boolean useStub = false;

    /**
     * Generation of stub data
     */
    static {
        stubData = new DataSircus();

        stubData.addLocationToList("Hôpital Bretonneau");
        stubData.addLocationToList("Hôpital Trousseau");

        stubData.addEyeTrackerToList("Tobii 1");
        stubData.addEyeTrackerToList("Tobii 2");
        stubData.addEyeTrackerToList("Tobii 3");

        stubData.addAdmin("admin", "password");
        stubData.addSuperAdmin("sudo", "password");
    }

    /**
     * Allows to facilitate the reading of an object from a file
     * @param stub Stub object if the file doesn't exist
     * @return Object read or stub if the file doesn't exist
     * @throws IOException The file in not accessible
     */
    private static Object readObject(Object stub) throws IOException {
        if(useStub) {
            return stub;
        }

        Object obj = stub;

        // Read in the file only if it exists
        if(Serializer.DATA_FILE.exists()) {
            try {
                ObjectInputStream input = new ObjectInputStream(new FileInputStream(Serializer.DATA_FILE));
                obj = input.readObject();
                input.close();
            } catch(FileNotFoundException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * Allows to facilitate the writing of a file
     *
     * @param object Object to write
     * @throws IOException The file in not accessible
     */
    private static void writeObject(Object object) throws IOException {
        if(!Serializer.DATA_FILE.isDirectory()) {
            try {
                ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(Serializer.DATA_FILE));
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
        return (DataSircus) readObject(stubData);
    }

    /**
     * Allow to save the information of the application in a file
     * @param dataSircus Object that represent the data to record
     * @throws IOException The file in not accessible
     */
    public static void writeDataCircus(DataSircus dataSircus) throws IOException {
        writeObject(dataSircus);
    }
}

