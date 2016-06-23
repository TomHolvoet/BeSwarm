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
