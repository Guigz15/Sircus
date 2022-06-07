package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.Sequence;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.matcher.control.TableViewMatchers;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;

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
        ComboBox<String> location = robot.lookup("#location").query();
        robot.interact(() -> location.getSelectionModel().select(0));
        robot.clickOn("#button_save");
        robot.clickOn("#metaSeqTab");
    }

    @Test
    @Order(1)
    public void test1_add_new_sequence() {
        System.out.println("Testing to add a new sequence");

        //fill the form to add new sequence
        robot.clickOn("#addSeqToMetaSeq");
        robot.clickOn("#nameNewSeq").write("test to add new sequence");
        //we can't add if we don't click on radioButton
        if (!robot.lookup("#addSeqSave").query().isDisabled()) {
            fail("We can add the new sequence whereas we should not.");
        }

        robot.clickOn((RadioButton) robot.lookup("#addNewSeq").query());
        robot.clickOn("#addSeqSave");
        robot.clickOn("Oui");

        //we are looking for the new sequence and verify that it's added
        TableView<String> table = robot.lookup("#metaSeqTable").query();
        verifyThat(table, TableViewMatchers.hasTableCell("test to add new sequence"));
    }

    @Test
    @Order(2)
    public void test2_cancel_adding_new_sequence() {
        System.out.println("Testing to cancel the adding of a new sequence by click on the cancel button");
        robot.clickOn("#addSeqToMetaSeq");
        robot.clickOn((RadioButton) robot.lookup("#addNewSeq").query());
        robot.clickOn("#nameNewSeq").write("test the cancel button");
        robot.clickOn("#addSeqCancel");

        //we verify that the new sequence was not added.
        TableView<String> tableSequence = robot.lookup("#metaSeqTable").query();
        for (Object row : tableSequence.getItems()) {
            assertNotEquals(((Sequence) row).getName(), "test the cancel button");
        }
    }

    @Test
    @Order(3)
    public void test3_cancel_adding_new_sequence() {
        System.out.println("Testing to cancel the adding of a new sequence by click on the no button and cancel button");
        robot.clickOn("#addSeqToMetaSeq");
        robot.clickOn((RadioButton) robot.lookup("#addNewSeq").query());
        robot.clickOn("#nameNewSeq").write("test the cancel button");
        robot.clickOn("#addSeqSave");
        robot.clickOn("Non");
        robot.clickOn("#addSeqCancel");

        //we verify that the new sequence was not added.
        TableView<String> tableSequence = robot.lookup("#metaSeqTable").query();
        for (Object row : tableSequence.getItems()) {
            assertNotEquals(((Sequence) row).getName(), "test the cancel button");
        }
    }

}

