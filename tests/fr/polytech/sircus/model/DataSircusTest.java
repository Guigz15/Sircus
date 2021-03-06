package fr.polytech.sircus.model;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataSircusTest {

    private static DataSircus dataSircusTest;

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestSetterAndGetter{

        private DataSircus testDataSircus;

        @BeforeAll
        void initialisation(){
            testDataSircus = new DataSircus();
            System.out.println("Test getters and setters of class DataSircus");
        }

        @Test
        void setLocationsList() {
            ArrayList<String> newListLocations = new ArrayList<>(List.of(new String[]{"LocationTest1", "LocationTest2"}));
            testDataSircus.setLocationsList(newListLocations);
            assertEquals(testDataSircus.getLocationsList(),newListLocations);
        }

        @Test
        void setMetaSequencesList() {
            MetaSequence newMetaSequence = new MetaSequence("test");
            ArrayList<MetaSequence> newListMetaSequence = new ArrayList<>(List.of(new MetaSequence[]{newMetaSequence}));
            testDataSircus.setMetaSequencesList(newListMetaSequence);
            assertEquals(testDataSircus.getMetaSequencesList(),newListMetaSequence);
            assertEquals(testDataSircus.getMetaSequencesList().size(),newListMetaSequence.size());

        }

    }

    @BeforeAll
    static void initTest(){
        dataSircusTest = new DataSircus();
    }

    @Test
    void saveMetaSeq() {
        MetaSequence newMetaSequence = new MetaSequence("test Sequence");
        dataSircusTest.saveMetaSeq(newMetaSequence);
        assertEquals(dataSircusTest.getMetaSequencesList().get(0),newMetaSequence);
    }

    @Test
    void addLocationToList() {
        String newLocation = "LocationTest";
        dataSircusTest.addLocationToList(newLocation);
        assertEquals(dataSircusTest.getLocationsList().get(0),newLocation);
    }


}