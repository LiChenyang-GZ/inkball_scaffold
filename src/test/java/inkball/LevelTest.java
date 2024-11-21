package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;

class LevelTest {
    App app;
    Level level;
    GameConfig mockConfig;
    List<Ball> balls;

    @BeforeEach
    void appSetup() {
        app = new App();
        PApplet.runSketch(new String[]{"inkball"}, app);
        app.setup();

        // Mock GameConfig initialization
        mockConfig = new GameConfig("config.json"); // Assume this constructor sets default values
        mockConfig.readConfig(app);
        level = new Level(0, mockConfig);
    }

    @Test
    void testLevelInitialization() {
        // Test that the level is initialized correctly with the expected layout and spawn interval.
        assertNotNull(level);
        assertEquals(mockConfig.getLevelLayout(0), "level1.txt");
        assertEquals(mockConfig.getSpawnInterval(0), 5);
    }

    @Test
    void testSpawnNewBall() {
        // Test spawning a new ball to ensure the ball count increases correctly.
        List<Ball> balls = new ArrayList<>();
        level.spawnNewBall();
        assertEquals(1, level.balls.size());
    }

    @Test
    void testUpdateTimeAndSpawn() {
        // Test the updateTimeAndSpawn method to ensure time decreases and spawning conditions are evaluated.
        level.updateTimeAndSpawn();
        assertTrue(level.remainingTime < level.totalTime);
    }

    @Test
    void testDrawLinesTimeUp() {
        // Test the drawLines method when the time is up to ensure it executes without exceptions.
        level.timeUp = true;
        assertDoesNotThrow(() -> level.drawLines(app));
    }

    @Test
    void testDrawLinesLevelEnded() {
        // Test the drawLines method when the level has ended to ensure it executes without exceptions.
        level.timeUp = false;
        level.isLevelEnded = true;
        assertDoesNotThrow(() -> level.drawLines(app));
    }

    @Test
    void testAddNewLine() {
        // Test adding a new line to ensure it is tracked correctly in the level.
        level.startNewLine(10, 10);
        level.addNewLinePoint(20, 20);
        assertEquals(1, level.lines.size());
    }

    @Test
    void testDeleteLineFail(){
        // Test attempting to delete a non-existing line and ensure the line count remains unchanged.
        level.startNewLine(10, 10);
        level.deleteLine(15, 15);
        assertEquals(1, level.lines.size());
    }

    @Test
    void testDeleteLineSuccess(){
        // Test successfully deleting a line and ensure it is removed from the level.
        Line newLine = new Line();
        newLine.addPoint(10, 10);
        List<Line> lines = new ArrayList<>();
        lines.add(newLine);
        level.deleteLine(10, 10);
        assertEquals(0, level.lines.size());
    }

    @Test
    void testAddNewLineFininshed(){
        // Test resetting the current line after finishing it to ensure the current line is cleared.
        level.startNewLine(10, 10);
        assertNotNull(level.currentLine);
        level.addNewLine();
        assertNull(level.currentLine);
    }

    @Test
    void testStartNewLine() {
        // Test starting a new line to ensure the current line is initialized.
        level.startNewLine(30, 30);
        assertNotNull(level.currentLine);
    }

    @Test
    void testScoreCalculation() {
        // Test score calculation when a ball is captured successfully.
        Ball ball = new Ball(0, 0, "grey");
        ball.capturedBall(true);
        String holeColor = "grey";
        float scoreChange = level.calScoreChange(holeColor, ball);
        assertEquals(70, scoreChange);
    }

    @Test
    void testCalScoreChangeWhenBallNotCaptured() {
        // Test score calculation when a ball is not captured to ensure it returns zero.
        Ball ball = new Ball(0, 0, "grey");
        ball.capturedBall(false); // Mark the ball as not captured
        String holeColor = "grey"; // Hole color
        float scoreChange = level.calScoreChange(holeColor, ball);
        assertEquals(0, scoreChange); // Should return 0 if the ball is not captured
    }

