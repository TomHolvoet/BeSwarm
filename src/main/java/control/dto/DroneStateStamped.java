package control.dto;

import com.google.auto.value.AutoValue;

/**
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class DroneStateStamped {

    DroneStateStamped() {}

    public static DroneStateStamped create(Pose pose, InertialFrameVelocity inertialFrameVelocity,
            double timeStampInSeconds) {
        return new AutoValue_DroneStateStamped(pose, inertialFrameVelocity, timeStampInSeconds);
    }

    public abstract Pose pose();

    public abstract InertialFrameVelocity inertialFrameVelocity();

    public abstract double getTimeStampInSeconds();
}
