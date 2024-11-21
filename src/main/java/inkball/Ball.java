package inkball;

import processing.core.PImage;
import processing.core.PVector;
import java.util.*;

/**
 * The Ball class represents a ball in the game with properties such as position, velocity, and color.
 * It handles movement, collision detection, and rendering of the ball.
 */
public class Ball {
    private static final Random random = new Random();
    private int radius = 12;
    private float currentRadius = radius; // Current radius, may change during shrinking
    // Velocity of the ball
    private float xVelocity;
    private float yVelocity;
    // Position of the ball
    private float x;
    private float y;
    // Attributes of the ball
    private String color;
    private boolean isRemove = false;
    private boolean isAttract = false;
    private boolean isShrinking = false;
    private boolean isCaptured = false;
    private float shrinkFactor = 0.5f;

    /**
     * Constructor for the Ball class.
     * Initializes the ball's position and color, and sets random velocities.
     *
     * @param x The initial x-coordinate of the ball.
     * @param y The initial y-coordinate of the ball.
     * @param color The color of the ball.
     */
    public Ball(float x, float y, String color){
        this.x = x;
        this.y = y;
        this.color = color;
        this.xVelocity = (random.nextInt(2) * 2 - 1) * 2;
        this.yVelocity = (random.nextInt(2) * 2 - 1) * 2;
    }

    /**
     * Gets the radius of the ball.
     *
     * @return The radius of the ball.
     */
    public int getRadius(){
        return this.radius;
    }

    /**
     * Sets the radius of the ball.
     *
     * @param newRadius The new radius to set for the ball.
     */
    public void setRadius(int newRadius){
        this.radius = newRadius;
    }

    /**
     * Gets the current radius of the ball.
     *
     * @return The current radius of the ball.
     */
    public float getCurrentRadius(){
        return this.currentRadius;
    }

    /**
     * Sets the current radius of the ball.
     *
     * @param newRadius The new current radius to set for the ball.
     */
    public void setCurrentRadius(float newRadius){
        this.currentRadius = newRadius;
    }

    /**
     * Gets the x-coordinate of the ball.
     *
     * @return The x-coordinate of the ball.
     */
    public float getX(){
        return this.x;
    }

    /**
     * Sets the x-coordinate of the ball.
     *
     * @param x The new x-coordinate to set for the ball.
     */
    public void setX(float x){
        this.x = x;
    }

    /**
     * Gets the y-coordinate of the ball.
     *
     * @return The y-coordinate of the ball.
     */
    public float getY(){
        return this.y;
    }

    /**
     * Sets the y-coordinate of the ball.
     *
     * @param y The new y-coordinate to set for the ball.
     */
    public void setY(float y){
        this.y = y;
    }

    /**
     * Gets the current x-velocity of the ball.
     *
     * @return The x-velocity of the ball.
     */
    public float getXVelocity(){
        return this.xVelocity;
    }

    /**
     * Gets the current y-velocity of the ball.
     *
     * @return The y-velocity of the ball.
     */
    public float getYVelocity(){
        return this.yVelocity;
    }

    /**
     * Reverses the x-velocity of the ball.
     */
    public void reverseXVelocity(){
        this.xVelocity *= -1;
    }

    /**
     * Reverses the y-velocity of the ball.
     */
    public void reverseYVelocity(){
        this.yVelocity *= -1;
    }

    /**
     * Sets a new x-velocity for the ball.
     *
     * @param newXVelocity The new x-velocity to set for the ball.
     */
    public void setXVelocity(float newXVelocity){
        this.xVelocity = newXVelocity;
    }

    /**
     * Sets a new y-velocity for the ball.
     *
     * @param newYVelocity The new y-velocity to set for the ball.
     */
    public void setYVelocity(float newYVelocity){
        this.yVelocity = newYVelocity;
    }

    /**
     * Gets the color of the ball.
     *
     * @return The color of the ball.
     */
    public String getColor(){
        return this.color;
    }