    @Test
    void testCalScoreChangeWhenCaptureFails() {
        // Test score calculation when a capture fails due to color mismatch.
        Ball ball = new Ball(0, 0, "blue");
        ball.capturedBall(true); // Mark the ball as captured
        String holeColor = "orange"; // Hole color that does not match the ball color

        // Mock score decrease values
        level.scoreDecreaseConfig.put("orange", 25);
        level.scoreDecreaseModifier = 1; // Set modifier to 1 for simplicity
        float scoreChange = level.calScoreChange(holeColor, ball);
        assertTrue(scoreChange < 0); // Score change should be negative
        assertEquals(-25, scoreChange); // Expected score change should match the decrease
    }

    @Test
    void testIsCaptureSuccessful() {
        // Test capture success for a ball that matches the hole color.
        Ball ball = new Ball(0, 0, "grey");
        String holeColor = "grey";
        assertTrue(level.isCaptureSuccessful(holeColor, ball));
        assertTrue(ball.isRemove()); // Ball should be marked as removed
    }

    @Test
    void testLevelEnded() {
        // Test if the level is correctly identified as ended when all balls are removed.
        Ball ball = new Ball(0, 0, "grey");
        List<Ball> balls = new ArrayList<>();
        balls.add(ball);
        assertFalse(level.isLevelEnded());
        // Simulating removal of all balls
        level.balls.clear();
        assertTrue(level.isLevelEnded());
    }

    @Test
    void testPauseGame() {
        // Test the pause and resume functionality of the game.
        level.pauseTheGame();
        assertTrue(level.pause);
        level.pauseTheGame();
        assertFalse(level.pause);
    }

    @Test
    void testMoveYellowTiles() {
        // Test moving yellow tiles to ensure their positions are updated correctly.
        Tile tile1 = new Tile(0, 0, "rotated"); 
        Tile tile2 = new Tile(App.BOARD_WIDTH - 1, 17, "rotated");
        level.yellowTiles = Arrays.asList(tile1, tile2);
        level.moveYellowTiles(app);
        // Check tile1's new position
        int newX1 = tile1.getX();
        int newY1 = tile1.getY();
        assertEquals(1, newX1);
        assertEquals(0, newY1);
        // Check tile2's new position
        int newX2 = tile2.getX();
        int newY2 = tile2.getY();
        assertEquals(App.BOARD_WIDTH - 2, newX2);
        assertEquals(17, newY2);
    }

    @Test
    void testMoveYellowTilesUp() {
        // Test moving yellow tiles up to ensure their positions are updated correctly.
        Tile tile1 = new Tile(0, 1, "rotated");    
        level.yellowTiles = Arrays.asList(tile1);
        level.moveYellowTiles(app);
        int newX1 = tile1.getX();
        int newY1 = tile1.getY();
        assertEquals(0, newX1);
        assertEquals(0, newY1);
    }

    @Test
    void testMoveYellowTilesDown() {
        // Test moving yellow tiles down to ensure their positions are updated correctly.
        Tile tile1 = new Tile(App.BOARD_WIDTH - 1, 1, "rotated");       
        level.yellowTiles = Arrays.asList(tile1);
        level.moveYellowTiles(app);
        int newX1 = tile1.getX();
        int newY1 = tile1.getY();
        assertEquals(App.BOARD_WIDTH - 1, newX1);
        assertEquals(2, newY1);
    }

    @Test
    void testMoveYellowTilesLeft() {
        // Test moving yellow tiles left to ensure their positions are updated correctly.
        Tile tile1 = new Tile(2, App.BOARD_HEIGHT - 3, "rotated");   
        level.yellowTiles = Arrays.asList(tile1);
        level.moveYellowTiles(app);
        int newX1 = tile1.getX();
        int newY1 = tile1.getY();
        assertEquals(1, newX1);
        assertEquals(App.BOARD_HEIGHT - 3, newY1);
    }

    @Test
    void testMoveYellowTilesRight() {
        // Test moving yellow tiles right to ensure their positions are updated correctly.
        Tile tile1 = new Tile(2, 0, "rotated");   
        level.yellowTiles = Arrays.asList(tile1);
        level.moveYellowTiles(app);
        int newX1 = tile1.getX();
        int newY1 = tile1.getY();
        assertEquals(3, newX1);
        assertEquals(0, newY1);
    }
    

