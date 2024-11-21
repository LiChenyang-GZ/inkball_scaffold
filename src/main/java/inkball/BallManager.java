package inkball;

import java.util.*;

/**
 * The BallManager class manages a list of all ball colors of the level.
 * It allows adding, removing, and retrieving colors from the list.
 */
public class BallManager{
    private LinkedList<String> ballColors;

    /**
     * Constructor for the BallManager class.
     * Initializes the ball manager with a list of initial colors.
     *
     * @param initialColors A list of colors to initialize the ball manager.
     */
    public BallManager(LinkedList<String> initialColors){
        this.ballColors = new LinkedList<>(initialColors);
    }

    /**
     * Retrieves the color at the specified index.
     *
     * @param index The index of the color to retrieve.
     * @return The color at the specified index, or an error message if out of bounds.
     */
    public String getColor(int index){
        if (index >= 0 && index < ballColors.size()){
            return ballColors.get(index);
        } else {
            return "ball color out of bound...";
        }
    }

    /**
     * Removes the color at the specified index.
     *
     * @param index The index of the color to remove.
     */
    public void removeColorAt(int index){
        if (index >= 0 && index < ballColors.size()){
            ballColors.remove(index);
        }
    }

    /**
     * Gets the number of colors currently managed by the BallManager.
     *
     * @return The number of ball colors in the list.
     */
    public int size(){
        return ballColors.size();
    }

    /**
     * Retrieves a list of remaining ball colors.
     *
     * @return A list of remaining ball colors.
     */
    public List<String> getRemainingBallColors(){
        return new ArrayList<>(ballColors);
    }

    /**
     * Adds a new ball to the list of ball.
     *
     * @param color The color to add to the list.
     */
    public void addColor(String color) {
        ballColors.add(color);
    }

}