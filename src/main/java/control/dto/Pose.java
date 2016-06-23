package control.dto;

import com.google.auto.value.AutoValue;
import geometry_msgs.PoseStamped;
import utils.math.Transformations;

/**
 * A value class which stores the pose of the drone.
 *
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

    public static Pose create(PoseStamped poseStamped) {
        return Pose.builder()
                .x(poseStamped.getPose().getPosition().getX())
                .y(poseStamped.getPose().getPosition().getY())
                .z(poseStamped.getPose().getPosition().getZ())
                .yaw(Transformations.quaternionToEulerAngle(poseStamped.getPose().getOrientation()).angleZ())
                .build();
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
