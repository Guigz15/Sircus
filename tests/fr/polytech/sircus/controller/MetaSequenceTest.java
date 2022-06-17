package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MetaSequenceTest {
    private static FxRobot robot;

    @BeforeAll
    public static void setup() throws Exception {
        System.out.println("*--- Testing the meta sequence UI ---*");
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(SircusApplication.class);
        robot = new FxRobot();

        //go to the meta-sequence view
        robot.clickOn("#id").write("identifiant");
        robot.clickOn((RadioButton) robot.lookup("M").query());
        robot.clickOn("#birthDate").write("03/05/2010").press(KeyCode.ENTER).release((KeyCode.ENTER));
        robot.clickOn("#name").write("Name_Patient_Test");
        robot.clickOn("#forename").write("Forename_Patient_Test");
        ComboBox<String> location = robot.lookup("#locations").query();
        robot.interact(() -> location.getSelectionModel().select(0));
        robot.clickOn("#admin");
        robot.clickOn("#userName").write("root");
        robot.clickOn("#password").write("password");
        robot.press(KeyCode.ENTER);
        robot.clickOn("#next");
    }

    @Test
    @Order(1)
    public void test1_add_new_metasequence() {
        System.out.println("Testing to add a new meta-sequence");

        ListView<MetaSequence> metaSequenceListView = robot.lookup("#metaListView").query();
        int sizeBeforeAdding = metaSequenceListView.getItems().size();

        robot.clickOn("#addMetaButton");

        int sizeAfterAdding = metaSequenceListView.getItems().size();

        assertNotEquals(sizeBeforeAdding, sizeAfterAdding);
    }

    @Test
    @Order(2)
    public void test2_rename_new_metasequence() {
        System.out.println("Testing to rename the new meta-sequence");

        robot.clickOn("#metaListView");
        robot.clickOn("#renameMetaButton");


    }
}

