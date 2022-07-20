package fr.polytech.sircus;

import fr.polytech.sircus.controller.MainWindowController;
import fr.polytech.sircus.model.DataSircus;
import fr.polytech.sircus.model.Participant;
import fr.polytech.sircus.model.User;
import fr.polytech.sircus.utils.Serializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
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
     * boolean for super administrator connection
     */
    public static boolean superAdminConnected = false;

    /**
     * The patient to be diagnostic
     */
    public static Participant participant = null;

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

            // Kill GUI Thread
            Platform.exit();
            // Kill the JVM
            System.exit(0);
        });

        stage.setTitle("Application SIRCUS");
        stage.setMinHeight(700);
        stage.setMinWidth(scene.getWidth());
        stage.setScene(scene);
        stage.getIcons().add(new Image("images/logo-Sircus-FT-fond-blanc.png"));
        stage.show();
    }

    /**
     * Format a string for xml style.
     *
     * @param xmlString String to format.
     * @param indent Number of indentation.
     * @param ignoreDeclaration Boolean to ignore declaration at the top of the file.
     * @return The string formatted.
     */
    public static String XMLFormatter(String xmlString, int indent, boolean ignoreDeclaration) throws Exception {
        InputSource src = new InputSource(new StringReader(xmlString));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", indent);
        Transformer transformer = transformerFactory.newTransformer(new StreamSource(SircusApplication.class.getClassLoader().getResourceAsStream("styleSheetXML.xsl")));
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, ignoreDeclaration ? "yes" : "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        Writer out = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(out));
        return out.toString();
    }
}
