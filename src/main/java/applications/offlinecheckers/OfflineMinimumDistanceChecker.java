package applications.offlinecheckers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import control.FiniteTrajectory4d;
import control.dto.Pose;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/** @author Hoang Tung Dinh */
public final class OfflineMinimumDistanceChecker {
  private final double minimumDistance;
  private final Collection<FiniteTrajectory4d> trajectories;
  private static final double DELTA_TIME = 0.001;

  private OfflineMinimumDistanceChecker(
      double minimumDistance, Collection<FiniteTrajectory4d> trajectories) {
    this.minimumDistance = minimumDistance;
    this.trajectories = trajectories;
  }

  public static OfflineMinimumDistanceChecker create(
      double minimumDistance, Collection<FiniteTrajectory4d> trajectories) {
    return new OfflineMinimumDistanceChecker(minimumDistance, trajectories);
  }

  /**
   * Checks whether there is any violation of the minimum distance constraint among violation
   * trajectories. This method will returns the first violation if there is at least one collision,
   * or return absent if there is no violation.
   *
   * @return the first collision if there is at least on violation or absent if there is no
   *     violation
   */
  public Optional<Violation> checkMinimumDistanceConstraint() {
    final Queue<FiniteTrajectory4d> trajectoryList = new LinkedList<>(trajectories);

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
            return Optional.of(Violation.create(trajectory, otherTrajectory, t));
          } else {
            t += DELTA_TIME;
          }
        }
      }
    }

    return Optional.absent();
  }

  public static final class Violation {
    private final FiniteTrajectory4d firstTrajectory;
    private final FiniteTrajectory4d secondTrajectory;
    private final double collisionTimeInSecs;

    private Violation(
        FiniteTrajectory4d firstTrajectory,
        FiniteTrajectory4d secondTrajectory,
        double collisionTimeInSecs) {
      this.firstTrajectory = firstTrajectory;
      this.secondTrajectory = secondTrajectory;
      this.collisionTimeInSecs = collisionTimeInSecs;
    }

    public static Violation create(
        FiniteTrajectory4d firstTrajectory,
        FiniteTrajectory4d secondTrajectory,
        double collisionTimeInSecs) {
      return new Violation(firstTrajectory, secondTrajectory, collisionTimeInSecs);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("firstTrajectory", firstTrajectory)
          .add("secondTrajectory", secondTrajectory)
          .add("collisionTimeInSecs", collisionTimeInSecs)
          .toString();
    }
  }
}
