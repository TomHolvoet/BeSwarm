package applications.trajectory;

import applications.trajectory.points.Point3D;
import com.google.auto.value.AutoValue;
import com.google.common.collect.Lists;
import control.FiniteTrajectory4d;
import control.Trajectory4d;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be> */
public class CollisionDetector {
  static final double DEFAULT_MINIMUM_DISTANCE = 1;
  static final double DEFAULT_TIME_DELTA = 0.1;
  private final List<FiniteTrajectory4d> trajectories;
  private final double minimumDistance;

  public CollisionDetector(List<FiniteTrajectory4d> trajectories) {
    this(trajectories, applications.trajectory.CollisionDetector.DEFAULT_MINIMUM_DISTANCE);
  }

  public CollisionDetector(List<FiniteTrajectory4d> trajectories, double minimumDistance) {
    this.trajectories = Lists.newArrayList(trajectories);
    this.minimumDistance = minimumDistance;
  }

  public List<Collision> findCollisions() {
    List<Collision> collisions = Lists.newArrayList();
    double finalTimePoint =
        applications.trajectory.CollisionDetector.findLastTimePoint(trajectories);
    for (double t = 0;
        t < finalTimePoint;
        t += applications.trajectory.CollisionDetector.DEFAULT_TIME_DELTA) {
      collisions.addAll(getCollisionsAtTime(t));
    }
    return collisions;
  }

  static double findLastTimePoint(List<FiniteTrajectory4d> trajectories) {
    double maxTime = 0;
    for (FiniteTrajectory4d trajectory : trajectories) {
      if (trajectory.getTrajectoryDuration() > maxTime) {
        maxTime = trajectory.getTrajectoryDuration();
      }
    }
    return maxTime;
  }

  private Collection<Collision> getCollisionsAtTime(double t) {
    if (Math.abs(t - 38) < 0.001) {
      System.out.println(t);
    }
    List<Collision> collT = Lists.newArrayList();
    for (int i = 0; i < trajectories.size(); i++) {
      for (int j = i + 1; j < trajectories.size(); j++) {
        if (isCollision(t, trajectories.get(i), trajectories.get(j))) {
          collT.add(Collision.create(t, trajectories.get(i), trajectories.get(j)));
        }
      }
    }
    return collT;
  }

  private boolean isCollision(double t, FiniteTrajectory4d first, FiniteTrajectory4d second) {
    Point3D firstPoint =
        Point3D.create(
            first.getDesiredPositionX(t),
            first.getDesiredPositionY(t),
            first.getDesiredPositionZ(t));
    Point3D secondPoint =
        Point3D.create(
            second.getDesiredPositionX(t),
            second.getDesiredPositionY(t),
            second.getDesiredPositionZ(t));
    if (Point3D.distance(firstPoint, secondPoint) < minimumDistance - TestUtils.EPSILON) {
      return true;
    }
    return false;
  }

  List<Collision> findDangerouslyDisconnectedSegments() {
    return Collections.emptyList();
  }

  @AutoValue
  public abstract static class Collision {
    public static Collision create(double time, Trajectory4d first, Trajectory4d second) {
      return new AutoValue_CollisionDetector_Collision(time, first, second);
    }

    public abstract double getTimePoint();

    public abstract Trajectory4d getFirstCollidingTrajectory();

    public abstract Trajectory4d getSecondCollidingTrajectory();
  }
}
