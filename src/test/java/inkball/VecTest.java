package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VecTest {
    private Vec vec1;
    private Vec vec2;

    @BeforeEach
    void setUp() {
        vec1 = new Vec(3, 4);
        vec2 = new Vec(1, 2);
    }

    @Test
    void testConstructor() {
        // Test the constructor to ensure properties are set correctly.
        assertEquals(3, vec1.x);
        assertEquals(4, vec1.y);
    }

    @Test
    void testDistanceTo() {
        // Test the distance calculation between two vectors.
        assertEquals(2.828, vec1.distanceTo(vec2), 1e-2);
    }

    @Test
    void testAdd() {
        // Test the addition of two vectors.
        Vec result = vec1.add(vec2);
        assertEquals(4, result.x);
        assertEquals(6, result.y);
    }

    @Test
    void testNormalize() {
        // Test normalization of the vector.
        Vec normalized = vec1.normalize();
        assertEquals(0.6, normalized.x, 1e-2);
        assertEquals(0.8, normalized.y, 1e-2);
    }

    @Test
    void testNormal() {
        // Test calculation of the normal vector.
        Vec normal = vec1.normal();
        assertEquals(-0.8, normal.x, 1e-2);
        assertEquals(0.6, normal.y, 1e-2);
    }

    @Test
    void testPerpendicular() {
        // Test calculation of perpendicular vectors.
        Vec[] perpendiculars = vec1.perpendicular();
        assertEquals(-0.8, perpendiculars[0].x, 1e-2);
        assertEquals(0.6, perpendiculars[0].y, 1e-2);
        assertEquals(0.8, perpendiculars[1].x, 1e-2);
        assertEquals(-0.6, perpendiculars[1].y, 1e-2);
    }

    @Test
    void testDot() {
        // Test the dot product of two vectors.
        assertEquals(11.0, vec1.dot(vec2), 1e-2);
        assertEquals(0.0, vec1.dot(new Vec(-4, 3)), 1e-2);
    }

    @Test
    void testNormalizeZeroVector() {
        // Test normalization of a zero vector.
        Vec zeroVec = new Vec(0, 0);
        Vec normalized = zeroVec.normalize();
        assertEquals(0, normalized.x);
        assertEquals(0, normalized.y);
    }
}