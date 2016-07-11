package services;

import control.dto.InertialFrameVelocity;
import control.dto.Pose;

/**
 * @author Hoang Tung Dinh
 */
public interface VelocityService {
    /**
     * Sends a velocity message to the drone. The velocity includes four components, including three velocity components
     * in the x-y-z coordinate and one velocity component for the yaw. This method requires the velocity in the
     * inertial frame and the current pose of the drone. The pose is to transform the velocity in the inertial frame
     * to the velocity in the body frame because ome drones (for example, Bebop and ArDrone) require the velocity in
     * the body frame.
     *
     * @param inertialFrameVelocity The desired velocity in the inertial frame
     * @param pose                  The current pose of the drone
     */
    void sendVelocityMessage(InertialFrameVelocity inertialFrameVelocity, Pose pose);
}
