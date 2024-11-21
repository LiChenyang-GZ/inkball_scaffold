package inkball;

/**
 * The Vec class represents a 2D vector with x and y components.
 * It provides methods for vector operations such as distance calculation, addition, normalization, and dot product.
 */
public class Vec {
    float x;
    float y;

    /**
     * Constructor for the Vec class.
     * Initializes the vector with given x and y components.
     *
     * @param x The x component of the vector.
     * @param y The y component of the vector.
     */
    public Vec(float x, float y){
        this.x = x;
        this.y = y;
    }

    /**
     * Calculates the distance between this vector and another vector.
     *
     * @param other The other vector to calculate the distance to.
     * @return The distance between the two vectors.
     */
    public float distanceTo(Vec other) {
        return (float) Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2));
    }

    /**
     * Adds another vector to this vector and returns the result.
     *
     * @param v The vector to add.
     * @return A new Vec representing the sum of this vector and the given vector.
     */
    public Vec add(Vec v) {
        return new Vec(this.x + v.x, this.y + v.y);
    }

    /**
     * Calculates the normal vector to this vector.
     * Returns the normalized version of the normal vector.
     *
     * @return A new Vec representing the normalized normal vector.
     */
    public Vec normal(){
        return new Vec(-this.y, this.x).normalize();
    }

    /**
     * Normalizes this vector, returning a unit vector in the same direction.
     *
     * @return A new Vec representing the normalized vector.
     */
    public Vec normalize(){
        float magnitude = (float) Math.sqrt(x * x + y * y);
        if (magnitude == 0){
            return new Vec(0, 0);
        }
        return new Vec(x / magnitude, y / magnitude);
    }

    /**
     * Returns the two perpendicular (normal) vectors to this vector, normalized.
     *
     * @return An array of two normalized perpendicular vectors.
     */
    public Vec[] perpendicular() {
        Vec n1 = new Vec(-this.y, this.x);   // First normal vector
        Vec n2 = new Vec(this.y, -this.x);   // Second normal vector
        return new Vec[]{n1.normalize(), n2.normalize()}; // 返回规范化的法线
    }

    /**
     * Calculates the dot product of this vector and another vector.
     *
     * @param other The other vector to calculate the dot product with.
     * @return The dot product of the two vectors.
     */
    public float dot(Vec other) {
        return this.x * other.x + this.y * other.y;
    }
}