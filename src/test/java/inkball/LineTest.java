package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.core.PApplet;

class LineTest {
    private Line line;
    private App app;

    @BeforeEach
    void setUp() {
        app = new App();
        PApplet.runSketch(new String[] {"inkball"}, app);
        app.setup();
        line = new Line();
    }

    @Test
    void testAddPoint() {
        // Test adding points to the line and ensure they are stored correctly.
        line.addPoint(10, 20);
        line.addPoint(15, 25);
        
        assertEquals(2, line.points.size());
        assertEquals(10, line.points.get(0).x);
        assertEquals(20, line.points.get(0).y);
        assertEquals(15, line.points.get(1).x);
        assertEquals(25, line.points.get(1).y);
    }

    @Test
    void testDraw() {
        // Test the draw method to ensure it executes without throwing exceptions.
        line.addPoint(10, 20);
        line.addPoint(15, 25);
        assertDoesNotThrow(() -> line.draw(app));
    }

    @Test
    void testBallDistanceWithLine() {
        // Test the distance check between a ball and a line.
        Ball ball = new Ball(5, 5, "grey");
        ball.setXVelocity(2);
        ball.setYVelocity(2);
        Vec p1 = new Vec(10, 20);
        Vec p2 = new Vec(15, 25);
        assertTrue(line.ballDistanceWithLine(ball, p1, p2));
        ball.setX(20);
        assertFalse(line.ballDistanceWithLine(ball, p1, p2));
    }

    @Test
    void testBallDistanceWithLineFar() {
        // Test the distance check between a ball and a line.
        Ball ball = new Ball(20, 5, "grey");
        ball.setXVelocity(2);
        ball.setYVelocity(2);
        Vec p1 = new Vec(10, 20);
        Vec p2 = new Vec(15, 25);
        assertFalse(line.ballDistanceWithLine(ball, p1, p2));
    }

    @Test
    void testLineCollisionSuccess() {
        // Test whether the ball collides with the line.
        Ball ball = new Ball(5, 5, "grey");
        ball.setXVelocity(2);
        ball.setYVelocity(2);
        line.addPoint(10, 20);
        line.addPoint(15, 25);
        assertTrue(line.isLineCollision(ball));
        ball.setX(20);
        assertFalse(line.isLineCollision(ball));
    }

    @Test
    void testLineCollisionFail() {
        // Test the ball not collides with the line.
        Ball ball = new Ball(20, 5, "grey");
        ball.setXVelocity(2);
        ball.setYVelocity(2);
        line.addPoint(10, 20);
        line.addPoint(15, 25);
        assertFalse(line.isLineCollision(ball));
    }

    @Test
    void testReflectBall() {
        // Test the reflection of the ball when it collides with the line.
        Ball ball = new Ball(5, 5, "grey");
        ball.setXVelocity(2);
        ball.setYVelocity(2);
        line.addPoint(10, 20);
        line.addPoint(15, 25);
        line.isLineCollision(ball);
        assertNotEquals(-2, ball.getXVelocity()); // Ensure the ball's velocity has changed
        assertNotEquals(-2, ball.getYVelocity());
    }

    @Test
    void testContainsPoint() {
        // Test whether a point is contained within the line's thickness.
        line.addPoint(10, 20);
        line.addPoint(15, 25);
        assertTrue(line.containsPoint(12, 22));
    }

    @Test
    void testNotContainsPoint() {
        // Test a point is not contained within the line's thickness.
        line.addPoint(10, 20);
        line.addPoint(15, 25);
        assertFalse(line.containsPoint(5, 5));
    }    
}