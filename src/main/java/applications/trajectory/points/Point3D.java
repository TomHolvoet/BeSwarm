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
     * @param x the x coordinate.
     * @param y the Y coordinate.
     * @param z the Z coordinate.
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
     * Gets the euclidean distance between two points.
     *
     * @param p0 the first point
     * @param p1 the second point
     * @return the euclidean distance between {@code p0} and {@code p1}
     */
    public static double distance(Point3D p0, Point3D p1) {
        return StrictMath.sqrt(
                (p0.getX() - p1.getX()) * (p0.getX() - p1.getX()) + (p0.getY() - p1.getY()) * (p0.getY() - p1.getY())
                        + (p0.getZ() - p1.getZ()) * (p0.getZ() - p1.getZ()));
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
