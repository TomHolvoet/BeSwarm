package control.dto;

import com.google.auto.value.AutoValue;
import geometry_msgs.PoseStamped;
import utils.math.Transformations;

/**
 * A value class which stores the pose of the drone.
 *
 * @author Hoang Tung Dinh
 * @author mhct
 */
@AutoValue
public abstract class Pose {
    /**
     * Default epsilon used for pose comparison
     */
    private static final double EPS = 0.001;

    Pose() {}

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

    public static Pose createZeroPose() {
    	return Pose.builder().x(0).y(0).z(0).yaw(0).build();
    }
    
    public static boolean areSamePoseWithinEps(Pose p1, Pose p2) {
        return StrictMath.abs(p1.x() - p2.x()) < EPS &&
                StrictMath.abs(p1.y() - p2.y()) < EPS &&
                StrictMath.abs(p1.z() - p2.z()) < EPS &&
                StrictMath.abs(p1.yaw() - p2.yaw()) < EPS;
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
