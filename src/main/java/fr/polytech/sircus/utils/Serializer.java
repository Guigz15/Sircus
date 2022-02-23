package fr.polytech.sircus.utils;

import fr.polytech.sircus.model.DataSircus;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;

import java.io.*;

/**
 * Classe permettant de serialiser et de deserialiser des objets facilement
 */
public final class Serializer {

    /**
     * Fichier contenant les informations sur les meta sequences
     */
    private static final File DATA_FILE = new File("data.sr");

    /**
     * Objet "stub" d'une meta sequence, pour tester
     */
    private static final DataSircus stubData;

    /**
     * Bool�en permettant de savoir si on utilise les objets "stub" lors des appels aux methodes de lectures
     */
    public static boolean useStub = false;

    // G�n�ration d'une entreprise "stub"
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
     * Permet de faciliter la lecture d'un objet depuis un fichier
     *
     * @param file Fichier a lire
     * @param stub Objet "stub" si jamais le fichier n'existe pas
     * @return L'objet lu ou l'objet "stub" si le fichier n'existe pas
     * @throws IOException Si le fichier n'est pas accessible
     */
    private static Object readObject(File file, Object stub) throws IOException {

        if (useStub) {
            return stub;
        }

        Object obj = stub;

        // On ne lit dans le fichier que si ce dernier existe
        if (file.exists()) {
            try {
                ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));

                obj = input.readObject();

                input.close();

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        return obj;
    }

    /**
     * Permet de faciliter l'ecriture d'un fichier
     *
     * @param file   Fichier dans lequel ecrire
     * @param object Objet a ecrire
     * @throws IOException Si le fichier n'est pas accessible
     */
    private static void writeObject(File file, Object object) throws IOException {

        // On verifie qu'il ne s'agisse pas d'un dossier
        if (!file.isDirectory()) {
            try {
                ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));

                output.writeObject(object);

                output.close();

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Permet de lire les donnees de l'application depuis un fichier
     * Si le fichier n'existe pas, une donnee "stub" est renvoyee
     *
     * @return DataCircus lue
     * @throws IOException Si le fichier n'est pas accessible
     */
    public static DataSircus readDataCircus() throws IOException {
        return (DataSircus) readObject(DATA_FILE, stubData);
    }

    /**
     * Permet d'enregistrer les informations de l'application dans un fichier
     *
     * @param dataSircus Objet representant les donnees a enregistrer
     * @throws IOException Si le fichier n'est pas accessible
     */
    public static void writeDataCircus(DataSircus dataSircus) throws IOException {
        writeObject(DATA_FILE, dataSircus);
    }

}

