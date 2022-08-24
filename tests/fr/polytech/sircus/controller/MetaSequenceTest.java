package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import fr.polytech.sircus.model.MetaSequence;
import fr.polytech.sircus.model.Sequence;
import fr.polytech.sircus.utils.ItemSequence;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.matcher.control.TextMatchers.hasText;

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
        robot.clickOn((RadioButton) robot.lookup("Droit").query());
        robot.clickOn((RadioButton) robot.lookup("Gaucher").query());
        robot.clickOn("#name").write("Name_User_Test");
        robot.clickOn("#forename").write("Forename_User_Test");
        ComboBox<String> location = robot.lookup("#locations").query();
        robot.interact(() -> location.getSelectionModel().select(0));
        robot.clickOn("#admin");
        robot.clickOn("#userName").write("admin");
        robot.clickOn("#password").write("password");
        robot.press(KeyCode.ENTER);
        robot.clickOn("#next");
    }

    @Test
    @Order(1)
    public void test1_add_new_metasequence() {
        System.out.println("Testing to add a new meta-sequence");

        ListView<MetaSequence> metaSequenceListView = robot.lookup("#metaListView").query();

        robot.clickOn("#addMetaButton");

        assertTrue(metaSequenceListView.getItems().contains(new MetaSequence("Nouvelle MetaSequence " + metaSequenceListView.getItems().size())));
    }

    @Test
    @Order(2)
    public void test2_rename_metasequence() {
        System.out.println("Testing to rename a meta-sequence");

        ListView<MetaSequence> metaSequenceListView = robot.lookup("#metaListView").query();

        robot.clickOn((Node) robot.lookup(hasText("Nouvelle MetaSequence " + metaSequenceListView.getItems().size())).query());
        robot.clickOn("#renameMetaButton");
        robot.clickOn("#metasequenceName").doubleClickOn(MouseButton.PRIMARY);
        robot.write("Rename test");
        robot.press(KeyCode.ENTER);

        assertTrue(metaSequenceListView.getItems().contains(new MetaSequence("Rename test")));
    }

    @Test
    @Order(3)
    public void test3_remove_metasequence() {
        System.out.println("Testing to remove a meta-sequence");

        ListView<MetaSequence> metaSequenceListView = robot.lookup("#metaListView").query();

        robot.clickOn((Node) robot.lookup(hasText("Rename test")).query());
        robot.clickOn("#removeMetaButton");

        assertFalse(metaSequenceListView.getItems().contains(new MetaSequence("Rename test")));
    }

    @Test
    @Order(4)
    public void test4_add_new_sequence() {
        System.out.println("Testing to add a new sequence");

        robot.clickOn((Node) robot.lookup(hasText("Meta sequence 1")).query());

        ListView<ItemSequence> sequenceListView = robot.lookup("#seqListView").query();

        robot.clickOn("#addSeqButton");

        boolean seqAdded = false;
        for (ItemSequence itemSequence : sequenceListView.getItems())
            if (itemSequence.getSequence().getName().equalsIgnoreCase("Nouvelle Sequence " + sequenceListView.getItems().size())) {
                seqAdded = true;
                break;
            }

        assertTrue(seqAdded);
    }

    @Test
    @Order(5)
    public void test5_rename_sequence() {
        System.out.println("Testing to rename a sequence");

        ListView<ItemSequence> sequenceListView = robot.lookup("#seqListView").query();

        robot.clickOn((Node) robot.lookup(hasText("Nouvelle Sequence " + sequenceListView.getItems().size())).query());
        robot.clickOn("#modifySeqButton");
        robot.clickOn("#sequenceName").doubleClickOn(MouseButton.PRIMARY);
        robot.write("Rename test");
        robot.type(KeyCode.ENTER);

        boolean seqUpdated = false;
        for (ItemSequence itemSequence : sequenceListView.getItems())
            if (itemSequence.getSequence().getName().equalsIgnoreCase("Rename test")) {
                seqUpdated = true;
                break;
            }

        assertTrue(seqUpdated);
    }

    @Test
    @Order(6)
    public void test6_remove_sequence() {
        System.out.println("Testing to remove a sequence");

        ListView<ItemSequence> sequenceListView = robot.lookup("#seqListView").query();

        robot.clickOn((Node) robot.lookup(hasText("Rename test")).query());
        robot.clickOn("#removeSeqButton");

        boolean seqRemoved = true;
        for (ItemSequence itemSequence : sequenceListView.getItems())
            if (itemSequence.getSequence().getName().equalsIgnoreCase("Rename test")) {
                seqRemoved = false;
                break;
            }

        assertTrue(seqRemoved);
    }

    @Test
    @Order(7)
    public void test7_add_existing_media_in_sequence() {
        System.out.println("Testing to add existing media in sequence");

        ListView<ItemSequence> sequenceListView = robot.lookup("#seqListView").query();

        Sequence sequenceBeforeAdding = sequenceListView.getItems().get(0).getSequence();
        int sizeBeforeAdding = sequenceBeforeAdding.getListMedias().size();

        robot.clickOn((Node) robot.lookup(hasText("Sequence 1")).query());
        robot.clickOn("#modifySeqButton");
        robot.clickOn("#addMediaToSeq");
        robot.clickOn("#mediasList");
        // To select a media in the list
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        // To valid the adding
        robot.type(KeyCode.ENTER);
        // To change focus and save adding
        robot.clickOn("#sequenceName");
        robot.type(KeyCode.ENTER);

        Sequence sequenceAfterAdding = sequenceListView.getItems().get(0).getSequence();
        int sizeAfterAdding = sequenceAfterAdding.getListMedias().size();

        assertNotEquals(sizeBeforeAdding, sizeAfterAdding);
    }

    @Test
    @Order(8)
    public void test8_cancel_adding_existing_media_in_sequence() {
        System.out.println("Testing to cancel adding existing media in sequence");

        ListView<ItemSequence> sequenceListView = robot.lookup("#seqListView").query();

        Sequence sequenceBeforeCanceling = sequenceListView.getItems().get(0).getSequence();
        int sizeBeforeCanceling = sequenceBeforeCanceling.getListMedias().size();

        robot.clickOn((Node) robot.lookup(hasText("Sequence 1")).query());
        robot.clickOn("#modifySeqButton");
        robot.clickOn("#addMediaToSeq");
        robot.clickOn("#mediasList");
        // To select a media in the list
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        // To valid the adding
        robot.type(KeyCode.ENTER);
        // To cancel adding
        robot.type(KeyCode.ESCAPE);

        Sequence sequenceAfterCanceling = sequenceListView.getItems().get(0).getSequence();
        int sizeAfterCanceling = sequenceAfterCanceling.getListMedias().size();

        assertEquals(sizeBeforeCanceling, sizeAfterCanceling);
    }

    @Test
    @Order(9)
    public void test9_add_media_in_sequence() {
        System.out.println("Testing to add media in sequence");

        robot.clickOn((Node) robot.lookup(hasText("Meta sequence 1")).query()); // REMOVE AFTER IT IS OK

        ListView<ItemSequence> sequenceListView = robot.lookup("#seqListView").query();

        Sequence sequenceBeforeAdding = sequenceListView.getItems().get(0).getSequence();
        int sizeBeforeAdding = sequenceBeforeAdding.getListMedias().size();

        robot.clickOn((Node) robot.lookup(hasText("Sequence 1")).query());
        robot.clickOn("#modifySeqButton");
        robot.clickOn("#addMediaToSeq");
        robot.clickOn("#newMediaButton");
        robot.clickOn("#addMediaFile");
        robot.sleep(3000); // ------------- YOU HAVE 3s TO SELECT THE FILE -------------
        robot.clickOn("#mediaDuration").doubleClickOn(MouseButton.PRIMARY);
        robot.write("1-3");
        robot.type(KeyCode.ENTER);
        // To change focus and save adding
        robot.clickOn("#sequenceName");
        robot.type(KeyCode.ENTER);

        Sequence sequenceAfterAdding = sequenceListView.getItems().get(0).getSequence();
        int sizeAfterAdding = sequenceAfterAdding.getListMedias().size();

        assertNotEquals(sizeBeforeAdding, sizeAfterAdding);
    }
}

