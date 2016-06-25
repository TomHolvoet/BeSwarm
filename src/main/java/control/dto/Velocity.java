package control.dto;

import com.google.auto.value.AutoValue;

/**
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class Velocity implements InertialFrameVelocity, BodyFrameVelocity {
    protected Velocity() {}

    @Override
    public abstract double linearX();

    @Override
    public abstract double linearY();

    @Override
    public abstract double linearZ();

    @Override
    public abstract double angularZ();

    /**
     * @return the yaw of the drone associated with the velocity in the body frame
     */
    @Override
    public abstract double poseYaw();

    public static Builder builder() {
        return new AutoValue_Velocity.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder linearX(double value);

        public abstract Builder linearY(double value);

        public abstract Builder linearZ(double value);

        public abstract Builder angularZ(double value);

        public abstract Builder poseYaw(double value);

        public abstract Velocity build();
    }
}
