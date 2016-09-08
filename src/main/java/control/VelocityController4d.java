package control;

import control.dto.InertialFrameVelocity;
import control.dto.Pose;

/**
 * A velocity controller in four dimensions (x y z and yaw).
 *
 * @author Hoang Tung Dinh
 */
public interface VelocityController4d {
  /**
   * Compute the next velocity (response) of the control loop.
   *
   * @param currentPose the current pose of the drone
   * @param currentVelocity the current velocity of the drone
   * @param currentTimeInSeconds the current time which will be used to get the desired position of
   *     the drone
   * @return the next velocity (response) of the drone
   */
  InertialFrameVelocity computeNextResponse(
      Pose currentPose, InertialFrameVelocity currentVelocity, double currentTimeInSeconds);
}
