package applications.visualization;

import control.FiniteTrajectory4d;
import control.Trajectory4d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Logger for multiple trajectory. This class is for visualizing multiple trajectories over time.
 *
 * @author Hoang Tung Dinh
 */
final class MultiTrajectoryLogger {
  private static final Logger logger = LoggerFactory.getLogger(MultiTrajectoryLogger.class);

  private final List<FiniteTrajectory4d> trajectories;
  private final double durationInSeconds;

  private MultiTrajectoryLogger(List<FiniteTrajectory4d> trajectories, double durationInSeconds) {
    this.trajectories = trajectories;
    this.durationInSeconds = durationInSeconds;
  }

  public static MultiTrajectoryLogger create(
      List<FiniteTrajectory4d> trajectories, double durationInSeconds) {
    return new MultiTrajectoryLogger(trajectories, durationInSeconds);
  }

  public void startLogging() {
    for (int trajectoryIndex = 0; trajectoryIndex < trajectories.size(); trajectoryIndex++) {
      final Trajectory4d trajectory = trajectories.get(trajectoryIndex);
      double time = 0;
      while (time < durationInSeconds) {
        final double posX = trajectory.getDesiredPositionX(time);
        final double posY = trajectory.getDesiredPositionY(time);
        final double posZ = trajectory.getDesiredPositionZ(time);
        final double yaw = trajectory.getDesiredAngleZ(time);

        logger.info("{} {} {} {} {} {}", trajectoryIndex, time, posX, posY, posZ, yaw);
        time += 0.01;
      }
    }
  }
}
