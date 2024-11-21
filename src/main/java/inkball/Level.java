package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.*;
import java.io.*;

/**
 * The Level class represents a game level in the Inkball game.
 * It manages the layout, score, time, balls, lines, and interactions
 * within the level.
 */
public class Level {
    // Basic elements
    private String layout; // The layout of the level.
    protected float scoreIncreaseModifier;  // Modifier for score increase.
    protected float scoreDecreaseModifier;  // Modifier for score decrease.
    protected Tile[][] board;  // The game board.
    private LinkedList<String> ballsConfig;  // Configuration for the balls.
    protected BallManager ballManager;  // Manages the remaining balls in the level.
    protected List<Ball> balls = new ArrayList<>(); // Stores the current balls on the board.
    private LevelDisplay levelDisplay;  // Displays the initial board.
    protected List<Line> lines = new ArrayList<>(); // Stores the drawn lines.
    protected Line currentLine = null;  // The current line being drawn.

    // Time Variables
    protected int totalTime; // Total time for the level.
    protected float remainingTime; // Remaining time for the level.
    private int spawnInterval;  // Interval for spawning new balls.
    protected int spawnTimer = 0;  // Timer for spawning new balls.
    protected float remainingSpawnTime;  // Remaining time for the next spawn.
    private boolean isTimeInitialized = false;  // Indicates if time is initialized.
    protected boolean pause = false;  // Indicates if the level is paused.
    protected boolean timeUp = false;  // Indicates if the level is time up.
    protected boolean timeValid; // Indicates if the time from the config is valid.

    // Score Variables
    protected int currentScore = 0; // Current score for the level.
    protected Map<String, Integer> scoreIncreaseConfig; // Configuration for score increases.
    protected Map<String, Integer> scoreDecreaseConfig; // Configuration for score decrease

    // Level finished actions elements
    protected Tile yellowTile1;  // First yellow tile to rotate.
    protected Tile yellowTile2;  // Second yellow tile to rotate.
    protected List<Tile> yellowTiles;  // List of yellow tiles.
    protected boolean tilesInitialized = false; // Indicates if yellow tiles are initialized.
    protected boolean isLevelFinished = false; // Indicates if all the level's actions are finished.
    protected boolean isLevelEnded = false;  // Indicates if the level has ended.
    private int frameCounter = 0; // Frame counter for managing updates.

    // Control variables for remaining balls movement
    private List<Float> remainingBallXPositions = new ArrayList<>(); // X positions for remaining balls.
    private boolean startMovingRemainingBalls = false; // Indicates if remaining balls should move.
    private boolean ballShiftStarted = false; // Indicates if ball shifting has started.
    private float moveOffset = 0; // Offset for moving balls.

    /**
     * Constructs a Level instance with the specified level index and configuration.
     *
     * @param level The index of the level.
     * @param configObject The GameConfig object containing configuration data.
     */
    public Level(int level, GameConfig configObject){
        this.layout = configObject.getLevelLayout(level);
        this.spawnInterval = configObject.getSpawnInterval(level);
        this.scoreIncreaseModifier = configObject.getScoreIncreaseModifier(level);
        this.scoreDecreaseModifier = configObject.getScoreDecreaseModifier(level);
        this.ballsConfig = new LinkedList<>(configObject.getBallsConfig(level));
        this.scoreIncreaseConfig = configObject.getScoreIncreaseFromCapture();
        this.scoreDecreaseConfig = configObject.getScoreDecreaseFromWrongCapture();

        int time = configObject.getTime(level);
        if (isValidTime(time)){
            this.totalTime = time;
        } else{
            this.totalTime = -1; // Invalid time set to -1.
        }        
    }

