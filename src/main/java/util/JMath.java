package util;

import org.joml.Vector2f;

// This class is used to calculate the vertex of the rotated object
public class JMath {

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        // we subtract the vertex point from the center (origin)

        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        // Use trigonometric functions cos and sin to rotate the vector.
        // These functions expect the angle in radians, so we convert degrees to radians using Math.toRadians.
        float cos = (float)Math.cos(Math.toRadians(angleDeg));
        float sin = (float)Math.sin(Math.toRadians(angleDeg));

        // Calculating the new coordinates using the 2D rotation formula
        // x` = x.cos(angle) -y.sin(angle), y` = x.sin(angle) + y.cos(angle)
        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        // NOW after we find out the new coords we will add it back to the center so that we can find the new position
        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static boolean compare(float x, float y, float epsilon) {
        return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    public static boolean compare(float x, float y) {
        return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }
}