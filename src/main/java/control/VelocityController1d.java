package control;

/**
 * A velocity controller in one dimension.
 *
 * @author Hoang Tung Dinh
 */
public interface VelocityController1d {
  /**
   * Compute the next velocity (response) of the control loop.
   *
   * @param currentPosition the current position of the drone
   * @param currentVelocity the current velocity of the drone
   * @param currentTimeInSeconds the current time
   * @return the next velocity (response) of the drone
   * @see <a href="https://en.wikipedia.org/wiki/PID_controller">Equation</a>
   */
  double computeNextResponse(
      double currentPosition, double currentVelocity, double currentTimeInSeconds);
}
