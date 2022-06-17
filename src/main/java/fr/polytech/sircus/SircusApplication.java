package fr.polytech.sircus;

import fr.polytech.sircus.controller.MainWindowController;
import fr.polytech.sircus.model.DataSircus;
import fr.polytech.sircus.model.Patient;
import fr.polytech.sircus.model.User;
import fr.polytech.sircus.utils.Serializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Main class
 */
public class SircusApplication extends Application {

	/**
     *  dataSircus object
	 */
	public static DataSircus dataSircus;

	/**
     *  main window controller
	 */
	private MainWindowController mainWindowController;

    /**
     * boolean for administrator connection
     */
    public static boolean adminConnected = false;

    /**
     * The patient to be diagnostic
     */
    public static Patient patient = null;

    /**
     * The practitioner do the diagnostic
     */
    public static User user = null;

    /**
     * Diagnostic location
     */
    public static String currentLocation = null;

    /**
     * Classification method used
     */
    public static String currentMethod = null;



    public static void main(String[] args) {
        launch();
    }

	/**
     * Starts application
	 */
    @Override
    public void start(Stage stage) throws IOException {

        SircusApplication.dataSircus = new DataSircus();
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("views/main_window.fxml")));
        mainWindowController = fxmlLoader.getController();

        try {
            SircusApplication.dataSircus = Serializer.readDataCircus();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);

        stage.setOnCloseRequest(event -> {
            try {
                Serializer.writeDataCircus(SircusApplication.dataSircus);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stage.setTitle("Application SIRCUS");
        stage.setScene(scene);
        stage.getIcons().add(new Image("images/logo-Sircus-FT-fond-blanc.png"));
        stage.show();
    }
}
