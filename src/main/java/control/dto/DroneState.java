package control.dto;

import com.google.auto.value.AutoValue;

/**
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class DroneState {
    protected DroneState() {}

    public static DroneState create(Pose pose, InertialFrameVelocity inertialFrameVelocity) {
        return new AutoValue_DroneState(pose, inertialFrameVelocity);
    }

    public abstract Pose pose();

    public abstract InertialFrameVelocity inertialFrameVelocity();
}