    /**
     * Initializes the level by loading its display and setting up game elements.
     *
     * @param app The main application instance.
     */
    public void setupLevel(App app){
        // load level basic elements
        this.levelDisplay = new LevelDisplay(app, layout);
        // System.out.println("load level...");
        levelDisplay.loadLevel();
        board = levelDisplay.getBoard();
        // System.out.println("display level...");
        this.ballManager = new BallManager(this.ballsConfig);        

        // Initialize balls based on the level configuration.
        List<Tile> ballTiles = levelDisplay.getBallTile();
        for (Tile tile: ballTiles){
            // System.out.println("the tile is in (" + tile.getX() + ", "+tile.getY());
            String tileType = tile.getType();
            int colorIndex;
            try {
                colorIndex = Integer.parseInt(tileType.substring(1));
                if (colorIndex < ballManager.size()){
                    String ballColor = ballManager.getColor(colorIndex);
                    if (ballColor != null){
                        balls.add(new Ball(tile.getX()*app.CELLSIZE, tile.getY()*app.CELLSIZE+app.TOPBAR, ballColor));
                        ballManager.removeColorAt(colorIndex);
                    }
                } else{
                    // System.out.println("Color index out of bounds for tile: " + tileType);
                }
            } catch (NumberFormatException e){
                // System.out.println("Invalid color inex in tile type: " + tileType);
            }
        }

        // Initialize total time
        // System.out.println("initialize time...");
        // System.out.println("total time is: " + totalTime);
        if (!isTimeInitialized){
            remainingTime = totalTime;
            // System.out.println("finish time initialized...");
            isTimeInitialized = true;
        }
        // Initialize the spawn time
        if (ballManager.size() > 0){
            remainingSpawnTime = spawnInterval;
        }
        
    }

    /**
     * Draws the lines on the level.
     *
     * @param app App to draw to.
     */
    protected void drawLines(App app) {
        if (timeUp || isLevelEnded){
            return;
        }
        for (Line line : lines) {
            line.draw(app);
            // draw the line which the player is drawing
            if (currentLine != null) {
                currentLine.draw(app);
            }
        }
    }

    /**
     * Draws the balls on the level and updates their positions.
     *
     * @param app App to draw to.
     */
    public void drawBalls(App app){
        List<Ball> ballsToRemove = new ArrayList<>();

        for (Ball ball : balls) {
            // Update ball position if not paused or time is up
            if (!pause && !timeUp){
                ball.updatePosition();
            }
            
            // Draw the ball if it is not be removed
            if (!ball.isRemove()){
                ball.draw(app);
            }

            // Check for captured by holes, and add scores
            for (Tile[] row : board){
                for (Tile tile : row){
                    if (tile != null && (tile.isHole())){
                        String holeType = tile.attractBall(ball);
                        if ((ball.isCaptured()) && (holeType != null) && (holeType.length() > 1) && (!ball.isRemove())){
                            // System.out.println("the remaining time is: " + remainingTime);
                            // System.out.println("the ball" + ball.getColor() + " has been captured by: " + holeType);
                            String holeColorChar = String.valueOf(holeType.charAt(1));
                            String holeColor = app.getColorFromCode(holeColorChar);
                            // System.out.println("calculate the score...");
                            float scoreChange = calScoreChange(holeColor, ball);
                            currentScore += scoreChange;
                            addScoreToAPP(app, scoreChange); // add the current score to App
                            ballsToRemove.add(ball);
                            break;
                            
                        }
                        
                    }
                }
            }

            // Check for collision with tiles
            for (Tile[] row : board) {
                for (Tile tile : row) {
                    if (tile != null) {
                        tile.hitTile(ball, app);  // Check the tiles would be damaged
                        tile.checkCollision(ball); // Check for collision with tiles
                        // System.out.println("check collision with ball finished...");
                    }
                }
            }

            // Check for collision with lines
            for (Line line : lines) {
                if (line.isLineCollision(ball)) {
                    lines.remove(line); // Remove the colliding line
                    // System.out.println("the line was remove by the ball" + ball.getColor() + " at remaining time: " + remainingTime);
                    break; // only remove one line in one time
                }
            }

        }
        // Remove captured balls from the main list
        for (Ball ball : ballsToRemove) {
            balls.remove(ball);
        }
    }

