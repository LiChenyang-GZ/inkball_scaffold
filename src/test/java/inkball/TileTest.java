package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PImage;
import processing.core.PApplet;


class TileTest {
    private Tile tile;
    private App app;
    private Ball ball;

    @BeforeEach
    void setUp() {
        app = new App();
        PApplet.runSketch(new String[] {"inkball"}, app);
        app.setup();
        ball = new Ball(1, 1, "grey");
        tile = new Tile(1, 1, "X");
    }

    @Test
    void testConstructor() {
        // Test the constructor to ensure properties are set correctly.
        assertEquals(1, tile.getX());
        assertEquals(1, tile.getY());
        assertEquals("X", tile.getType());
    }

    @Test
    void testGetImage() {
        // Test getting the image associated with the tile.
        PImage image = app.getImage("wall0");
        tile.setImage(image);
        assertEquals(tile.image, app.getImage("wall0"));
    }

    @Test
    void testSetImage(){
        // Test setting the image for the tile.
        PImage image = app.getImage("wall0");
        tile.setImage(image);
        assertEquals(image, tile.getImage());
    }

    @Test
    void testDraw() {
        // Test the draw method to ensure it executes without exceptions.
        PImage image = app.getImage("wall0");
        tile.setImage(image);
        assertDoesNotThrow(() -> tile.draw(app));
    }

    @Test
    void testIsHole() {
        // Test if the tile is a hole.
        Tile holeTile = new Tile(1, 1, "H1");
        assertTrue(holeTile.isHole());
        assertFalse(tile.isHole());
    }

    @Test
    void testIsOverLap() {
        // Test if the ball overlaps with the tile.
        ball.setX(32);
        ball.setY(96);
        assertTrue(tile.isOverLap(ball));
        ball.setX(100); // move the ball away
        assertFalse(tile.isOverLap(ball));
    }

    @Test
    void testCheckCollision() {
        // Test collision detection and position change of the ball.
        tile.checkCollision(ball);
        assertNotEquals(15, ball.getX());
        assertNotEquals(15, ball.getY());
    }

    @Test
    void testCheckCollisionColorChangeOrange() {
        // Test color change to orange on collision with tile type "1".
        ball = new Ball(32, 96, "grey");
        Tile newTile = new Tile(1, 1, "1");
        newTile.checkCollision(ball);
        assertEquals("orange", ball.getColor());
    }

    @Test
    void testCheckCollisionColorChangeBlue() {
        // Test color change to orange on collision with tile type "2".
        ball = new Ball(32, 96, "grey");
        Tile newTile = new Tile(1, 1, "2");
        newTile.checkCollision(ball);
        assertEquals("blue", ball.getColor());
    }

    @Test
    void testCheckCollisionColorChangeGreen() {
        // Test color change to orange on collision with tile type "3".
        ball = new Ball(32, 96, "grey");
        Tile newTile = new Tile(1, 1, "3");
        newTile.checkCollision(ball);
        assertEquals("green", ball.getColor());
    }

    @Test
    void testCheckCollisionColorChangeYellow() {
        // Test color change to orange on collision with tile type "4".
        ball = new Ball(32, 96, "grey");
        Tile newTile = new Tile(1, 1, "4");
        newTile.checkCollision(ball);
        assertEquals("yellow", ball.getColor());
    }

    @Test
    void testAttractBallSuccess() {
        // Test the attraction of the ball to a hole tile.
        Tile holeTile = new Tile(2, 2, "H1");
        ball.setX(32);
        ball.setY(96);
        String result = holeTile.attractBall(ball);
        assertNotNull(holeTile.getHoleCenter());
        assertTrue(ball.isAttract());
    }

    @Test
    void testAttractBallResult(){
        // Test the result of attracting the ball to a hole tile.
        Tile holeTile = new Tile(2, 2, "H1");
        ball.setX(64);
        ball.setY(128);
        String type = holeTile.attractBall(ball);
        assertEquals("H1", type);
    }

    @Test
    void testAttractBallFail() {
        // Test failure to attract the ball when outside range.
        Tile holeTile = new Tile(2, 2, "H0");
        ball.setX(1);
        ball.setY(1);
        String result = holeTile.attractBall(ball);
        assertNull(result);
    }


    @Test
    void testHoleCenterFail(){
        // Test getting the hole center from a non-hole tile.
        Tile notHoleTile = new Tile(2, 2, "B2");
        assertNull(notHoleTile.getHoleCenter());
    }

    @Test
    void testHitTile() {
        // Test hitting the tile and counting hits.
        tile.hits = 0;
        ball.setX(32);
        ball.setY(96);
        tile.hitTile(ball, app);
        assertEquals(1, tile.hits);

        // Hit again
        tile.hitTile(ball, app);
        assertEquals(2, tile.hits);

        // Third hit, check type reset
        tile.hitTile(ball, app);
        assertEquals(3, tile.hits);
        assertEquals("tile", tile.getType());
    }

    @Test
    void testHitOrangeTile(){
        // Test hitting an orange tile.
        Tile newTile = new Tile(1, 1, "1");
        newTile.hits = 0;
        ball.setX(32);
        ball.setY(96);
        ball.setColor("orange");
        newTile.hitTile(ball, app);
        assertEquals(1, newTile.hits);
        assertEquals(app.getImage("cracked1"), newTile.getImage());
    }

    @Test
    void testHitBlueTile(){
        // Test hitting a blue tile.
        Tile newTile = new Tile(1, 1, "2");
        newTile.hits = 0;
        ball.setX(32);
        ball.setY(96);
        ball.setColor("blue");
        newTile.hitTile(ball, app);
        assertEquals(1, newTile.hits);
        assertEquals(app.getImage("cracked2"), newTile.getImage());
    }

    @Test
    void testHitGreenTile(){
        // Test hitting a green tile.
        Tile newTile = new Tile(1, 1, "3");
        newTile.hits = 0;
        ball.setX(32);
        ball.setY(96);
        ball.setColor("green");
        newTile.hitTile(ball, app);
        assertEquals(1, newTile.hits);
        assertEquals(app.getImage("cracked3"), newTile.getImage());
    }

    @Test
    void testHitYellowTile(){
        // Test hitting a yellow tile.
        Tile newTile = new Tile(1, 1, "4");
        newTile.hits = 0;
        ball.setX(32);
        ball.setY(96);
        ball.setColor("yellow");
        newTile.hitTile(ball, app);
        assertEquals(1, newTile.hits);
        assertEquals(app.getImage("cracked4"), newTile.getImage());
    }

    @Test
    void testSetYellowPosition() {
        // Test setting the position of a yellow tile.
        tile.setYellowPosition(2, 2, app);
        assertEquals(2, tile.getX());
        assertEquals(2, tile.getY());
    }

    @Test
    void testSetOldPosition() {
        // Test setting the tile type to "old".
        tile.setOldPosition();
        assertEquals("old", tile.getType());
    }
}