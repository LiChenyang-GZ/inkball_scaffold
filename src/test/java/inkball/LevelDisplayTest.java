package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

class LevelDisplayTest {
    App app;
    LevelDisplay levelDisplay;
    String layoutFilePath = "level.txt";

    @BeforeEach
    void setUp() throws IOException {
        app = new App();
        levelDisplay = new LevelDisplay(app, layoutFilePath);
    }

    @Test
    void testLoadLevel() {
        // Test loading the level from the layout file.
        levelDisplay.loadLevel();
        Tile[][] board = levelDisplay.getBoard();
        
        assertNotNull(board);
        assertEquals(18, board.length);
        assertEquals(App.BOARD_WIDTH, board[0].length);
    }

    @Test
    void testGetSpawnTile() {
        // Test retrieval of spawn tiles from the level display.
        List<Tile> spawnTiles = levelDisplay.getSpawnTile();
        assertNotNull(spawnTiles);
        assertEquals(1, spawnTiles.size());
        assertEquals("S", spawnTiles.get(0).getType());
    }

    @Test
    void testGetBallTile() {
        // Test retrieval of ball tiles from the level display.
        List<Tile> ballTiles = levelDisplay.getBallTile();
        assertNotNull(ballTiles);
        assertEquals(3, ballTiles.size()); // B2
        assertEquals("B2", ballTiles.get(0).getType());
    }

    @Test
    void testCreateTile() {
        // Test the tile creation method.
        levelDisplay.loadLevel();
        Tile tile = levelDisplay.createTile("X", 0, 0);
        assertNotNull(tile);
        assertEquals("X", tile.getType());
        assertEquals(0, tile.getX());
        assertEquals(0, tile.getY());
    }

    @Test
    void testGetBoard() {
        // Test retrieval of the board from the level display.
        Tile[][] board = levelDisplay.getBoard();
        assertNotNull(board);
        assertEquals(18, board.length);
        assertEquals(18, board[0].length);
    }
}