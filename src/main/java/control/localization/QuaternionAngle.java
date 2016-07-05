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
        abstract Builder w(double value);

        abstract Builder x(double value);

        abstract Builder y(double value);

        abstract Builder z(double value);

        abstract QuaternionAngle build();
    }
}