    /**
     * Draws the remaining balls that have not yet been spawned.
     *
     * @param app App to draw to.
     */
    private void drawRemainingBalls(App app) {
        // app.fill(0);
        app.rect(10, 10, 160, 30);
        List<String> remainingBalls = ballManager.getRemainingBallColors();
        // System.out.println("the remaining balls are: " + remainingBalls);
        int ballCount = Math.min(remainingBalls.size(), 5);

        if (!ballShiftStarted) {
            remainingBallXPositions.clear(); // Clear previous positions
            for (int i = 0; i < remainingBalls.size(); i++) {
                remainingBallXPositions.add(10 + (i * 30.0f)); // Calculate initial positions
            }
        }

        // Start moving balls only after spawning new ones
        if (startMovingRemainingBalls) {
            moveOffset += 1.0f;  // Increment move offset
            
            for (int i = 0; i < ballCount; i++) {
                String ballColor = remainingBalls.get(i);
                PImage ballImage = app.getBallImage(ballColor);

                // new position = initial position - moveOffset
                float newXPosition = remainingBallXPositions.get(i)+30.0f - moveOffset;
                // System.out.println("move the ball " + ballColor);
                // System.out.println("the new position of the ball " + ballColor + " is: " + newXPosition);

                // Stop moving if the first ball reaches the left edge
                if (newXPosition <= 10 && i == 0) {
                    // System.out.println("stop moving the ball");
                    startMovingRemainingBalls = false;  // Stop overall movement
                    moveOffset = 0;  // Reset move variable
                }
                // Draw the ball
                if (ballImage != null) {
                    app.image(ballImage, newXPosition, 10, 30, 30);
                }
            }
        } else {
            // Display balls without movement
            for (int i = 0; i < ballCount; i++) {
                String ballColor = remainingBalls.get(i);
                PImage ballImage = app.getBallImage(ballColor);
                if (ballImage != null) {
                    app.image(ballImage, remainingBallXPositions.get(i), 10, 30, 30);
                }
            }
        }
    }

    /**
     * Draws the current score and remaining time on the screen.
     *
     * @param app App to draw to.
     */
    protected void drawScoreAndTime(App app) {
        app.fill(0);
        app.textSize(20);
        String timeText = "Time: " + Math.max(0, (int)remainingTime);
        app.text(timeText, app.WIDTH - 130, 50);
        if (ballManager.size() > 0){ // only show the remaining spawn time if it have remaining balls
            String countDownText = String.format("%.1f", Math.max(0, remainingSpawnTime));
            app.text(countDownText, 200, 30);
        }
        // Display paused message if the game is paused
        if ((pause && remainingTime > 0) || (pause && !timeValid)){
            app.text("***PAUSED***", app.WIDTH/2-15, app.TOPBAR/2+10);
        } 
        // Display time's up message if time runs out
        if (remainingTime <= 0 && !isLevelEnded && timeValid){
            app.text("=== TIME'S UP ===", app.WIDTH/2-50, app.TOPBAR/2+10);
            this.timeUp = true;
        }
    }

    /**
     * Updates the time and spawns new balls as needed.
     */
    public void updateTimeAndSpawn() {
        if (pause || timeUp){
            return;  // Do not update if paused or time is up
        }

        // Update remaining spawn time
        if (remainingSpawnTime > 0) {
            remainingSpawnTime -= (1.0 / App.FPS);
        }

        // Check if it's time to spawn a new ball
        spawnTimer++;
        if (spawnTimer >= App.FPS * spawnInterval) {
            spawnNewBall();
            spawnTimer = 0;
        }

        // Update remaining time if time valid
        if (!timeValid){
            return;
        } else if (timeValid && remainingTime >= 0){
            remainingTime -= (1.0 / App.FPS);
        }
    }

    /**
     * Draws the tiles on the board.
     * This method iterates through the board and calls the draw method on each tile.
     * If the level has ended, it also draws the yellow tiles.
     *
     * @param app App to draw to.
     */
    protected void drawTiles(App app){
        for (Tile[] row : board){
            for (Tile tile : row){
                if (tile != null)
                    tile.draw(app);
            }
        }
        // If the level has ended, draw the yellow tiles
        if (isLevelEnded){
            for (Tile tile : yellowTiles){
                tile.draw(app);
            }
        }
        
    }

    /**
     * Main draw method for the level.
     * This method sets the background and calls the methods to draw tiles, balls, remaining balls,
     * lines, updates time and spawns new balls, and draws the score and time.
     *
     * @param app App to draw to..
     */
    public void draw(App app){
        app.background(200);
        drawTiles(app);
        drawBalls(app);
        drawRemainingBalls(app);
        drawLines(app);
        updateTimeAndSpawn();
        drawScoreAndTime(app);
    }


    // public String getLayout() {
    //     return layout;
    // }

    // public int getSpawnInterval() {
    //     return spawnInterval;
    // }

