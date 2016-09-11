package control;

import utils.math.EulerAngle;

/**
 * The one-dimensional PID controller to control angular velocity. Different from linear position,
 * angular position has discontinuity problem. Therefore, instead of calculating the position error
 * by simply subtract the current position to the desired position, we have to calculate the angle
 * distance between the two positions (which in range [-pi, pi]). Also, instead of trying to be
 * close to the desired angle (which could be out of range [-pi, pi]), this controller tries to make
 * the angle distance between two positions equal zero.
 *
 * <p>The pid equation of this controller is similar to the one of {@link LinearPidController1d}.
 * Therefore, this controller uses an internal {@link LinearPidController1d} and adapts the current
 * position so that the internal {@link LinearPidController1d} will try to minimize the angle
 * distance.
 *
 * @author Hoang Tung Dinh
 */
public final class AngularPidController1d implements VelocityController1d {
  private final LinearPidController1d linearPidController1d;
  private final Trajectory1d trajectory;

  private AngularPidController1d(PidParameters parameters, Trajectory1d trajectory) {
    linearPidController1d = LinearPidController1d.create(parameters, trajectory);
    this.trajectory = trajectory;
  }

  /**
   * Creates an {@link AngularPidController1d}'s instance.
   *
   * @param parameters the pid parameters for this pid controller
   * @param trajectory the trajectory that this pid controller will try to follow
   * @return an instance of this class
   */
  public static AngularPidController1d create(PidParameters parameters, Trajectory1d trajectory) {
    return new AngularPidController1d(parameters, trajectory);
  }

  @Override
  public double computeNextResponse(
      double currentPosition, double currentVelocity, double currentTimeInSeconds) {

    final double desiredPosition = trajectory.getDesiredPosition(currentTimeInSeconds);
    final double angularError = EulerAngle.computeAngleDistance(currentPosition, desiredPosition);
    final double adaptedCurrentPosition = desiredPosition - angularError;

    return linearPidController1d.computeNextResponse(
        adaptedCurrentPosition, currentVelocity, currentTimeInSeconds);
  }
}
