package services;

import control.dto.InertialFrameVelocity;
import control.dto.Pose;

/**
 * Represents velocity 4d service.
 *
 * @author Hoang Tung Dinh
 */
public interface Velocity4dService extends VelocityService {
    /**
     * Sends a velocity-4d message to the drone. The velocity includes four components, including
     * three velocity components in the x-y-z coordinate and one velocity component for the yaw.
     * This method requires the velocity in the inertial frame and the current pose of the drone.
     * The pose is to transform the velocity in the inertial frame to the velocity in the body
     * frame because ome drones (for example, Bebop and ArDrone) require the velocity in the body
     * frame.
     *
     * @param inertialFrameVelocity The desired velocity in the inertial frame
     * @param pose The current pose of the drone
     */
    void sendVelocity4dMessage(InertialFrameVelocity inertialFrameVelocity, Pose pose);
}