    /**
     * Gets the current score of the level.
     *
     * @return The current score.
     */
    public int getCurrentScore(){
        return this.currentScore;
    }

    // public int getLevelScore(){
    //     if (isLevelEnded){
    //         return this.currentScore;
    //     } else {
    //         return 0;
    //     }
    // }

    // /**
    //  * Gets the modifier for score decreases.
    //  *
    //  * @return The score decrease modifier.
    //  */
    // public float getScoreIncreaseModifier() {
    //     return scoreIncreaseModifier;
    // }

    // public float getScoreDecreaseModifier() {
    //     return scoreDecreaseModifier;
    // }

    /**
     * Starts a new line at the specified coordinates.
     *
     * @param x The x-coordinate for the starting point of the line.
     * @param y The y-coordinate for the starting point of the line.
     */
    public void startNewLine(int x, int y) {
        currentLine = new Line();
        currentLine.addPoint(x, y);
        lines.add(currentLine);  // Add the line to the list of lines
    }

    /**
     * Deletes the line that the player choose.
     *
     * @param x The x-coordinate of the point to check.
     * @param y The y-coordinate of the point to check.
     */
    public void deleteLine(int x, int y){
        currentLine = null;
        for (int i = lines.size() - 1; i >= 0; i--){
            Line line = lines.get(i);
            if (line.containsPoint(x, y)){ // Check if the line contains the point
                lines.remove(i); //Remove the line if it does
                break;
            }
        }
    }

    /**
     * Adds a new point to the current line if it exists.
     *
     * @param x The x-coordinate of the point to add.
     * @param y The y-coordinate of the point to add.
     */
    public void addNewLinePoint(int x, int y) {
        if (currentLine != null) {
            currentLine.addPoint(x, y);  // Add the point to the current line
        }

    }

    /**
     * Resets the current line to null, indicating that a new line can be started.
     */
    public void addNewLine(){
        currentLine = null;
    }

    /**
     * Spawns a new ball if available in the BallManager.
     * Selects a random spawn tile from valid options and adds the new ball to the game.
     */
    public void spawnNewBall(){
        if (ballManager.size() > 0){
            String newBallColor = ballManager.getColor(0); // Get the color of the new ball
            List<Tile> spawnTiles = levelDisplay.getSpawnTile(); // Get valid spawn tiles

            if (!spawnTiles.isEmpty()){
                Tile spawnTile = spawnTiles.get((int) (Math.random()*spawnTiles.size()));
                balls.add(new Ball(spawnTile.getX()*App.CELLSIZE, spawnTile.getY()*App.CELLSIZE+App.TOPBAR, newBallColor));
                ballManager.removeColorAt(0);
                remainingSpawnTime = spawnInterval; // update remaining spawn time
                startMovingRemainingBalls = true; // start to move the remaining ball
            } else{
                // System.out.println("No valid spawn tile available...");
            }
        } else {
            // System.out.println("No upcoming balls to spawn.");
        }
    }

    /**
     * Checks if the capture of the ball was successful based on color matching.
     *
     * @param holeColor The color of the hole.
     * @param ball The ball to check.
     * @return True if the capture was successful, false otherwise.
     */
    public boolean isCaptureSuccessful(String holeColor, Ball ball) {
        String ballColor = ball.getColor();
        if (ballColor.equals(holeColor) || ballColor.equals("grey") || holeColor.equals("grey")){
            ball.removeBall(); // remove the ball
            // System.out.println("remove the ball");
            // System.out.println("Captured ball successfully!!!");
            return true;
        }
        // System.out.println("unsuccessfully capture ball...");
        ball.capturedBall(false); // Mark the ball as not captured successfully
        ball.removeBall();
        return false;
    }

