package applications.trajectory;

import applications.trajectory.points.Point3D;
import applications.trajectory.points.Point4D;
import com.google.auto.value.AutoValue;
import com.google.common.collect.Lists;
import control.FiniteTrajectory4d;
import control.Trajectory1d;
import control.Trajectory4d;
import org.junit.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/** @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be> */
public final class TestUtils {
  public static final double EPSILON = 0.001;
  public static final double DELTA = EPSILON;

  private TestUtils() {}

  public static void assertBounds(List<Double> results, double min, double max) {
    for (Double d : results) {
      Assert.assertTrue(Collections.min(results) + EPSILON >= min - EPSILON);
      Assert.assertTrue(Collections.max(results) - EPSILON <= max + EPSILON);
    }
  }

  public static void testPositionFrequencyRadiusRelation(
      double frequency, double radius, Trajectory1d target) {
    for (double i = 0; i < 30; i += 1 / frequency) {
      assertEquals(radius, target.getDesiredPosition(i), 0.01);
    }
  }

  public static void testVelocityFrequencyRadiusRelation(double frequency, Trajectory1d target) {
    for (double i = 0; i < 30; i += 1 / frequency) {
      assertEquals(0, getVelocity(target, i), 0.01);
    }
  }

  public static void testSpeedBounds(Trajectory1d target, double maxspeed) {
    for (double i = 0; i < 30; i += 2) {
      Assert.assertTrue(Math.abs(getVelocity(target, i)) < maxspeed);
    }
  }

  public static double getVelocityX(Trajectory4d trajectory, double t) {
    return (trajectory.getDesiredPositionX(t + DELTA) - trajectory.getDesiredPositionX(t)) / DELTA;
  }

  public static double getVelocityY(Trajectory4d trajectory, double t) {
    return (trajectory.getDesiredPositionY(t + DELTA) - trajectory.getDesiredPositionY(t)) / DELTA;
  }

  public static double getVelocityZ(Trajectory4d trajectory, double t) {
    return (trajectory.getDesiredPositionZ(t + DELTA) - trajectory.getDesiredPositionZ(t)) / DELTA;
  }

  public static double getAngularVelocity(Trajectory4d trajectory, double t) {
    return (trajectory.getDesiredAngleZ(t + DELTA) - trajectory.getDesiredAngleZ(t)) / DELTA;
  }

  public static double getVelocity(Trajectory1d trajectory, double t) {
    return (trajectory.getDesiredPosition(t + DELTA) - trajectory.getDesiredPosition(t)) / DELTA;
  }

  public static void testTrajectoryPos4D(Trajectory4d traj, double time, Point4D target) {
    assertEquals(target.getX(), traj.getDesiredPositionX(time), EPSILON);
    assertEquals(target.getY(), traj.getDesiredPositionY(time), EPSILON);
    assertEquals(target.getZ(), traj.getDesiredPositionZ(time), EPSILON);
    assertEquals(target.getAngle(), traj.getDesiredAngleZ(time), EPSILON);
  }

  public static void testTrajectoryCollisions(
      List<FiniteTrajectory4d> trajectories, double minimumDistance) {
    CollisionDetector detector = new CollisionDetector(trajectories, minimumDistance);
  }

  public static void testTrajectoryCollisions(List<FiniteTrajectory4d> trajectories) {
    CollisionDetector detector = new CollisionDetector(trajectories);
  }

  private static class CollisionDetector {
    private static final double DEFAULT_MINIMUM_DISTANCE = 1;
    private static final double DEFAULT_TIME_DELTA = 0.1;
    private final List<FiniteTrajectory4d> trajectories;
    private final double minimumDistance;

    public CollisionDetector(List<FiniteTrajectory4d> trajectories, double minimumDistance) {
      this.trajectories = Lists.newArrayList(trajectories);
      this.minimumDistance = minimumDistance;
    }

    public CollisionDetector(List<FiniteTrajectory4d> trajectories) {
      this(trajectories, DEFAULT_MINIMUM_DISTANCE);
    }

    public List<Collision> findCollisions() {
      List<Collision> collisions = Lists.newArrayList();
      double finalTimePoint = findLastTimePoint();
      for (double t = 0; t < finalTimePoint; t += DEFAULT_TIME_DELTA) {
        collisions.addAll(getCollisionsAtTime(t));
      }
      return collisions;
    }

    private Collection<Collision> getCollisionsAtTime(double t) {
      List<Collision> collT = Lists.newArrayList();
      for (int i = 0; i < trajectories.size(); i++) {
        for (int j = 0; j < trajectories.size(); j++) {
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
      if (Point3D.distance(firstPoint, secondPoint) < minimumDistance) {
        return true;
      }
      return false;
    }

    private double findLastTimePoint() {
      double maxTime = 0;
      for (FiniteTrajectory4d trajectory : trajectories) {
        if (trajectory.getTrajectoryDuration() > maxTime) {
          maxTime = trajectory.getTrajectoryDuration();
        }
      }
      return maxTime;
    }
  }

  @AutoValue
  public abstract static class Collision {
    public abstract double getTimePoint();

    public abstract Trajectory4d getFirstCollidingTrajectory();

    public abstract Trajectory4d getSecondCollidingTrajectory();

    public static Collision create(double time, Trajectory4d first, Trajectory4d second) {
      return new AutoValue_TestUtils_Collision(time, first, second);
    }
  }
}
