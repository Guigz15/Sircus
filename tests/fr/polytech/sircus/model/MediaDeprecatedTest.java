package fr.polytech.sircus.model;

import java.time.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

class MediaDeprecatedTest {

    private static MediaDeprecated mediaTest;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestGetter {

        private MediaDeprecated mediaTest;

        @BeforeAll
        void initialisation() {
            System.out.println("Tests sur les getters de la classe Media");
            Duration duration = Duration.ofSeconds(15);
            mediaTest = new MediaDeprecated("mediaTest", "filename", duration, TypeMedia.PICTURE, null);
        }

        @Test
        void getName() {
            assertEquals("mediaTest", mediaTest.getName());
        }

        @Test
        void getFilename() {
            assertEquals("filename", mediaTest.getFilename());
        }

        @Test
        void getDuration() {
            Duration testDuration = Duration.ofSeconds(15);
            assertTrue(testDuration.equals(mediaTest.getDuration()));
        }

        @Test
        void getType() {
            assertTrue(mediaTest.getType() == TypeMedia.PICTURE);
        }

        @Test
        void getInterStim() {
            assertNull(mediaTest.getInterStim());
        }

        @Test
        void getLock() {
            assertTrue(mediaTest.getLock());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestSetter{
        private MediaDeprecated mediaTest;

        @BeforeAll
        void initialisation() {
            System.out.println("Tests sur les setters de la classe Media");
            mediaTest = new MediaDeprecated();
        }

        @Test
        void setName() {
            String newName = "testMediaName";
            mediaTest.setName(newName);
            assertEquals("testMediaName",mediaTest.getName());
        }

        @Test
        void setFilename() {
            String newName = "newName";
            mediaTest.setFilename(newName);
            assertEquals("newName",mediaTest.getFilename());
        }

        @Test
        void setDuration() {
            Duration testDuration = Duration.ofSeconds(30);
            mediaTest.setDuration(testDuration);
            assertTrue(testDuration.equals(mediaTest.getDuration()));
        }

        @Test
        void setType() {
            mediaTest.setType(TypeMedia.VIDEO);
            assertTrue(mediaTest.getType() == TypeMedia.VIDEO);
        }

        @Test
        void setInterStim() {
            MediaDeprecated testInterStim = new MediaDeprecated("stim","testPath\\inter_stim",Duration.ofSeconds(15),TypeMedia.PICTURE,null);
            mediaTest.setInterStim(testInterStim);
            assertEquals(testInterStim,mediaTest.getInterStim());
        }

        @Test
        void setLock() {
            mediaTest.setLock(Boolean.FALSE);
            assertFalse(mediaTest.getLock());
        }
    }




}