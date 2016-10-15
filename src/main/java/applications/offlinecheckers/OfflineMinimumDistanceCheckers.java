package applications.offlinecheckers;

import com.google.common.base.MoreObjects;
import control.FiniteTrajectory4d;
import control.dto.Pose;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/** @author Hoang Tung Dinh */
public final class OfflineMinimumDistanceCheckers {
  private static final double DELTA_TIME = 0.001;

  private OfflineMinimumDistanceCheckers() {}

  /**
   * Checks whether there is any violation of the minimum distance constraint among violation
   * trajectories. This method will returns the first violation if there is at least one collision,
   * or return absent if there is no violation.
   *
   * @param trajectories the collection of all trajectories to be checked
   * @param minimumDistance the minimum distance between two trajectories
   * @return the first violation if there is at least on violation or absent if there is no
   *     violation
   */
  public static Optional<Violation> checkMinimum3dDistanceConstraint(
      Collection<FiniteTrajectory4d> trajectories, double minimumDistance) {
    final Queue<FiniteTrajectory4d> trajectoryList = new LinkedList<>(trajectories);

    while (!trajectoryList.isEmpty()) {
      final FiniteTrajectory4d trajectory = trajectoryList.remove();
      final double duration = trajectory.getTrajectoryDuration();

      for (final FiniteTrajectory4d otherTrajectory : trajectoryList) {
        double t = 0;
        while (t <= duration) {
          final Pose firstPose = Pose.createFromTrajectory(trajectory, t);
          final Pose secondPose = Pose.createFromTrajectory(otherTrajectory, t);
          final double distance = Pose.computeEuclideanDistance(firstPose, secondPose);
          if (distance < minimumDistance) {
            return Optional.of(Violation.create(trajectory, otherTrajectory, t));
          } else {
            t += DELTA_TIME;
          }
        }
      }
    }

    return Optional.empty();
  }

  /**
   * Checks whether there is any violation of the 2d x-y minimum distance constraint among violation
   * trajectories. This method will returns the first violation if there is at least one violation,
   * or return absent if there is no violation.
   *
   * @param trajectories the collection of all trajectories to be checked
   * @param minimumDistance the minimum distance between two trajectories
   * @return the first violation if there is at least on violation or absent if there is no
   *     violation
   */
  public static Optional<Violation> checkMinimum2dDistanceConstraint(
      Collection<FiniteTrajectory4d> trajectories, double minimumDistance) {
    final Queue<FiniteTrajectory4d> trajectoryList = new LinkedList<>(trajectories);

    while (!trajectoryList.isEmpty()) {
      final FiniteTrajectory4d trajectory = trajectoryList.remove();
      final double duration = trajectory.getTrajectoryDuration();

      for (final FiniteTrajectory4d otherTrajectory : trajectoryList) {
        double t = 0;
        while (t <= duration) {
          final Pose firstPose = Pose.createFromTrajectory(trajectory, t);
          final Pose secondPose = Pose.createFromTrajectory(otherTrajectory, t);
          final double distance = Pose.compute2dEuclideanDistance(firstPose, secondPose);
          if (distance < minimumDistance) {
            return Optional.of(Violation.create(trajectory, otherTrajectory, t));
          } else {
            t += DELTA_TIME;
          }
        }
      }
    }

    return Optional.empty();
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
