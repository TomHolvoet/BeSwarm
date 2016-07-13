package control.localization;

import com.google.auto.value.AutoValue;
import com.google.common.annotations.VisibleForTesting;

/**
 * @author Hoang Tung Dinh
 */
@VisibleForTesting
@AutoValue
abstract class QuaternionAngle {

    QuaternionAngle() {}

    abstract double w();

    abstract double x();

    abstract double y();

    abstract double z();

    public static Builder builder() {
        return new AutoValue_QuaternionAngle.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        abstract Builder setW(double value);

        abstract Builder setX(double value);

        abstract Builder setY(double value);

        abstract Builder setZ(double value);

        abstract QuaternionAngle build();
    }
}
