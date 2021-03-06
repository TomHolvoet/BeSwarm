package control;

/**
 * The one-dimensional PID controller to control linear velocity.
 *
 * @author Hoang Tung Dinh
 * @see <a href="https://en.wikipedia.org/wiki/PID_controller">Equation</a>
 */
public final class LinearPidController1d implements VelocityController1d {

  private static final double DELTA_TIME_IN_SECOND = 0.1;
  private final PidParameters parameters;
  private final Trajectory1d trajectory;
  private double lastTimeInSeconds = -1;
  // initialized to 0
  private double accumulatedError;

  private LinearPidController1d(PidParameters parameters, Trajectory1d trajectory) {
    this.parameters = parameters;
    this.trajectory = trajectory;
  }

  /**
   * Creates a {@link LinearPidController1d} instance.
   *
   * @param parameters the pid parameters for this pid controller
   * @param trajectory the trajectory that this pid controller will try to follow
   * @return an instance of this class
   */
  public static LinearPidController1d create(PidParameters parameters, Trajectory1d trajectory) {
    return new LinearPidController1d(parameters, trajectory);
  }

  private static double getDesiredVelocity(Trajectory1d trajectory, double desiredTimeInSeconds) {
    final double firstPoint = trajectory.getDesiredPosition(desiredTimeInSeconds);
    final double secondPoint =
        trajectory.getDesiredPosition(desiredTimeInSeconds + DELTA_TIME_IN_SECOND);
    return (secondPoint - firstPoint) / DELTA_TIME_IN_SECOND;
  }

  @Override
  public double computeNextResponse(
      double currentPosition, double currentVelocity, double currentTimeInSeconds) {
    final double desiredTimeInSeconds = currentTimeInSeconds + parameters.lagTimeInSeconds();
    final double error = trajectory.getDesiredPosition(desiredTimeInSeconds) - currentPosition;

    updateAccumulatedError(desiredTimeInSeconds, error);

    final double pTerm = parameters.kp() * error;
    final double dTerm =
        parameters.kd() * (getDesiredVelocity(trajectory, desiredTimeInSeconds) - currentVelocity);
    final double iTerm = parameters.ki() * accumulatedError;

    double outVelocity = pTerm + dTerm + iTerm;

    if (outVelocity > parameters.maxVelocity()) {
      outVelocity = parameters.maxVelocity();
    } else if (outVelocity < parameters.minVelocity()) {
      outVelocity = parameters.minVelocity();
    }

    return outVelocity;
  }

  private void updateAccumulatedError(double currentTimeInSeconds, double error) {
    final double dtInSeconds;
    if (lastTimeInSeconds < 0) {
      lastTimeInSeconds = currentTimeInSeconds;
      dtInSeconds = 0;
    } else {
      dtInSeconds = currentTimeInSeconds - lastTimeInSeconds;
      lastTimeInSeconds = currentTimeInSeconds;
    }

    accumulatedError += error * dtInSeconds;

    if (accumulatedError > parameters.maxIntegralError()) {
      accumulatedError = parameters.maxIntegralError();
    } else if (accumulatedError < parameters.minIntegralError()) {
      accumulatedError = parameters.minIntegralError();
    }
  }
}
