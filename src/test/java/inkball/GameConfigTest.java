package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.data.JSONArray;
import processing.data.JSONObject;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

public class GameConfigTest {
    private GameConfig gameConfig;

    @BeforeEach
    public void setUp() {
        JSONObject mockConfig = createMockConfig();
        gameConfig = new GameConfig("mock_path");
        gameConfig.readConfig(new MockApp(mockConfig));
    }

    private JSONObject createMockConfig() {
        // Create a JSONObject and manually populate it with mock data.
        JSONObject config = new JSONObject();
        
        JSONArray levels = new JSONArray();
        JSONObject level1 = new JSONObject();
        level1.setString("layout", "level1_layout");
        level1.setJSONArray("balls", new JSONArray().setString(0, "red").setString(1, "blue"));
        level1.setInt("spawn_interval", 2000);
        level1.setInt("time", 60);
        level1.setFloat("score_increase_from_hole_capture_modifier", 1.5f);
        level1.setFloat("score_decrease_from_wrong_hole_modifier", 0.5f);
        levels.append(level1);

        JSONObject level2 = new JSONObject();
        level2.setString("layout", "level2_layout");
        level2.setJSONArray("balls", new JSONArray().setString(0, "green").setString(1, "yellow"));
        level2.setInt("spawn_interval", 1500);
        level2.setInt("time", -30);
        level2.setFloat("score_increase_from_hole_capture_modifier", 2.0f);
        level2.setFloat("score_decrease_from_wrong_hole_modifier", 1.0f);
        levels.append(level2);

        config.setJSONArray("levels", levels);
        
        JSONObject scoreIncrease = new JSONObject();
        scoreIncrease.setInt("red", 10);
        scoreIncrease.setInt("blue", 15);
        config.setJSONObject("score_increase_from_hole_capture", scoreIncrease);
        
        JSONObject scoreDecrease = new JSONObject();
        scoreDecrease.setInt("green", -5);
        scoreDecrease.setInt("yellow", -10);
        config.setJSONObject("score_decrease_from_wrong_hole", scoreDecrease);
        
        return config;
    }

    @Test
    public void testGetLevelLayout() {
        // Test retrieval of level layouts.
        assertEquals("level1_layout", gameConfig.getLevelLayout(0));
        assertEquals("level2_layout", gameConfig.getLevelLayout(1));
    }

    @Test
    public void testGetBallsConfig() {
        // Test retrieval of ball configurations for each level.
        assertEquals(2, gameConfig.getBallsConfig(0).size());
        assertTrue(gameConfig.getBallsConfig(0).contains("red"));
        assertTrue(gameConfig.getBallsConfig(0).contains("blue"));
        assertEquals(2, gameConfig.getBallsConfig(1).size());
        assertTrue(gameConfig.getBallsConfig(1).contains("green"));
        assertTrue(gameConfig.getBallsConfig(1).contains("yellow"));
    }

    @Test
    public void testGetSpawnInterval() {
        // Test retrieval of spawn intervals for each level.
        assertEquals(2000, gameConfig.getSpawnInterval(0));
        assertEquals(1500, gameConfig.getSpawnInterval(1));
    }

    @Test
    public void testGetTime() {
        // Test retrieval of time for each level.
        assertEquals(60, gameConfig.getTime(0));
        assertEquals(-1, gameConfig.getTime(1));
    }

    @Test
    public void testGetScoreIncreaseModifier() {
        // Test retrieval of score increase modifiers for each level.
        assertEquals(1.5, gameConfig.getScoreIncreaseModifier(0), 0.01);
        assertEquals(2.0, gameConfig.getScoreIncreaseModifier(1), 0.01);
    }

    @Test
    public void testGetScoreDecreaseModifier() {
        // Test retrieval of score decrease modifiers for each level.
        assertEquals(0.5, gameConfig.getScoreDecreaseModifier(0), 0.01);
        assertEquals(1.0, gameConfig.getScoreDecreaseModifier(1), 0.01);
    }

    @Test
    public void testGetScoreIncreaseFromCapture() {
        // Test retrieval of score increase values from hole captures.
        HashMap<String, Integer> scoreIncrease = new HashMap<>(gameConfig.getScoreIncreaseFromCapture());
        assertEquals(2, scoreIncrease.size());
        assertEquals(10, scoreIncrease.get("red"));
        assertEquals(15, scoreIncrease.get("blue"));
    }

    @Test
    public void testGetScoreDecreaseFromWrongCapture() {
        // Test retrieval of score decrease values from wrong captures.
        HashMap<String, Integer> scoreDecrease = new HashMap<>(gameConfig.getScoreDecreaseFromWrongCapture());
        assertEquals(2, scoreDecrease.size());
        assertEquals(-5, scoreDecrease.get("green"));
        assertEquals(-10, scoreDecrease.get("yellow"));
    }

    // MockApp class to simulate the App instance for testing
    private static class MockApp extends App {
        private final JSONObject mockConfig;

        public MockApp(JSONObject mockConfig) {
            this.mockConfig = mockConfig;
        }

        @Override
        public JSONObject loadJSONObject(String path) {
            return mockConfig;
        }
    }
}