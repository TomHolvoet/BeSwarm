package control.dto;

import com.google.auto.value.AutoValue;

/**
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class InertialFrameVelocity {
    protected InertialFrameVelocity() {}

    public abstract double linearX();

    public abstract double linearY();

    public abstract double linearZ();

    public abstract double angularZ();

    /**
     * The pose of the drone associated with this velocity. This value is optional and is only for transforming
     * velocity from global to local frame and vice versa.
     *
     * @return the pose of the drone associated with this velocity
     */
    public abstract Pose pose();

    public static Builder builder() {
        return new AutoValue_InertialFrameVelocity.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder linearX(double value);

        public abstract Builder linearY(double value);

        public abstract Builder linearZ(double value);

        public abstract Builder angularZ(double value);

        public abstract Builder pose(Pose pose);

        public abstract InertialFrameVelocity build();
    }
}
