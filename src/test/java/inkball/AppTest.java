package inkball;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PImage;
import processing.core.PApplet;
import java.io.*;


import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private App app;

    // Initialize the App object before each test
    @BeforeEach
    public void setUp() {
        app = new App();
        PApplet.runSketch(new String[] {"inkball"}, app);
        app.setup();
    }

    @Test
    void testDraw() {
        // Test the draw method to ensure it does not throw any exceptions.
        assertDoesNotThrow(() -> app.draw());
    }

    @Test
    void testDrawEnded() {
        // Test the drawEnded method to ensure it does not throw exceptions.
        assertDoesNotThrow(() -> app.drawEnded());
        
        // Capture output to verify it contains the expected string
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        app.drawEnded();
        String output = outputStream.toString();
        assertTrue(output.contains("***Ended***")); // Check if output contains "Ended"
    }


    @Test
    void testGetLevel() {
        // Test retrieving the first level and ensure the current level and index are correctly set.
        app.getLevel(0);
        assertNotNull(app.currentLevel); // Ensure current level is not null
        assertEquals(0, app.currentLevelIndex); // Ensure current level index is 0
    }

    @Test
    public void testGetColorFromCode() {
        // Test color retrieval from code to ensure correct mapping.
        assertEquals("grey", app.getColorFromCode("0"));
        assertEquals("orange", app.getColorFromCode("1"));
        assertEquals("blue", app.getColorFromCode("2"));
        assertEquals("green", app.getColorFromCode("3"));
        assertEquals("yellow", app.getColorFromCode("4"));
        assertEquals("unknown", app.getColorFromCode("5"));
    }

    @Test
    public void testGetCodeFromColor() {
        // Test code retrieval from color to ensure correct mapping.
        assertEquals("0", app.getCodeFromColor("grey"));
        assertEquals("1", app.getCodeFromColor("orange"));
        assertEquals("2", app.getCodeFromColor("blue"));
        assertEquals("3", app.getCodeFromColor("green"));
        assertEquals("4", app.getCodeFromColor("yellow"));
        assertEquals("unknown", app.getCodeFromColor("purple"));
    }

    @Test
    public void testGetBallImage() {
        // Test retrieving a ball image by color.
        PImage image = app.getBallImage("grey");
        assertNotNull(image);
    }

    @Test
    public void testGetBallImageFail() {
        // Handle unknown color
        PImage image = app.getBallImage("unknown");
        assertNull(image); // 应该返回 null
    }

    @Test
    public void testAddScore() {
        // Test the addScore method to verify score updates correctly.
        app.score = 0;
        app.addScore(10);
        assertEquals(10, app.score);
    }

    @Test
    void testDrawLevelFinishedAddScore() {
        // Test score addition when the current level is marked as finished.
        app.currentLevel = new Level(0, app.configObject);
        app.currentLevel.setupLevel(app);
        
        // Assume the current level is finished
        app.currentLevel.isLevelFinished = true;
        app.currentLevel.LevelFinishedAddScore(app); // Test score addition
        assertTrue(app.getLevelScore() > 0); // Ensure score is updated
    }

    @Test
    public void testDrawScore() {
        // Test the redraw method to ensure it does not throw exceptions when score is displayed.
        app.score = 10;
        assertDoesNotThrow(() -> {
            app.redraw(); // redraw
        });
    }

    @Test
    public void testLoadLevels() {
        // Test loading levels to ensure the correct number of levels are loaded.
        app.loadLevels();
        assertEquals(3, app.levels.size());
    }

    @Test
    void testKeyPressedSpace(){
        // Test handling of space key press, ensuring the game can be paused.
        Level currentLevel = new Level(0, app.configObject);
        KeyEvent spacePressEvent = new KeyEvent(null, 50, KeyEvent.PRESS, 0, ' ', 32);
        app.keyPressed(spacePressEvent);
        assertDoesNotThrow(() -> app.keyPressed(spacePressEvent));
        assertDoesNotThrow(() -> app.currentLevel.pauseTheGame()); // Ensure game can be paused
    }

    @Test
    void testKeyPressedR(){
        // Test handling of 'r' key press to ensure it functions correctly.
        KeyEvent rPressEvent = new KeyEvent(null, 50, KeyEvent.PRESS, 0, 'r', 32);
        app.keyPressed(rPressEvent);
        assertDoesNotThrow(() -> app.keyPressed(rPressEvent));
    }

    @Test
    void testKeyPressedRActions(){
        // Test the actions taken when 'r' is pressed while the game is over.
        app.wholeGameOver = true;
        KeyEvent rPressEvent = new KeyEvent(null, 50, KeyEvent.PRESS, 0, 'r', 32);
        app.keyPressed(rPressEvent);
        assertFalse(app.wholeGameOver);
    }

    @Test
    void testMouseDragged() {
        // Test handling of mouse dragged events to ensure they do not throw exceptions.
        long mouseX = 100;
        int mouseY = 150;
        MouseEvent event = new MouseEvent(app, mouseX, mouseY, 0, 0, 0, 0, 0);
        assertDoesNotThrow(() -> app.mouseDragged(event));
    }

    @Test
    void testMouseDraggedActions() {
        // Test the actions taken when the mouse is dragged to ensure new points are added correctly.
        Level currentLevel = new Level(0, app.configObject);
        app.currentLevel.setupLevel(app);
        long mouseX = 100;
        int mouseY = 150;
        MouseEvent event = new MouseEvent(app, mouseX, mouseY, 0, 0, 0, 0, 0);
        app.mouseDragged(event);
        // Test adding line point
        assertDoesNotThrow(() -> app.currentLevel.addNewLinePoint((int) mouseX, mouseY)); 
    }

    @Test
    void testMouseReleased(){
        // Test handling of mouse released events to ensure they do not throw exceptions.
        long mouseX = 100;
        int mouseY = 150;
        MouseEvent event = new MouseEvent(app, mouseX, mouseY, 0, 0, 0, 0, 0);
        Level currentLevel = new Level(0, app.configObject);
        app.currentLevel.setupLevel(app);
        // Test mouse released event
        assertDoesNotThrow(() -> app.mouseReleased(event));
    }

    @Test
    public void testSetup() {
        // Test the setup method to ensure configuration and current level are initialized.
        assertNotNull(app.configObject); // Ensure configuration object is initialized
        assertNotNull(app.currentLevel); // Ensure current level is initialized
    }

    @Test
    void testDrawNextLevel() {
        // Test transitioning to the next level after the current level is finished.
        app.currentLevel = new Level(0, app.configObject);
        app.currentLevel.setupLevel(app);
        
        // Simulate current level completion
        app.currentLevel.isLevelFinished = true;
        app.currentLevel.LevelFinishedAddScore(app);
        // Ensure the current level is finished
        assertTrue(app.currentLevel.isLevelFinished());
        assertDoesNotThrow(() -> app.draw());  // Test draw method, ensure no exceptions
        
        // Verify level index is updated
        assertEquals(1, app.currentLevelIndex);
    }

    @Test
    void testGameOver() {
        // Test the game over conditions when the last level is reached.
        app.currentLevel = new Level(0, app.configObject);
        app.currentLevel.setupLevel(app);
        
        // Simulate reaching the last level
        app.currentLevelIndex = app.levels.size() - 1;
        app.currentLevel.isLevelFinished = true;
        app.currentLevel.LevelFinishedAddScore(app);
        // Ensure the current level is finished
        assertTrue(app.currentLevel.isLevelFinished()); 
        assertDoesNotThrow(() -> app.draw()); // Test draw method, ensure no exceptions
        assertEquals(app.score, app.wholeScore); // Ensure score is correct
    }

    @Test
    void testWholeGameOver(){
        // Test the state when the whole game is over and ensure initial values are correct.
        app.wholeGameOver = true;
        assertEquals(0, app.currentLevelIndex); // Ensure current level index is 0
        assertEquals(0, app.score); // Ensure score is 0
        assertDoesNotThrow(() -> app.drawEnded()); // Test drawEnded method, ensure no exceptions
    }

    @Test
    void testFinalScore(){
        // Test final score after the last level is completed to ensure it matches the total score.
        app.currentLevel = new Level(0, app.configObject);
        app.currentLevel.setupLevel(app);
        app.currentLevel.isLevelEnded = true;
        app.currentLevel.isLevelFinished = true;
        app.currentLevelIndex = app.levels.size() - 1;
        float totalScore = app.score;
        assertEquals(totalScore, app.wholeScore); // Verify total score matches
    }

    @Test
    void testDrawHandlesLevelFinish() {
        // Test the draw method to ensure it handles level completion properly and transitions to the next level.
        app.currentLevel = new Level(0, app.configObject);
        app.currentLevel.setupLevel(app);
        app.currentLevelIndex = 0;
        app.currentLevel.isLevelEnded = true;
        app.currentLevel.LevelFinishedAddScore(app);
        app.currentLevel.isLevelFinished = true;
        
        // Verify level index is updated
        assertEquals(1, app.currentLevelIndex);
    }

    @Test
    void testMain() {
        // Test the main method to ensure it does not throw exceptions when starting the application.
        assertDoesNotThrow(() -> App.main(new String[] {}));
    }

}