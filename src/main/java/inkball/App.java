package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

/**
 * The main application class for the Inkball game, extending PApplet from Processing.
 * This class handles game initialization, configuration loading, user input,
 * and rendering of game elements.
 */
public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    // public static Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.
    public GameConfig configObject;  // Game configuration object
    private HashMap<String, PImage> sprites = new HashMap<>();  // store the image address

    protected List<Level> levels = new ArrayList<>();  // List of game levels
    int currentLevelIndex = 0; // get the  current level
    Level currentLevel; // Current level object

    protected float score=0; // get the current total score
    protected boolean wholeGameOver = false; // Show whether the game totally finished 
    protected float wholeScore; // record the whole score

    /**
     * Constructs a new App instance and initializes the configuration path.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Get an image by its name, loads the image from the specified path. 
     * @param s The name of the image.
     * @return The loaded PImage object.
     */
    public PImage getImage(String s){
        PImage result = sprites.get(s);
        if (result == null){
            result = loadImage(this.getClass().getResource(s +".png").getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
            sprites.put(s, result);
        }
        return result;
    }

    /**
     * Loads all sprite images used in the game.
     */
    public void loadSprites(){
        String[] sprites = new String[] {
            "entrypoint",
            "tile"
        };
        for (int i = 0; i < sprites.length; i++){
            getImage(sprites[i]);
        }
        for (int i = 0; i < 5; i++){
            getImage("ball"+String.valueOf(i));
            getImage("hole"+String.valueOf(i));
            getImage("wall"+String.valueOf(i));
            getImage("cracked"+String.valueOf(i));
        }
    }

    /**
     * Converts a color code to its corresponding string representation.
     * @param code The color code.
     * @return The color string corresponding to the code.
     */
    public String getColorFromCode(String code) {
        switch (code) {
            case "0":
                return "grey";
            case "1":
                return "orange";
            case "2":
                return "blue";
            case "3":
                return "green";
            case "4":
                return "yellow";
            default:
                return "unknown"; // Return "unknown" for invalid codes.
        }
    }

    /**
     * Converts a color string to its corresponding code.
     * @param color The color string.
     * @return The corresponding color code.
     */
    public String getCodeFromColor(String color) {
        switch (color) {
            case "grey":
                return "0";
            case "orange":
                return "1";
            case "blue":
                return "2";
            case "green":
                return "3";
            case "yellow":
                return "4";
            default:
                return "unknown"; // Return "unknown" for invalid colors.
        }
    }

    /**
     * Retrieves the image corresponding to a ball color code.
     * @param code The color code of the ball.
     * @return The PImage object of the ball, or null if unknown.
     */
    public PImage getBallImage(String code) {
        String color = getCodeFromColor(code);
        PImage image = null;

        if (!color.equals("unknown")){
            image = getImage("ball"+color);
        } else {
            // System.out.println("unknown ball code: " + code);
        }
        return image;
    }

    /**
     * Loads all levels of the game from the configuration.
     */
    protected void loadLevels() {
        // System.out.println("total level size is: " + totalLevels);
        // System.out.println("load levels...");
        for (int i = 0; i < 3; i++) {
            
            Level level = new Level(i, configObject);
            // System.out.println("load level finished: " + i);
            levels.add(level);
            
        }
        // System.out.println("APP load levels finished...");
    }

    /**
     * Get the level at the specified index and sets it as the current level.
     * @param index The index of the level to retrieve.
     */
    protected void getLevel(int index) {
        if (index >= 0 && index < levels.size()) {
            currentLevel = levels.get(index);
            // currentLevel = new Level(levels.get(index), )
            currentLevel.setupLevel(this);
        } else {
            // System.out.println("Invalid level index: " + index);
        }
    }

    /**
     * Retrieves the current score of the level.
     * @return The current score.
     */
    public float getLevelScore() {
        return currentLevel.getCurrentScore();
    }
    
    /**
     * Adds the specified score to the current score.
     * @param scoreAdded The score to be added.
     */
    public void addScore(float scoreAdded){
        score += scoreAdded;
    }

    /**
     * Draws the current score on the screen.
     */
    public void drawScore(){
        fill(0);
        textSize(20);
        text("Score: " + (int) this.score, WIDTH-130, 30);
    }

    /**
     * Draws a message indicating that the game has ended.
     */
    public void drawEnded(){
        fill(0);
        textSize(25);
        text("***Ended***", WIDTH/2-15, TOPBAR/2+10);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        // System.out.println("Setting up the application...");
        frameRate(FPS);
        // read config file
        configObject = new GameConfig(configPath);
        configObject.readConfig(this);

        // load images
        loadSprites();
        loadLevels(); // Load all levels
        currentLevel = levels.get(currentLevelIndex);
        // currentLevel = new Level(currentLevelIndex, configObject);
        currentLevel.setupLevel(this);
    }

    /**
     * Receive key pressed signal from the keyboard.
     * @param event The KeyEvent object containing information about the key press.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if (key==' '){
            currentLevel.pauseTheGame();
        } else if (key == 'r'){
            if (wholeGameOver){
                wholeGameOver = false;
            } else {
                score -= currentLevel.getCurrentScore();
            }
            currentLevel = new Level(currentLevelIndex, configObject);
            currentLevel.setupLevel(this);
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	// @Override
    // public void keyReleased(){
        
    // }

    /**
     * Handles mouse press events to interact with the game.
     * @param e The MouseEvent object containing information about the mouse press.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
        int mouseX = e.getX();
        int mouseY = e.getY();
        if (mouseX>=0 && mouseX<WIDTH && mouseY>=0 & mouseY<HEIGHT){
            if (e.getButton() == LEFT){ // left mouse
                currentLevel.startNewLine(mouseX, mouseY);
            } else if (e.getButton() == RIGHT) { // remove player-drawn line object if right mouse button is held 
                currentLevel.deleteLine(mouseX, mouseY);
            }
        }
    }
	
    /**
     * Handles mouse drag events to add points to the drawn line.
     * @param e The MouseEvent object containing information about the mouse drag.
     */
	@Override
    public void mouseDragged(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        if (e.getButton() == LEFT){
            currentLevel.addNewLinePoint(mouseX, mouseY);
        }
    }

    /**
     * Handles mouse release events to finalize the drawn line.
     * @param e The MouseEvent object containing information about the mouse release.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        currentLevel.addNewLine();
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        background(200);
        if (currentLevel.isLevelEnded()){ // if current level ended, start to add score
            currentLevel.LevelFinishedAddScore(this);
            // System.out.println("the level has ended...");
            if (currentLevel.isLevelFinished()){ // If score addition has ended
                // System.out.println("Current Level index is: "+currentLevelIndex);
                if (currentLevelIndex < levels.size()-1){ 
                    // System.out.println("the level size is: " + (levels.size()-1));
                    currentLevelIndex++; 
                    // System.out.println("Current Level index is: "+currentLevelIndex);
                    // System.out.println("Change to next level...");
                    getLevel(currentLevelIndex); // change to next level
                } else { 
                    // System.out.println("Current Level index is: "+ currentLevelIndex);
                    // System.out.println("the game ended...");
                    wholeScore = this.score;
                    wholeGameOver = true; // End the game
                }
            }
        } // else, the entity will stop, player should press r to restart the game
        
        currentLevel.draw(this); // Draw the current level.

        if (wholeGameOver){
            score = wholeScore; // Reset score to final score if game is over.
        }
        drawScore(); //// Draw the score.

        if (wholeGameOver){
            currentLevelIndex = 0; // Reset level index.
            score = 0; // Reset score.
            drawEnded(); // Draw end game message.
        }

        //----------------------------------
        //display Board for current level:
        //----------------------------------
        //TODO

        //----------------------------------
        //display score
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------
		//display game end message

    }

    /**
     * The main method to launch the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
