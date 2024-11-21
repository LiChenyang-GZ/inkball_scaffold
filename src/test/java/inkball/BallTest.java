package inkball;

import processing.core.PApplet;
import processing.core.PImage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;


public class BallTest {

    private Ball ball;
    private App app;

    @BeforeEach
    public void setUp() {
        app = new App();
        PApplet.runSketch(new String[]{"inkball"}, app);
        app.setup();
        ball = new Ball(50, 50, "grey");
    }


    @Test
    public void testGetX() {
        // Test the getX method to ensure it returns the correct x-coordinate.
        assertEquals(50, ball.getX(), 1);
    }

    @Test
    public void testGetY() {
        // Test the getY method to ensure it returns the correct y-coordinate.
        assertEquals(50, ball.getY(), 1);
    }

    @Test
    public void testGetRadius() {
        // Test the getRadius method to ensure it returns the correct radius.
        assertEquals(12, ball.getRadius(), 1);
    }

    @Test
    public void testSetRadius() {
        // Test setting a new radius and ensure it updates correctly.
        ball.setRadius(15);
        assertEquals(15, ball.getRadius(), 1);
    }

    @Test
    public void testSetCurrentRadius() {
        // Test setting the current radius and ensure it updates correctly.
        ball.setCurrentRadius(10);
        assertEquals(10, ball.getCurrentRadius(), 1);
    }

    @Test
    public void testSetColor() {
        // Test setting a new color for the ball.
        ball.setColor("yellow");
        assertEquals("yellow", ball.getColor());
    }

    @Test
    public void testSetX() {
        // Test setting a new x-coordinate and ensure it updates correctly.
        ball.setX(100);
        assertEquals(100, ball.getX(), 1);
    }

    @Test
    public void testSetY() {
        // Test setting a new y-coordinate and ensure it updates correctly.
        ball.setY(100);
        assertEquals(100, ball.getY(), 1);
    }

    @Test
    public void testReverseXVelocity() {
        // Test reversing the x-velocity of the ball.
        float originalVelocity = ball.getXVelocity();
        ball.reverseXVelocity();
        assertEquals(-originalVelocity, ball.getXVelocity(), 0.01);
    }

    @Test
    public void testReverseYVelocity() {
        // Test reversing the y-velocity of the ball.
        float originalVelocity = ball.getYVelocity();
        ball.reverseYVelocity();
        assertEquals(-originalVelocity, ball.getYVelocity(), 0.01);
    }

    @Test
    public void testAttractBall() {
        // Test the attractBall method to ensure it sets the attraction state correctly.
        ball.attractBall(true);
        assertTrue(ball.isAttract());
    }

    @Test
    public void testAttractBallFail(){
        // Test the attractBall method when setting attraction to false.
        ball.attractBall(false);
        assertFalse(ball.isAttract());
    }

    @Test
    public void testRemoveBall() {
        // Test removing the ball to ensure its state is updated correctly.
        ball.removeBall();
        assertTrue(ball.isRemove());
    }

    @Test
    public void testNotShrink() {
        // Test the shrinking behavior when the ball is not allowed to shrink.
        ball.setShrinking(true);
        ball.shrink(32);
        assertEquals(12.0f, ball.getCurrentRadius(), 0.1);
    }

    @Test
    public void testIsShrink() {
        // Test the shrinking behavior when allowed to shrink.
        ball.setShrinking(true);
        ball.shrink(16);
        assertEquals(6.0f, ball.getCurrentRadius(), 0.1);
    }

    @Test
    public void testCaptureBall() {
        // Test capturing the ball to ensure its state is updated correctly.
        ball.capturedBall(true);
        assertTrue(ball.isCaptured());
        ball.capturedBall(false);
        assertFalse(ball.isCaptured());
    }

    @Test
    public void testCaptureBallFail() {
        // Test capturing the ball when it is not captured.
        ball.capturedBall(false);
        assertFalse(ball.isCaptured());
    }

    @Test
    public void testUpdatePosition() {
        // Test updating the position of the ball.
        float initialX = ball.getX();
        float initialY = ball.getY();
        ball.updatePosition();
        assertNotEquals(initialX, ball.getX());
        assertNotEquals(initialY, ball.getY());
    }

    @Test
    public void testCheckCollisionLeftBoundary() {
        // Test collision detection with boundaries.
        ball.setXVelocity(2);
        ball.setYVelocity(2);
        ball.setX(-5); // Test left boundary collision
        ball.setY(100);
        ball.checkCollision();
        assertTrue(ball.getXVelocity() < 0);
        assertEquals(-2, ball.getXVelocity());
        assertEquals(2, ball.getYVelocity());
    }

    @Test
    public void testCheckCollisionRightBoundary() {
        // Test collision detection with boundaries.
        ball.setXVelocity(2);
        ball.setYVelocity(2);
        ball.setX(App.WIDTH + 5); // Test right boundary collision
        ball.setY(100);
        ball.checkCollision();
        assertTrue(ball.getXVelocity() < 0);
        assertEquals(-2, ball.getXVelocity());
        assertEquals(2, ball.getYVelocity());
    }

    @Test
    public void testCheckCollisionTopBoundary() {
        // Test collision detection with boundaries.
        ball.setXVelocity(2);
        ball.setYVelocity(2);
        ball.setX(20); 
        ball.setY(-5);  // Test top boundary collision
        ball.checkCollision();
        assertTrue(ball.getYVelocity() < 0);
        assertEquals(2, ball.getXVelocity());
        assertEquals(-2, ball.getYVelocity());
    }

    @Test
    public void testCheckCollisionBottomBoundary() {
        // Test collision detection with boundaries.
        ball.setXVelocity(2);
        ball.setYVelocity(2);
        ball.setX(20); 
        ball.setY(App.HEIGHT + 5);  // Test bottom boundary collision
        ball.checkCollision();
        assertTrue(ball.getYVelocity() < 0);
        assertEquals(2, ball.getXVelocity());
        assertEquals(-2, ball.getYVelocity());
    }



    @Test
    public void testIsShrinking() {
        // Test the shrinking state of the ball.
        ball.setShrinking(true);
        assertTrue(ball.isShrinking());
        
        ball.setShrinking(false);
        assertFalse(ball.isShrinking());
    }
    
}