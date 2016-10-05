package applications.offlinecheckers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import control.FiniteTrajectory4d;
import control.dto.Pose;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

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

  /**
   * Checks whether there is any collision among multiple trajectories. This method will returns the
   * first collision if there is at least one collision, or return absent if there is no collision.
   *
   * @return the first collision if there is at least on collision or absent if there is no
   *     collision
   */
  public Optional<Collision> checkCollision() {
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
            return Optional.of(Collision.create(trajectory, otherTrajectory, t));
          } else {
            t += DELTA_TIME;
          }
        }
      }
    }

    return Optional.absent();
  }

  public static final class Collision {
    private final FiniteTrajectory4d firstTrajectory;
    private final FiniteTrajectory4d secondTrajectory;
    private final double collisionTimeInSecs;

    private Collision(
        FiniteTrajectory4d firstTrajectory,
        FiniteTrajectory4d secondTrajectory,
        double collisionTimeInSecs) {
      this.firstTrajectory = firstTrajectory;
      this.secondTrajectory = secondTrajectory;
      this.collisionTimeInSecs = collisionTimeInSecs;
    }

    public static Collision create(
        FiniteTrajectory4d firstTrajectory,
        FiniteTrajectory4d secondTrajectory,
        double collisionTimeInSecs) {
      return new Collision(firstTrajectory, secondTrajectory, collisionTimeInSecs);
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
