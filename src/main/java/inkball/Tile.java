package inkball;

import processing.core.PImage;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * The Tile class represents a tile on the game board.
 * It handles tile properties such as position, type, and interactions with balls.
 */
public class Tile{
    private int x;
    private int y;
    protected PImage image;
    private String type; // The type of the tile (e.g., "X", "H1", etc.)
    private boolean isHole = false;
    protected int hits = 0;  // Counter for the number of hits the tile has received

    /**
     * Constructor for the Tile class.
     * Initializes the tile's position and type.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @param type The type of the tile.
     */
    public Tile(int x, int y, String type){
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Sets the image for the tile.
     *
     * @param img The image to be set for the tile.
     */
    public void setImage(PImage img){
        this.image = img;
    }

    /**
     * Gets the image of the tile.
     *
     * @return The current image of the tile.
     */
    public PImage getImage(){
        return this.image;
    }

    /**
     * Draws the tile on the App.
     *
     * @param app The main application to draw to.
     */
    public void draw(App app){
        if (image != null){
            app.image(image, x*app.CELLSIZE, y*app.CELLHEIGHT+app.TOPBAR);
        }
    }

    /**
     * Gets the x-coordinate of the tile.
     *
     * @return The x-coordinate.
     */
    public int getX(){
        return x;
    }

    /**
     * Gets the y-coordinate of the tile.
     *
     * @return The y-coordinate.
     */
    public int getY(){
        return y;
    }

    /**
     * Gets the type of the tile.
     *
     * @return The type as a string.
     */
    public String getType(){
        return this.type;
    }

    /**
     * Checks if the tile is a hole.
     *
     * @return True if the tile is a hole, false otherwise.
     */
    public boolean isHole(){
        return this.type.startsWith("H");
    }

    /**
     * Calculates the center x-coordinate of a ball.
     *
     * @param ball The ball whose center x-coordinate is to be calculated.
     * @return The center x-coordinate of the ball.
     */
    private float calBallCenterX(Ball ball){
        return ball.getX() + ball.getRadius();
    }

    /**
     * Calculates the center y-coordinate of a ball.
     *
     * @param ball The ball whose center y-coordinate is to be calculated.
     * @return The center y-coordinate of the ball.
     */
    private float calBallCenterY(Ball ball){
        return ball.getY() + ball.getRadius();
    }

    /**
     * Gets the center position of the hole if the tile is a hole.
     *
     * @return A Vec representing the hole's center, or null if not a hole.
     */
    public Vec getHoleCenter(){
        if (isHole()){
            return new Vec((x+1)*App.CELLSIZE, (y+1)*App.CELLHEIGHT+App.TOPBAR);
        }
        // System.out.println("no holes in the board...");
        return null;
    }

    /**
     * Checks if the ball overlaps with the tile.
     *
     * @param ball The ball to check for overlap.
     * @return True if the ball overlaps with the tile, false otherwise.
     */
    public boolean isOverLap(Ball ball){
        float ballCenterX = calBallCenterX(ball);
        float ballCenterY = calBallCenterY(ball);
        int ballRadius = ball.getRadius();

        // Calculate tile boundaries
        int tileLeft = x*App.CELLSIZE;
        int tileRight = tileLeft + App.CELLSIZE;
        int tileTop = y*App.CELLHEIGHT + App.TOPBAR;
        int tileBottom = tileTop + App.CELLHEIGHT;

        // Check for overlap based on tile type
        if (type.equals("X") || type.equals("1") || type.equals("2") || type.equals("3") || type.equals("4")){
            boolean overlap = ballCenterX + ballRadius > tileLeft && ballCenterX - ballRadius < tileRight
                        && ballCenterY + ballRadius > tileTop && ballCenterY - ballRadius < tileBottom;
            return overlap;
        } else{
            return false;
        }
    }

    /**
     * Checks for collision between the ball and the tile.
     * Adjusts the ball's position and direction upon collision.
     *
     * @param ball The ball involved in the collision.
     */
    public void checkCollision(Ball ball){
        if (!isOverLap(ball)){ // If no overlap
            return;
        }
        // System.out.println("the ball hit the tile!!!");
        if (type.equals("X") || type.equals("1") || type.equals("2") || type.equals("3") || type.equals("4")){
            float ballCenterX = calBallCenterX(ball);
            float ballCenterY = calBallCenterY(ball);
            int ballRadius = ball.getRadius();

            // Calculate tile boundaries
            int tileLeft = x*App.CELLSIZE;
            int tileRight = tileLeft + App.CELLSIZE;
            int tileTop = y*App.CELLSIZE + App.TOPBAR;
            int tileBottom = tileTop + App.CELLSIZE;

            // Calculate distances from ball edge to the wall edges
            float distToLeft = (ballCenterX - ballRadius) - tileLeft;
            float distToRight = tileRight - (ballCenterX + ballRadius);
            float distToTop = (ballCenterY - ballRadius) - tileTop;
            float distToBottom = tileBottom - (ballCenterY + ballRadius);

            // Find the minimum distance to determine the collision side
            float minDistance = Math.min(Math.min(distToLeft, distToRight), Math.min(distToTop, distToBottom));

            // Reflect the ball based on the closest side and adjust its position
            if (minDistance == distToLeft) {
                // Ball hit the left side of the wall, reverse X direction
                ball.reverseXVelocity();
                ball.setX(tileLeft - 2 * ballRadius);  // Move ball just outside the left wall boundary
            } else if (minDistance == distToRight) {
                // Ball hit the right side of the wall, reverse X direction
                ball.reverseXVelocity();
                ball.setX(tileRight);  // Move ball just outside the right wall boundary
            } else if (minDistance == distToTop) {
                // Ball hit the top side of the wall, reverse Y direction
                ball.reverseYVelocity();
                ball.setY(tileTop - 2 * ballRadius);  // Move ball just outside the top wall boundary
            } else if (minDistance == distToBottom) {
                // Ball hit the bottom side of the wall, reverse Y direction
                ball.reverseYVelocity();
                ball.setY(tileBottom);  // Move ball just outside the bottom wall boundary
            }

            // Change ball color based on tile type
            if (type.equals("0")){
                ball.setColor("grey");
            } else if (type.equals("1")){
                ball.setColor("orange");
            } else if (type.equals("2")){
                ball.setColor("blue");
            } else if (type.equals("3")){
                ball.setColor("green");
            } else if (type.equals("4")){
                ball.setColor("yellow");
            }
        }

    }

    /**
     * Attracts the ball towards the hole if it is within range.
     *
     * @param ball The ball to be attracted.
     * @return The type of the tile if the ball is captured, null otherwise.
     */
    public String attractBall(Ball ball) {
        Vec holeCenter = getHoleCenter();
        if (holeCenter != null) {
            Vec ballCenter = new Vec(calBallCenterX(ball), calBallCenterY(ball));
            PVector direction = new PVector((holeCenter.x-ballCenter.x), (holeCenter.y-ballCenter.y)); // Calculate direction
            float distance = direction.mag(); // Get the distance to the hole
            direction.normalize(); // Normalize the direction vector

            if (distance < 32 + ball.getRadius()) {
                ball.attractBall(true); // Activate ball attraction
                
                // Attraction force based on distance
                float attractionForce = 0.05f * (32 + ball.getCurrentRadius() - distance);
                ball.setXVelocity(ball.getXVelocity() + direction.x * attractionForce);
                ball.setYVelocity(ball.getYVelocity() + direction.y * attractionForce);

                // Set shrinking behavior
                ball.setShrinking(true);
                ball.shrink(distance);

                // If the ball is close enough to the hole, capture it
                if (distance < ball.getCurrentRadius()+10) {
                    ball.setX(holeCenter.x);
                    ball.setY(holeCenter.y);
                    ball.capturedBall(true); // the ball has been captured
                    
                    return this.type;
                }
            } else {
                ball.setShrinking(false); // Disable shrinking if not in range
            }
        } else {
            // System.out.println("no holes in the board...");
        }
        return null;
    }

    /**
     * Handles the interaction when a ball hits the tile.
     * Increments the hit counter and changes the tile image based on hits.
     *
     * @param ball The ball interacting with the tile.
     * @param app The main application instance used for image retrieval.
     */
    public void hitTile(Ball ball, App app){
        if (!isOverLap(ball)){ // If no overlap, exit
            return;
        }
        
        // hit the wall
        // System.out.println("the ball hit the wall...");
        if (type.equals("X") || app.getCodeFromColor(ball.getColor()).equals(this.type) ){
            this.hits++;
            // System.out.println("hit increase to: " + this.hits);
        }

        // Update tile image based on hits
        if (this.hits==1){
            if (this.type.equals("X")){
                this.setImage(app.getImage("cracked0"));
            } else {
                this.setImage(app.getImage("cracked"+this.type));
            }            
        }
        if (this.hits==3){ // Reset to default tile image
            this.setImage(app.getImage("tile"));
            this.type = "tile";
        }

    }

    /**
     * Sets the position of the tile and updates its image for yellow tiles.
     *
     * @param x The new x-coordinate of the tile.
     * @param y The new y-coordinate of the tile.
     * @param app The main application instance used for image retrieval.
     */
    public void setYellowPosition(int x, int y, App app){
        this.x = x;
        this.y = y;
        this.setImage(app.getImage("wall4"));
    }

    /**
     * Marks the tile as an "old" position.
     */
    public void setOldPosition(){
        this.type = "old";
    }
}
