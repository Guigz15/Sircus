package fr.polytech.sircus.tests;

import fr.polytech.sircus.model.DataSircus;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataSircusTest {


    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestSetterAndGetter{

        private DataSircus testDataSircus;

        @BeforeAll
        void initialisation(){
            testDataSircus = new DataSircus();
            System.out.println("Tests des getters et setters de la classe DataSircus");
        }

        @Test
        void setLocationList() {
            testDataSircus.setLocationList("path\\test");
            assertEquals("path\\test",testDataSircus.getLocationsList());
        }

        @Test
        void setLocationsList() {
            ArrayList<String> newListLocations = new ArrayList<String>(List.of(new String[]{"path\\setTest"}));
            testDataSircus.setLocationsList(newListLocations);
            assertEquals(testDataSircus.getLocationsList(),newListLocations);
        }

        @Test
        void setMetaSequencesList() {
            fail("Pas implementer");
        }

    }



    @Test
    void saveMetaSeq() {
        fail("Pas implementer");
    }

    @Test
    void addLocationToList() {
        fail("Pas implementer");
    }


}