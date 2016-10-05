package applications.offlinecheckers;

import control.FiniteTrajectory4d;
import control.dto.Pose;

import java.util.Collection;
import java.util.LinkedList;

/** @author Hoang Tung Dinh */
public final class OfflineCollisionChecker {
  private final double minimumDistance;
  private final Collection<FiniteTrajectory4d> trajectories;
  private static final double DELTA_TIME = 0.001;

  private OfflineCollisionChecker(
      double minimumDistance, Collection<FiniteTrajectory4d> trajectories) {
    this.minimumDistance = minimumDistance;
    this.trajectories = trajectories;
  }

  public static OfflineCollisionChecker create(
      double minimumDistance, Collection<FiniteTrajectory4d> trajectories) {
    return new OfflineCollisionChecker(minimumDistance, trajectories);
  }

  public String checkCollision() {
    final LinkedList<FiniteTrajectory4d> trajectoryList = new LinkedList<>(trajectories);

    while (!trajectoryList.isEmpty()) {
      final FiniteTrajectory4d trajectory = trajectoryList.remove();
      final double duration = trajectory.getTrajectoryDuration();

      for (final FiniteTrajectory4d otherTrajectory : trajectoryList) {
        double t = 0;
        while (t <= duration) {
          final Pose firstPose = Pose.createFromTrajectory(trajectory, t);
          final Pose secondPose = Pose.createFromTrajectory(otherTrajectory, t);
          final double distance = Pose.computeEucllideanDistance(firstPose, secondPose);
          if (distance < minimumDistance) {
            return String.format(
                "There is a collision at %f between trajectory %s and trajectory %s",
                t,
                trajectory,
                otherTrajectory);
          } else {
            t += DELTA_TIME;
          }
        }
      }
    }

    return "Yay! There is no collision.";
  }
}
