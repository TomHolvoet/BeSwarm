package applications.trajectory.points;

import com.google.auto.value.AutoValue;

/**
 * Point class for grouping a point in 4d space with angular orientation.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
@AutoValue
public abstract class Point3D {
    /**
     * Create a new point object
     *
     * @param x     the x coordinate.
     * @param y     the Y coordinate.
     * @param z     the Z coordinate.
     * @return A value class representing the given coordinates.
     */
    public static Point3D create(double x, double y, double z) {
        return new AutoValue_Point3D(x, y, z);
    }

    /**
     * @return A coordinate instance representing (0,0,0,0).
     */
    public static Point3D origin() {
        return new AutoValue_Point3D(0, 0, 0);
    }

    /**
     * @param arg The point4D to project onto the primary 3 dimensions.
     * @return
     */
    public static Point3D project(Point4D arg) {
        return new AutoValue_Point3D(arg.getX(), arg.getY(), arg.getZ());
    }

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
}
