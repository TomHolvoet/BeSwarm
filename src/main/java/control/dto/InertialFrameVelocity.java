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
     * @return the yaw of the drone associated with the velocity in the inertial frame
     */
    public abstract double poseYaw();

    public static Builder builder() {
        return new AutoValue_InertialFrameVelocity.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder linearX(double value);

        public abstract Builder linearY(double value);

        public abstract Builder linearZ(double value);

        public abstract Builder angularZ(double value);

        public abstract Builder poseYaw(double value);

        public abstract InertialFrameVelocity build();
    }
}
