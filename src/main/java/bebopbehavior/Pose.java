package bebopbehavior;

import com.google.auto.value.AutoValue;

/**
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class Pose {
    public abstract double x();
    public abstract double y();
    public abstract double z();
    public abstract double yaw();

    public static Builder builder() {
        return new AutoValue_Pose.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder x(double value);
        public abstract Builder y(double value);
        public abstract Builder z(double value);
        public abstract Builder yaw(double value);
        public abstract Pose build();
    }
}