    /**
     * Calculates the score change based on the hole color and ball state.
     *
     * @param holeColor The color of the hole.
     * @param ball The ball being evaluated.
     * @return The score change resulting from this interaction.
     */
    public float calScoreChange(String holeColor, Ball ball){
        if (!ball.isCaptured()){
            return 0;  // Return 0 if the ball is not captured
        }
        String ballColor = ball.getColor();
        float scoreChange = 0;
        if (isCaptureSuccessful(holeColor, ball)){
            float scoreOfHole = scoreIncreaseConfig.getOrDefault(holeColor, 0); // Get score for the hole color
            scoreChange = scoreOfHole * scoreIncreaseModifier; // Calculate score increase
            // System.out.println("the score add: " + scoreChange);
        } else {
            ballManager.addColor(ballColor); // Return the ball to the manager
            ball.attractBall(false);
            // System.out.println("add ball to ball manager");
            float scoreOfHole = scoreDecreaseConfig.getOrDefault(holeColor, 0); // Get score decrease for the hole color
            scoreChange = -scoreOfHole * scoreDecreaseModifier; // Calculate score decrease
            // System.out.println("the score decrease: " + scoreChange);
        }
        return scoreChange;
    } 

    /**
     * Adds the specified score to the application.
     *
     * @param app The main application instance.
     * @param scoreAdded The score to add.
     */
    public void addScoreToAPP(App app, float scoreAdded){
        app.addScore(scoreAdded);  // Call the app's method to add the score
    }

    /**
     * Toggles the pause state of the game.
     */
    public void pauseTheGame(){
        this.pause = !this.pause;
        // System.out.println("pause the game...");
    }

    // public int ballSize(){
    //     return ballManager.size() + balls.size();
    // }


    /**
     * Checks if the level has ended due to no remaining balls.
     *
     * @return True if the level has ended, false otherwise.
     */
    public boolean isLevelEnded(){
        if (((ballManager.size() + balls.size())==0)){  // If no balls are left
            // System.out.println("level ended!!");
            if (!tilesInitialized){
                yellowTile1 = new Tile(0, 0, "roatated");
                yellowTile2 = new Tile(App.BOARD_WIDTH - 1, 17, "roatated");
                yellowTiles = Arrays.asList(yellowTile1, yellowTile2);
                tilesInitialized = true;  // Mark tiles as initialized
                this.isLevelEnded = true;  // Set level as ended
            } else if (!timeValid){ // no need to rotate the yello tiles
                // System.out.println("the time is invalid...");
                this.isLevelEnded = true;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the level is finished.
     *
     * @return True if the level is finished, false otherwise.
     */
    public boolean isLevelFinished(){
        return this.isLevelFinished;
    }

    /**
     * Moves the yellow tiles based on their current positions.
     *
     * @param app The main application instance.
     */
    public void moveYellowTiles(App app){
        for (Tile tile : yellowTiles){
            tile.setOldPosition();
            int x = tile.getX();
            int y = tile.getY();
            // System.out.println("the old position is: " + x + ", " + y);
            // Determine new position based on current position
            if (x == App.BOARD_WIDTH - 1) { // right side of the board
                if (y < App.BOARD_HEIGHT-3){
                    y++; // move down
                } else {
                    x--; // move left
                }
            } else if (x == 0) { // left side of the board
                if (y > 0){
                    y--; // move up
                } else {
                    x++; // move right
                }
            } else if (y == App.BOARD_HEIGHT - 3){ // bottom of the board
                x--; // move left
            } else if (y == 0){
                x++; // move right
            }
            tile.setYellowPosition(x, y, app); // Update the yellow tile's position
        }
    }

    /**
     * Adds score based on remaining time when the level is finished.
     * It increments the score every two frames while the level is finished.
     *
     * @param app The main application instance.
     */
    public void LevelFinishedAddScore(App app){
        if (isLevelEnded()){
            frameCounter++;
            if (remainingTime > 0){
                // System.out.println("the remaining time is: " + remainingTime);
                if (frameCounter % 2 == 0){ // add 1 score every 0.067s (every 2 frames)
                    app.addScore(1);
                    remainingTime -= 1;
                    moveYellowTiles(app);
                }
                
                if (remainingTime <= 0){ // Mark level as finished if remaining time runs out
                    this.isLevelFinished = true;
                }
            } else if (remainingTime == -1){ // invalid time, no need to do anything, just finished the level
                this.isLevelFinished = true;
            } else{
                this.isLevelFinished = false;
            }   
        }
    }

    /**
     * Check if the time is valid.
     *
     * @param time The time to validate.
     * @return True if the time is valid, false otherwise.
     */
    protected boolean isValidTime(int time) {
        if (time >= 0){
            this.timeValid = true;
            return true;
        } else{
            this.timeValid = false;
            return false;
        }
    }

}