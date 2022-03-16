package fr.polytech.sircus;

import fr.polytech.sircus.controller.MainWindowController;
import fr.polytech.sircus.model.DataSircus;
import fr.polytech.sircus.utils.Serializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class SircusApplication extends Application {

	/**
     *  dataSircus object
	 */
	public static DataSircus dataSircus;

	/**
     *  main window controller
	 */
	private MainWindowController mainWindowController;

    public static void main(String[] args) {
        launch();
    }

	/**
     * Starts application
	 *
	 * @param stage
	 * @throw IOException
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

        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);

        stage.setOnCloseRequest(event -> {
            try {
                Serializer.writeDataCircus(SircusApplication.dataSircus);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stage.setTitle("Application Sircus");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
