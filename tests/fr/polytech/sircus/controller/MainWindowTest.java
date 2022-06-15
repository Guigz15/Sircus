package fr.polytech.sircus.controller;

import fr.polytech.sircus.SircusApplication;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.matcher.base.NodeMatchers;
import java.time.LocalDate;
import java.time.Period;
import static org.junit.jupiter.api.Assertions.*;
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
        ((TextField) robot.lookup("#id").query()).clear();
        ((RadioButton) robot.lookup("M").query()).setSelected(false);
        ((DatePicker) robot.lookup("#birthDate").query()).getEditor().clear();
        ((TextField) robot.lookup("#name").query()).clear();
        ((TextField) robot.lookup("#forename").query()).clear();
        ComboBox<String> locations = robot.lookup("#locations").query();
        robot.interact(() -> locations.getSelectionModel().select(null));
    }


    @Test
    @Order(1)
    public void test1_all_component_are_defined() {
        System.out.println("Testing if all components are initialized");
        verifyThat("#id", NodeMatchers.isNotNull());
        verifyThat("#sex", NodeMatchers.isNotNull());
        verifyThat("#birthDate", NodeMatchers.isNotNull());
        verifyThat("#age", NodeMatchers.isNotNull());
        verifyThat("#ocularDom", NodeMatchers.isNotNull());
        verifyThat("#manualLateral", NodeMatchers.isNotNull());
        verifyThat("#type", NodeMatchers.isNotNull());
        verifyThat("#name", NodeMatchers.isNotNull());
        verifyThat("#forename", NodeMatchers.isNotNull());
        verifyThat("#locations", NodeMatchers.isNotNull());
        verifyThat("#methods", NodeMatchers.isNotNull());
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

    @Test
    @Order(3)
    public void test3_add_location_cancel() {
        //test to add a location and cancel
        System.out.println("Testing to add a new location and cancel");
        //click to add new location
        robot.clickOn("#locationAdd");

        robot.clickOn("#locationField").write("Test location to cancel");

        robot.clickOn("#cancelLocButton");

        //we verify that the new location was not added.
        ComboBox<String> locations = robot.lookup("#locations").query();
        for (String location : locations.getItems()) {
            assertNotEquals(location,"Test location to cancel");
        }
    }

    @Test
    @Order(4)
    public void test4_add_location() {
        System.out.println("Testing to add a new location");
        //click to add new location
        robot.clickOn("#locationAdd");

        robot.clickOn("#locationField").write("Test location to add");

        robot.clickOn("#validLocButton");

        // we verify that the new location was added.
        ComboBox<String> locations = robot.lookup("#locations").query();
        assertTrue(locations.getItems().contains("Test location to add"));
    }

    @Test
    @Order(5)
    public void test5_update_location_cancel() {
        System.out.println("Testing to update a location and cancel");

        robot.clickOn("#locations");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#locationUpdate");
        robot.clickOn("#locationField").doubleClickOn(MouseButton.PRIMARY);
        robot.clickOn("#locationField").write("Test location update to cancel");
        robot.clickOn("#cancelLocButton");

        //we verify that the location was not updated.
        ComboBox<String> locations = robot.lookup("#locations").query();
        for (String location : locations.getItems()) {
            assertNotEquals(location,"Test location update to cancel");
        }
    }

    @Test
    @Order(6)
    public void test6_update_location() {
        System.out.println("Testing to update a location");

        robot.clickOn("#locations");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#locationUpdate");
        robot.clickOn("#locationField").doubleClickOn(MouseButton.PRIMARY);
        robot.clickOn("#locationField").write("Test location to update");
        robot.clickOn("#validLocButton");

        // we verify that the location was updated.
        ComboBox<String> locations = robot.lookup("#locations").query();
        assertTrue(locations.getItems().contains("Test location to update"));
    }


    @Test
    @Order(7)
    public void test7_remove_location_cancel() {
        System.out.println("Testing to remove a location and cancel");

        ComboBox<String> locations = robot.lookup("#locations").query();
        int size = locations.getItems().size();

        robot.clickOn("#locations");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#locationRemove");
        robot.type(KeyCode.ESCAPE);

        // we verify that the location wasn't be removed.
        assertEquals(size, locations.getItems().size());
    }

    @Test
    @Order(8)
    public void test8_remove_location() {
        System.out.println("Testing to remove a location");

        ComboBox<String> locations = robot.lookup("#locations").query();
        int size = locations.getItems().size();

        robot.clickOn("#locations");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#locationRemove");
        robot.type(KeyCode.ENTER);

        // we verify that the location wasn't be removed.
        assertNotEquals(size, locations.getItems().size());
    }

    @Test
    @Order(9)
    public void test9_add_method_cancel() {
        //test to add a method and cancel
        System.out.println("Testing to add a new method and cancel");
        //click to add new method
        robot.clickOn("#methodAdd");

        robot.clickOn("#methodField").write("Test method to cancel");

        robot.clickOn("#cancelMethodButton");

        //we verify that the new method was not added.
        ComboBox<String> methods = robot.lookup("#methods").query();
        for (String method : methods.getItems()) {
            assertNotEquals(method,"Test method to cancel");
        }
    }

    @Test
    @Order(10)
    public void test10_add_method() {
        System.out.println("Testing to add a new method");
        //click to add new method
        robot.clickOn("#methodAdd");

        robot.clickOn("#methodField").write("Test method to add");

        robot.clickOn("#validMethodButton");

        // we verify that the new method was added.
        ComboBox<String> methods = robot.lookup("#methods").query();
        assertTrue(methods.getItems().contains("Test method to add"));
    }

    @Test
    @Order(11)
    public void test11_update_method_cancel() {
        System.out.println("Testing to update a method and cancel");

        robot.clickOn("#methods");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#methodUpdate");
        robot.clickOn("#methodField").doubleClickOn(MouseButton.PRIMARY);
        robot.clickOn("#methodField").write("Test method update to cancel");
        robot.clickOn("#cancelMethodButton");

        //we verify that the method was not updated.
        ComboBox<String> methods = robot.lookup("#methods").query();
        for (String method : methods.getItems()) {
            assertNotEquals(method,"Test method update to cancel");
        }
    }

    @Test
    @Order(12)
    public void test12_update_method() {
        System.out.println("Testing to update a method");

        robot.clickOn("#methods");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#methodUpdate");
        robot.clickOn("#methodField").doubleClickOn(MouseButton.PRIMARY);
        robot.clickOn("#methodField").write("Test method to update");
        robot.clickOn("#validMethodButton");

        // we verify that the method was updated.
        ComboBox<String> methods = robot.lookup("#methods").query();
        assertTrue(methods.getItems().contains("Test method to update"));
    }


    @Test
    @Order(13)
    public void test13_remove_method_cancel() {
        System.out.println("Testing to remove a method and cancel");

        ComboBox<String> methods = robot.lookup("#methods").query();
        int size = methods.getItems().size();

        robot.clickOn("#methods");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#methodRemove");
        robot.type(KeyCode.ESCAPE);

        // we verify that the method wasn't be removed.
        assertEquals(size, methods.getItems().size());
    }

    @Test
    @Order(14)
    public void test14_remove_method() {
        System.out.println("Testing to remove a method");

        ComboBox<String> methods = robot.lookup("#methods").query();
        int size = methods.getItems().size();

        robot.clickOn("#methods");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.clickOn("#methodRemove");
        robot.type(KeyCode.ENTER);

        // we verify that the method wasn't be removed.
        assertNotEquals(size, methods.getItems().size());
    }

    @Test
    @Order(15)
    public void test15_adminConnection() {
        System.out.println("Testing the admin connection");

        robot.clickOn("#admin");
        robot.clickOn("#userName").write("root");
        robot.clickOn("#password").write("nothing");
        robot.press(KeyCode.ENTER);
        assertFalse(SircusApplication.adminConnected);

        robot.clickOn("#admin");
        robot.clickOn("#userName").write("root");
        robot.clickOn("#password").write("password");
        robot.press(KeyCode.ENTER);
        assertFalse(SircusApplication.adminConnected);
    }

    @Test
    @Order(16)
    public void test16_adminDisconnection() {
        System.out.println("Testing the admin disconnection");

        robot.clickOn("#admin");
        robot.clickOn("#userName").write("root");
        robot.clickOn("#password").write("password");
        robot.press(KeyCode.ENTER);

        robot.clickOn("#adminLogOut");
        assertFalse(SircusApplication.adminConnected);
    }
}