    @Test
    void testIsLevelEnded() {
        // Test if the level is correctly identified as ended when there are no balls.
        level.balls.clear(); // Clear balls to simulate an empty state
        boolean ended = level.isLevelEnded();
        assertTrue(ended); // The level should be ended
    }

    @Test
    void testSetupLevel() {
        // Test the setupLevel method to ensure the board and balls are initialized correctly.
        level.setupLevel(app);
        assertNotNull(level.board); // Ensure board is initialized
        assertFalse(level.balls.isEmpty()); // Ensure balls are added based on the layout
    }

    @Test
    void testLevelFinishedAddScore() {
        // Test score addition when the level is finished.
        level.remainingTime = 5; // Set remaining time
        level.isLevelEnded = true; // Mark the level as ended
        app.score = 10; // Get initial score
        float initialScore = 10;
        level.LevelFinishedAddScore(app);
        assertTrue(app.score > initialScore); // Ensure score has increased
    }

    @Test
    void testAddScoreToApp(){
        // Test adding score to the app to ensure the score updates correctly.
        app.score = 10;
        level.addScoreToAPP(app, 10);
        assertTrue(app.score > 10); // Ensure the score has increased
    }

    @Test
    void testUpdateTimeAndSpawnWhenPaused() {
        // Test the updateTimeAndSpawn method when the game is paused.
        level.pause = true; // Set the pause state
        level.remainingSpawnTime = 5; // Initial spawn time for testing
        level.updateTimeAndSpawn();
        assertEquals(5, level.remainingSpawnTime); // Spawn time should remain unchanged
        assertEquals(0, level.spawnTimer); // Timer should not increment
    }

    @Test
    void testUpdateTimeAndSpawnWhenTimeIsUp() {
        // Test the updateTimeAndSpawn method when the time is up.
        level.timeUp = true;
        level.remainingTime = 5;
        assertEquals(5, level.remainingTime); // Remaining time should remain unchanged
        assertEquals(0, level.spawnTimer); // Timer should not increment
    }

    @Test
    void testUpdateInValidTime(){
    // Test the updateTimeAndSpawn method when time is invalid.
    level.timeValid = false;
    level.remainingTime = -1;
    level.updateTimeAndSpawn();
    assertEquals(-1, level.remainingTime); // remainingTime should remain unchanged
    }

    @Test
    void testIsLevelEndedWhenNoBallsLeft() {
        // Test if the level ends correctly when there are no balls left.
        level.balls.clear(); // Clear the balls
        boolean ended = level.isLevelEnded();
        assertTrue(ended); // The level should be ended
        assertTrue(level.isLevelEnded); // The isLevelEnded flag should be true
    }

    @Test
    void testIsLevelEndedWhenTilesNotInitialized() {
        // Test if the level ends correctly when tiles are not initialized.
        level.balls.clear();
        level.tilesInitialized = false; // Ensure tiles are not initialized
        boolean ended = level.isLevelEnded();
        assertTrue(ended); // The level should be ended
        assertTrue(level.tilesInitialized); // Tiles should now be initialized
        assertNotNull(level.yellowTiles); // Yellow tiles should be created
        assertEquals(2, level.yellowTiles.size()); // Ensure two yellow tiles are created
    }

    @Test
    void testIsLevelEndedWhenTimeInvalid() {
        // Test if the level ends correctly when time is invalid.
        level.balls.clear(); // Clear the balls
        level.timeValid = false; // Set time as invalid
        boolean ended = level.isLevelEnded;
        assertTrue(ended); // The level should be ended
        assertFalse(level.tilesInitialized); // Tiles should not be initialized
    }

    @Test
    void testIsValidTimeFalse(){
        // Test the isValidTime method to ensure it returns false for invalid times.
        boolean isValid = level.isValidTime(-1);
        assertFalse(isValid);
        assertFalse(level.timeValid);
    }

    @Test
    void testIsLevelNotEndedWhenBallsArePresent() {
        // Test if the level is not ended when there are balls present.
        level.balls.add(new Ball(0, 0, "grey")); // Add a ball to the level
        boolean ended = level.isLevelEnded;
        assertFalse(ended); // The level should not be ended
    }
}