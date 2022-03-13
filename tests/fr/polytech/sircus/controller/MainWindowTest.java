package fr.polytech.sircus.controller;


import fr.polytech.sircus.SircusApplication;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.matcher.base.NodeMatchers;

import java.time.LocalDate;
import java.time.Period;
import java.time.chrono.Chronology;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;

public class MainWindowTest{

    private static FxRobot robot;

    @BeforeAll
    public static void setup() throws Exception {
        System.out.println("Testing the main UI");
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(SircusApplication.class);
        robot = new FxRobot();
    }

    @AfterAll
    public void cleanup() {
        ((DatePicker) robot.lookup("#birthDate").query()).getEditor().clear();
    }


    @Test
    public void test_all_component_are_defined() {
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
    public void test_calcul_age_function() {
        System.out.println("Testing if the calcul of the age is working");

        String date_test = "03/05/2010";
        robot.clickOn("#birthDate").write(date_test).press(KeyCode.ENTER).release(KeyCode.ENTER);
        String age = ((TextField) robot.lookup("#age").query()).getText().split(" ")[0];
        Period period = Period.between(LocalDate.of(Integer.valueOf(date_test.split("/")[2]),Integer.valueOf(date_test.split("/")[1]),Integer.valueOf(date_test.split("/")[0])), LocalDate.now());
        if (period.getYears() != Integer.valueOf(age)) {
            fail("The calcul of the age not working");
        }
        ((DatePicker) robot.lookup("#birthDate").query()).getEditor().clear();
    }

    @Test
    public void test_try_to_switch_other_tab_without_fill_correctly_form() {

        System.out.println("Testing to switch to other view without fill correctly the form : ");
        //write the identifiant
        robot.clickOn("#id").write("identifiant");

        System.out.println("-- Fill Gender fields");
        //click on save button
        robot.clickOn("#button_save");
        //need to click on Ok for 4 popup alert
        robot.clickOn("OK");
        robot.clickOn("OK");
        robot.clickOn("OK");
        robot.clickOn("OK");

        //check if we can't click on the other view
        if (!robot.lookup("#meta_seq_tab_view").query().isDisabled() && !robot.lookup("#resultat_tab_view").query().isDisabled() ) {
            fail("We can switch to other view whereas we should not.");
        }

        System.out.println("-- Fill birthDate fields");

        robot.clickOn("#birthDate").write("03/05/2010").press(KeyCode.ENTER).release((KeyCode.ENTER));
        //click on save button
        robot.clickOn("#button_save");
        //need to click on Ok for 3 popup alert
        robot.clickOn("OK");
        robot.clickOn("OK");
        robot.clickOn("OK");

        //check if we can't click on the other view
        if (!robot.lookup("#meta_seq_tab_view").query().isDisabled() && !robot.lookup("#resultat_tab_view").query().isDisabled() ) {
            fail("We can switch to other view whereas we should not.");
        }

    }
}