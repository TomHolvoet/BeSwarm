package applications.simulations.trajectory;

import com.google.auto.value.AutoValue;

/**
 * Point class for grouping a point in 4d space with angular orientation.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
@AutoValue
public abstract class Point4D {

    public abstract double getX();

    public abstract double getY();

    public abstract double getZ();

    public abstract double getAngle();

    public static Point4D create(double x, double y, double z, double angle) {
        return new AutoValue_Point4D(x, y, z, angle);
    }

    public static Point4D origin() {
        return new AutoValue_Point4D(0, 0, 0, 0);
    }
}
