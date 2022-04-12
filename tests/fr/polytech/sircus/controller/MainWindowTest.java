package fr.polytech.sircus.controller;


import fr.polytech.sircus.SircusApplication;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.matcher.base.NodeMatchers;

import java.time.LocalDate;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainWindowTest{

    private static FxRobot robot;

    @BeforeAll
    public static void setup() throws Exception {
        System.out.println("*--- Testing the main UI ---*");
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(SircusApplication.class);
        robot = new FxRobot();
    }

    @AfterEach
    public void cleanup() {
        ((DatePicker) robot.lookup("#birthDate").query()).getEditor().clear();
        ComboBox<String> location = robot.lookup("#location").query();
        robot.interact(() -> location.getSelectionModel().select(null));
        ((TextField) robot.lookup("#id").query()).clear();
        ((TextField) robot.lookup("#name").query()).clear();
        ((RadioButton) robot.lookup("M").query()).setSelected(false);

    }


    @Test
    @Order(1)
    public void test1_all_component_are_defined() {
        System.out.println("Testing if all components are initialized");
        verifyThat("#id", NodeMatchers.isNotNull());
        verifyThat("#gender_selection", NodeMatchers.isNotNull());
        verifyThat("#birthDate", NodeMatchers.isNotNull());
        verifyThat("#age", NodeMatchers.isNotNull());
        verifyThat("#ocularDom", NodeMatchers.isNotNull());
        verifyThat("#type_selection", NodeMatchers.isNotNull());
        verifyThat("#name", NodeMatchers.isNotNull());
        verifyThat("#location", NodeMatchers.isNotNull());
        verifyThat("#location_selection", NodeMatchers.isNotNull());
        verifyThat("#locationAdd", NodeMatchers.isNotNull());
        verifyThat("#method_selection", NodeMatchers.isNotNull());
        verifyThat("#method", NodeMatchers.isNotNull());
        verifyThat("#methodAdd", NodeMatchers.isNotNull());
        verifyThat("#lateral", NodeMatchers.isNotNull());
        verifyThat("#metaSeqTab", NodeMatchers.isNotNull());
        verifyThat("#resultTab", NodeMatchers.isNotNull());
    }

    @Test
    @Order(2)
    public void test2_calcul_age_function() {
        System.out.println("Testing if the calcul of the age is working");

        String date_test = "03/05/2010";
        robot.clickOn("#birthDate").write(date_test).press(KeyCode.ENTER).release(KeyCode.ENTER);
        String age = ((TextField) robot.lookup("#age").query()).getText().split(" ")[0];
        Period period = Period.between(LocalDate.of(Integer.parseInt(date_test.split("/")[2]),Integer.parseInt(date_test.split("/")[1]),Integer.parseInt(date_test.split("/")[0])), LocalDate.now());
        if (period.getYears() != Integer.parseInt(age)) {
            fail("The calcul of the age not working");
        }
        ((DatePicker) robot.lookup("#birthDate").query()).getEditor().clear();
    }

    /*
    @Test
    @Order(3)
    public void test3_add_location_cancel() {
        //test to add a location and cancel
        System.out.println("Testing to add a new location and cancel");
        //click to add new location
        robot.clickOn("#locationAdd");

        robot.clickOn("#city").write("Tours");
        robot.clickOn("#street").write("test to cancel");
        robot.clickOn("#streetNumber").write("64");
        robot.clickOn("#postCode").write("37000");

        robot.clickOn("#cancelButton");

        //we verify that the new location was not added.
        ComboBox<String> location = robot.lookup("#location").query();
        for (String loca : location.getItems()) {
            assertNotEquals(loca.getStreet(),"test to cancel");
        }
    }

    @Test
    @Order(4)
    public void test4_add_location() {
        System.out.println("Testing to add a new location");
        //click to add new location
        robot.clickOn("#locationAdd");

        ComboBox<String> location = robot.lookup("#country").query();
        robot.interact(() -> location.getSelectionModel().select("France"));

        //We fill the city
        robot.clickOn("#city").write("Tours");
        //We fill the street
        robot.clickOn("#street").write("Portalis");
        //We fill the number street
        robot.clickOn("#streetNumber").write("64");
        //We fill the post code
        robot.clickOn("#postCode").write("37000");

        //We click on the add button
        robot.clickOn("#addButton");

        robot.clickOn("#cancelButton");
        //TODO finish the test of add button of location. Remove the click to cancel


    }

    @Test
    @Order(5)
    public void test5_try_to_switch_other_tab_without_fill_correctly_form() {

        System.out.println("Testing to switch to other view without fill correctly the form : ");
        //write the identifiant
        robot.clickOn("#id").write("identifiant");
        System.out.println("-- Test without fill anythings");
        //click on save button
        robot.clickOn("#button_save");
        //need to click on Ok for quit popup alert
        robot.clickOn("OK");

        //check if we can't click on the other view
        if (!robot.lookup("#meta_seq_tab_view").query().isDisabled() || !robot.lookup("#resultat_tab_view").query().isDisabled() ) {
            fail("We can switch to other view whereas we should not.");
        }
        System.out.println("-- Fill Gender field");
        robot.clickOn((RadioButton) robot.lookup("M").query());

        robot.clickOn("#button_save");
        //need to click on Ok for 3 popup alert
        robot.clickOn("OK");

        System.out.println("-- Fill birthDate field");
        robot.clickOn("#birthDate").write("03/05/2010").press(KeyCode.ENTER).release((KeyCode.ENTER));
        //click on save button
        robot.clickOn("#button_save");

        //need to click on Ok for quit popup alert
        robot.clickOn("OK");

        //check if we can't click on the other view
        if (!robot.lookup("#meta_seq_tab_view").query().isDisabled() || !robot.lookup("#resultat_tab_view").query().isDisabled() ) {
            fail("We can switch to other view whereas we should not.");
        }

        System.out.println("-- Fill Name field");
        robot.clickOn("#name").write("Name_Patient_Test");
        //click on save button
        robot.clickOn("#button_save");
        //need to click on Ok for quit popup alert
        robot.clickOn("OK");

        //check if we can't click on the other view
        if (!robot.lookup("#meta_seq_tab_view").query().isDisabled() || !robot.lookup("#resultat_tab_view").query().isDisabled() ) {
            fail("We can switch to other view whereas we should not.");
        }

        ComboBox<Location> location = robot.lookup("#location").query();
        robot.interact(() -> location.getSelectionModel().select(0));
        //click on save button
        robot.clickOn("#button_save");
        //check if we can click on the other view
        if (robot.lookup("#meta_seq_tab_view").query().isDisabled() || robot.lookup("#resultat_tab_view").query().isDisabled() ) {
            fail("We can't switch to other view.");
        }
    }
    */
}