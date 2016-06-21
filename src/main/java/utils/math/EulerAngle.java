package utils.math;

import com.google.auto.value.AutoValue;
import geometry_msgs.Quaternion;

/**
 * A value class which stores the euler angle in three dimensions.
 *
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class EulerAngle {

    EulerAngle() {}

    public abstract double angleX();

    public abstract double angleY();

    public abstract double angleZ();

    public static Builder builder() {
        return new AutoValue_EulerAngle.Builder();
    }

    /**
     * Compute euler angle from quarternion angle. The resulting angles are always in range [-pi, pi]
     *
     * @param quaternion the angle in quaternion representation
     * @return the angle in euler representation.
     * @see <a href="https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles">Equations</a>
     */
    public static EulerAngle createFromQuaternion(Quaternion quaternion) {
        final double q0 = quaternion.getW();
        final double q1 = quaternion.getX();
        final double q2 = quaternion.getY();
        final double q3 = quaternion.getZ();

        final double eulerX = StrictMath.atan2(2 * (q0 * q1 + q2 * q3), 1 - 2 * (q1 * q1 + q2 * q2));
        final double eulerY = StrictMath.asin(2 * (q0 * q2 - q3 * q1));
        final double eulerZ = StrictMath.atan2(2 * (q0 * q3 + q1 * q2), 1 - 2 * (q2 * q2 + q3 * q3));

        return builder().angleX(eulerX).angleY(eulerY).angleZ(eulerZ).build();
    }

    public static double computeAngleDistance(double firstAngle, double secondAngle) {
        double distance = secondAngle - firstAngle;

        while (distance < -Math.PI) {
            distance += 2 * Math.PI;
        }

        while (distance > Math.PI) {
            distance -= 2 * Math.PI;
        }

        return distance;
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder angleX(double value);

        public abstract Builder angleY(double value);

        public abstract Builder angleZ(double value);

        public abstract EulerAngle build();
    }
}
