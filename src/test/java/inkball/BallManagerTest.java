package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BallManagerTest {
    private BallManager ballManager;

    @BeforeEach
    public void setUp() {
        LinkedList<String> initialColors = new LinkedList<>();
        initialColors.add("grey");
        initialColors.add("blue");
        initialColors.add("green");
        ballManager = new BallManager(initialColors);
    }

    @Test
    public void testGetColorValidIndex() {
        // Test getting colors at valid indices.
        assertEquals("grey", ballManager.getColor(0));
        assertEquals("blue", ballManager.getColor(1));
        assertEquals("green", ballManager.getColor(2));
    }

    @Test
    public void testGetColorInvalidIndex() {
        // Test getting colors at invalid indices.
        assertEquals("ball color out of bound...", ballManager.getColor(-1));
        assertEquals("ball color out of bound...", ballManager.getColor(3)); // 超出范围
    }

    @Test
    public void testRemoveColorAtValidIndex() {
        // Test removing a color at a valid index.
        ballManager.removeColorAt(1);
        assertEquals(2, ballManager.size());
        assertEquals("green", ballManager.getColor(1));
    }

    @Test
    public void testRemoveColorAtInvalidIndex() {
        // Test trying to remove a color at an invalid index.
        int initialSize = ballManager.size();
        ballManager.removeColorAt(-1); // Invalid index, should not change size
        assertEquals(initialSize, ballManager.size());

        ballManager.removeColorAt(3); // Invalid index, should not change size
        assertEquals(initialSize, ballManager.size());
    }

    @Test
    public void testSize() {
        // Test the size of the color list.
        assertEquals(3, ballManager.size());
        ballManager.addColor("yellow");
        assertEquals(4, ballManager.size());
    }

    @Test
    public void testGetRemainingBallColors() {
        // Test getting the remaining ball colors.
        List<String> remainingColors = ballManager.getRemainingBallColors();
        assertEquals(3, remainingColors.size());
        assertTrue(remainingColors.contains("grey"));
        assertTrue(remainingColors.contains("blue"));
        assertTrue(remainingColors.contains("green"));
    }

    @Test
    public void testAddColor() {
        // Test adding a new color.
        ballManager.addColor("yellow");
        assertEquals(4, ballManager.size());
        assertEquals("yellow", ballManager.getColor(3));
    }
}