    /**
     * Sets the color of the ball.
     *
     * @param color The new color to set for the ball.
     */
    public void setColor(String color){
        this.color = color;
    }

    /**
     * Checks if the ball is marked for removal.
     *
     * @return True if the ball is marked for removal, false otherwise.
     */
    public boolean isRemove(){
        return this.isRemove;
    }

    /**
     * Marks the ball for removal.
     */
    public void removeBall(){
        this.isRemove = true;
    }

    /**
     * Checks if the ball is attracted.
     *
     * @return True if the ball is being attracted, false otherwise.
     */
    public boolean isAttract(){
        return this.isAttract;
    }

    /**
     * Sets the attraction state of the ball.
     *
     * @param attracted True to attract the ball, false otherwise.
     */
    public void attractBall(boolean attracted){
        this.isAttract = attracted;
    }

    /**
     * Sets the shrinking state of the ball.
     *
     * @param shrinking True to set the ball as shrinking, false otherwise.
     */
    public void setShrinking(boolean shrinking) {
        this.isShrinking = shrinking; // Set the shrinking state
    }

    /**
     * Checks if the ball is currently shrinking.
     *
     * @return True if the ball is shrinking, false otherwise.
     */
    public boolean isShrinking() {
        return isShrinking; // Return whether the ball is shrinking
    }

    /**
     * Shrinks the ball based on the distance from a hole.
     *
     * @param distance The distance from the hole used to determine the new radius.
     */
    public void shrink(float distance) {
        if (isShrinking) {
            if (distance < 32){
                // Adjust current radius based on distance
                currentRadius = Math.max(0, radius * (distance/32));
            }
            
        }
    }

    /**
     * Checks if the ball is captured.
     *
     * @return True if the ball is captured, false otherwise.
     */
    public boolean isCaptured(){
        return this.isCaptured;
    }

    /**
     * Sets the captured state of the ball.
     *
     * @param captured True to capture the ball, false otherwise.
     */
    public void capturedBall(boolean captured){
        this.isCaptured = captured;
    }

    /**
     * Updates the position of the ball based on its velocity.
     * This method should be called every frame to move the ball.
     */
    public void updatePosition(){
        // System.out.println("Current Velocity: (" + xVelocity + ", " + yVelocity + ")");
        this.x += xVelocity;
        this.y += yVelocity;
    }

    /**
     * Checks for collisions with the boundaries of the application window.
     * If the ball hits a boundary, its velocity is reversed.
     */
    public void checkCollision(){
        if (x < 0 || x+2*currentRadius > App.WIDTH){
            xVelocity *= -1;
        }
        if (y < App.TOPBAR || y+2*currentRadius > App.HEIGHT){
            yVelocity *= -1;
            // System.out.println("now the ball is in (" + x + ", " + y + ")");
        }
    }

    /**
     * Draws the ball on the application canvas.
     * Uses the ball's color to retrieve the appropriate image from the application.
     *
     * @param app The main application instance used for rendering.
     */
    public void draw(App app){
        PImage image = null;
        if (color.equals("grey")){
            image = app.getImage("ball0");
        } else if (color.equals("orange")){
            image = app.getImage("ball1");
        } else if (color.equals("blue")){
            image = app.getImage("ball2");
        } else if (color.equals("green")){
            image = app.getImage("ball3");
        } else if (color.equals("yellow")){
            image = app.getImage("ball4");
        } else {
            // System.out.println("unknow ball color...");
        }

        checkCollision();  // Check whether the ball will hit the board
        // Only draw if not removed or captured
        if ((!this.isRemove) && (!this.isCaptured)){
            if (image != null){
                app.image(image, x, y, currentRadius * 2, currentRadius * 2);
                // System.out.println("ball image is not null");
            } else{
                // System.out.println("ball image is null");
            }
        }
    }
}