package inkball;

import processing.data.JSONArray;
import processing.data.JSONObject;
import java.util.*;

/**
 * The GameConfig class handles the configuration settings for the game.
 * It loads level data and score modifiers from a specified configuration file.
 */
public class GameConfig{
    private String filepath;
    private JSONArray levels;
    private HashMap<String, Integer> scoreIncreaseFromHoleCapture;
    private HashMap<String, Integer> scoreDecreaseFromWrongHole;

    /**
     * Constructor for the GameConfig class.
     * Initializes the file path and the score modifiers.
     *
     * @param filepath The path to the configuration file.
     */
    public GameConfig(String filepath){
        this.filepath = filepath;
        scoreIncreaseFromHoleCapture = new HashMap<>();
        scoreDecreaseFromWrongHole = new HashMap<>();
    }

    /**
     * Reads the configuration from the specified file.
     * Loads level data and score modifiers into the class variables.
     *
     * @param app The main application instance used for loading the JSON.
     * @return The complete JSON configuration object.
     */
    public JSONObject readConfig(App app){
        JSONObject config = app.loadJSONObject(filepath); // Load the JSON configuration

        // read the level config
        levels = config.getJSONArray("levels");
        // System.out.println(levels);
        // read score increase config
        JSONObject scoreIncrease = config.getJSONObject("score_increase_from_hole_capture");
        JSONObject scoreDecrease = config.getJSONObject("score_decrease_from_wrong_hole");

        // Populate the score modifiers
        for (Object key : scoreIncrease.keys()) {
            scoreIncreaseFromHoleCapture.put((String) key, scoreIncrease.getInt((String) key));
        }

        for (Object key : scoreDecrease.keys()) {
            scoreDecreaseFromWrongHole.put((String) key, scoreDecrease.getInt((String) key));
        }

        return config;
    }

    // /**
    //  * Gets the count of levels defined in the configuration.
    //  *
    //  * @return The number of levels.
    //  */
    // public int getLevelCnt(){
    //     return levels.size();
    // }

    /**
     * Gets the layout file name for a specific level.
     *
     * @param levelNumber The index of the level.
     * @return The layout file name as a string.
     */
    public String getLevelLayout(int levelNumber){
        return levels.getJSONObject(levelNumber).getString("layout");
    }

    /**
     * Gets the configuration for balls in a specific level.
     *
     * @param levelNumber The index of the level.
     * @return A list of ball configurations as strings.
     */
    public List<String> getBallsConfig(int levelNumber){
        JSONArray ballsArray = levels.getJSONObject(levelNumber).getJSONArray("balls");
        List<String> ballsConfig = new ArrayList<>();
        for (int i = 0; i < ballsArray.size(); i++){
            ballsConfig.add(ballsArray.getString(i));
        }
        return ballsConfig;
    }

    /**
     * Gets the spawn interval for balls in a specific level.
     *
     * @param levelNumber The index of the level.
     * @return The spawn interval in milliseconds.
     */
    public int getSpawnInterval(int levelNumber){
        return levels.getJSONObject(levelNumber).getInt("spawn_interval");
    }

    /**
     * Gets the time limit for a specific level.
     *
     * @param levelNumber The index of the level.
     * @return The time limit in seconds, or -1 if not defined.
     */
    public int getTime(int levelNumber){
        JSONObject levelConfig = levels.getJSONObject(levelNumber);
        if (!levelConfig.isNull("time")){  // Check if time is defined
            float time = levelConfig.getInt("time");
            if (time >= 0 && time == Math.floor(time)) { // Check if the time is integer
                return (int) time;
            } else {
                // System.out.println("Invalid time format for level " + levelNumber);
            }
        }
        return -1;  // Return -1 if time is not defined or invalid
    }

    /**
     * Gets the score increase modifier for capturing holes in a specific level.
     *
     * @param levelNumber The index of the level.
     * @return The score increase modifier as a float.
     */
    public float getScoreIncreaseModifier(int levelNumber) {
        return levels.getJSONObject(levelNumber).getFloat("score_increase_from_hole_capture_modifier");
    }

    /**
     * Gets the score decrease modifier for wrong captures in a specific level.
     *
     * @param levelNumber The index of the level.
     * @return The score decrease modifier as a float.
     */
    public float getScoreDecreaseModifier(int levelNumber) {
        return levels.getJSONObject(levelNumber).getFloat("score_decrease_from_wrong_hole_modifier");
    }

    /**
     * Gets the score increase modifiers from hole captures.
     *
     * @return A map containing score increase modifiers.
     */
    public Map<String, Integer> getScoreIncreaseFromCapture() {
        return scoreIncreaseFromHoleCapture;
    }

    /**
     * Gets the score decrease modifiers from wrong captures.
     *
     * @return A map containing score decrease modifiers.
     */
    public Map<String, Integer> getScoreDecreaseFromWrongCapture() {
        return scoreDecreaseFromWrongHole;
    }

}