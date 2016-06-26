package applications.trajectory;

import com.google.auto.value.AutoValue;

/**
 * Point class for grouping a point in 4d space with angular orientation.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
@AutoValue
public abstract class Point4D {
    /**
     * @return The X coordinate.
     */
    public abstract double getX();

    /**
     * @return The Y coordinate.
     */
    public abstract double getY();

    /**
     * @return The Z coordinate.
     */
    public abstract double getZ();

    /**
     * @return The yaw angle orientation.
     */
    public abstract double getAngle();

    /**
     * Create a new point object
     *
     * @param x     the x coordinate.
     * @param y     the Y coordinate.
     * @param z     the Z coordinate.
     * @param angle the yaw angle.
     * @return A value class representing the given coordinates.
     */
    public static Point4D create(double x, double y, double z, double angle) {
        return new AutoValue_Point4D(x, y, z, angle);
    }

    /**
     * @return A coordinate instance representing (0,0,0,0).
     */
    public static Point4D origin() {
        return new AutoValue_Point4D(0, 0, 0, 0);
    }

    /**
     * @param targetPoint The destination of the vector.
     * @param sourcePoint the source of the vector.
     * @return A point instance representing the difference or distance
     * between the given points.
     */
    public static Point4D minus(Point4D targetPoint, Point4D sourcePoint) {
        return create(targetPoint.getX() - sourcePoint.getX(),
                targetPoint.getY() - sourcePoint.getY(),
                targetPoint.getZ() - sourcePoint.getZ(),
                targetPoint.getAngle() - sourcePoint.getAngle());
    }
}
