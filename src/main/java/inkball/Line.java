package inkball;

import java.util.*;
import processing.core.PApplet; 

/**
 * The Line class represents a line made up of a series of points.
 * It provides methods for adding points, drawing the line, detecting collisions with balls,
 * reflecting balls off the line, and checking if a point is within the line's thickness.
 */
public class Line {
    protected List<Vec> points = new ArrayList<>();
    protected int thickness = 10;

    /**
     * Adds a new point to the line.
     *
     * @param x The x-coordinate of the point to add.
     * @param y The y-coordinate of the point to add.
     */
    public void addPoint(float x, float y){
        points.add(new Vec(x, y));
    }

    /**
     * Draws the line on the application canvas.
     *
     * @param app The application to draw to.
     */
    public void draw(App app){
        app.stroke(0);
        app.strokeWeight(thickness);

        for (int i = 0; i < points.size() - 1; i++){
            Vec p1 = points.get(i);
            Vec p2 = points.get(i+1);
            // Only draw the line segment if the points are below the topbar
            if (p1.y > App.TOPBAR && p2.y > App.TOPBAR) {
                app.line(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    /**
     * Checks if the distance between a ball and a line segment is less than the ball's radius.
     *
     * @param ball The ball to check for collision with the line.
     * @param p1 The first point of the line segment.
     * @param p2 The second point of the line segment.
     * @return True if the ball is close enough to the line segment; false otherwise.
     */
    public boolean ballDistanceWithLine(Ball ball, Vec p1, Vec p2){
        float ballCenterX = ball.getX() + ball.getRadius();
        float ballCenterY = ball.getY() + ball.getRadius();
        Vec ballCenter = new Vec(ballCenterX, ballCenterY);
        Vec ballVelocity = new Vec(ball.getXVelocity(), ball.getYVelocity());
        double distance1 = p1.distanceTo(ballCenter.add(ballVelocity));
        double distance2 = p2.distanceTo(ballCenter.add(ballVelocity));
        double distance3 = p1.distanceTo(p2) + ball.getRadius();
        return (distance1+distance2) < distance3;
    }

    /**
     * Checks for collisions between the ball and the line segments.
     *
     * @param ball The ball to check for collisions.
     * @return True if a collision is detected; false otherwise.
     */
    public boolean isLineCollision(Ball ball){
        for (int i = 0; i < points.size()-1; i++){
            Vec p1 = points.get(i);
            Vec p2 = points.get(i+1);
            if (ballDistanceWithLine(ball, p1, p2)){
                reflectBall(ball, p1, p2);
                return true;
            }
        }
        return false;
    }

    /**
     * Reflects a ball off a line segment based on its current position and velocity.
     *
     * @param ball The ball to reflect.
     * @param p1 The first point of the line segment.
     * @param p2 The second point of the line segment.
     */
    public void reflectBall(Ball ball, Vec p1, Vec p2){
        // Cal the normal vectors of the line segment
        Vec direction = new Vec(p2.x - p1.x, p2.y - p1.y);
        Vec[] normals = direction.perpendicular(); // obtain the normal vectors

        // Cal the midpoint of the line segment
        Vec midpoint = new Vec((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
        Vec ballPosition = new Vec(ball.getX()+ball.getRadius(), ball.getY()+ball.getRadius());

        // Select the closest normal to the ball's position
        Vec selectedNormal;
        if (midpoint.distanceTo(ballPosition)<midpoint.add(normals[0]).distanceTo(ballPosition)){
            selectedNormal = normals[0];
        } else {
            selectedNormal = normals[1];
        }

        // Calculate the new velocity for the ball
        Vec velocity = new Vec(ball.getXVelocity(), ball.getYVelocity());
        float dotProduct = velocity.dot(selectedNormal);
        // Reflect the ball's velocity
        float newXVelocity = velocity.x - 2 * dotProduct * selectedNormal.x;
        float newYVelocity = velocity.y - 2 * dotProduct * selectedNormal.y;
        ball.setXVelocity(newXVelocity);
        ball.setYVelocity(newYVelocity);

    }

    /**
     * Checks if a given point (e.g., mouse position) is within the line's thickness.
     *
     * @param mouseX The x-coordinate of the point to check.
     * @param mouseY The y-coordinate of the point to check.
     * @return True if the point is within the line's thickness; false otherwise.
     */
    public boolean containsPoint(float mouseX, float mouseY) {
        for (int i = 0; i < points.size() - 1; i++) {
            Vec p1 = points.get(i);
            Vec p2 = points.get(i + 1);
            
            // Calculate the midpoint of the line segment
            float closestX = (p1.x + p2.x) / 2;
            float closestY = (p1.y + p2.y) / 2;

            // Calculate the distance between the point and the closest point on the line segment
            float distance = PApplet.dist(mouseX, mouseY, closestX, closestY);
            if (distance <= thickness / 2) {
                return true;  // Point is within the thickness of the line
            }
        }
        return false;
    }
}