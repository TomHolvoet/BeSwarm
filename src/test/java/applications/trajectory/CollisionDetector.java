package applications.trajectory;

import applications.trajectory.points.Point3D;
import com.google.auto.value.AutoValue;
import com.google.common.collect.Lists;
import control.FiniteTrajectory4d;
import control.Trajectory4d;
import georegression.struct.line.LineParametric3D_F64;
import georegression.struct.point.Vector3D_F64;

import java.util.Collection;
import java.util.List;

import static georegression.metric.MiscOps.dot;

/** @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be> */
public class CollisionDetector {
  private static final double DEFAULT_MINIMUM_DISTANCE = 1;
  private static final double DEFAULT_TIME_DELTA = 0.1;
  private static final CollisionTester SINGLE_SAMPLE_DISTANCE_BASED_COLLISION =
      new CollisionTester() {

        @Override
        public boolean isCollision(
            double t, FiniteTrajectory4d first, FiniteTrajectory4d second, double minimumDistance) {
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
      };
  private static final CollisionTester TWO_SAMPLE_LINE_DISTANCE_BASED_COLLISION =
      new CollisionTester() {

        @Override
        public boolean isCollision(
            double t, FiniteTrajectory4d first, FiniteTrajectory4d second, double minimumDistance) {
          Point3D firstPointT1 =
              Point3D.create(
                  first.getDesiredPositionX(t),
                  first.getDesiredPositionY(t),
                  first.getDesiredPositionZ(t));
          Point3D secondPointT1 =
              Point3D.create(
                  first.getDesiredPositionX(t + DEFAULT_TIME_DELTA),
                  first.getDesiredPositionY(t + DEFAULT_TIME_DELTA),
                  first.getDesiredPositionZ(t + DEFAULT_TIME_DELTA));
          Point3D firstPointT2 =
              Point3D.create(
                  second.getDesiredPositionX(t),
                  second.getDesiredPositionY(t),
                  second.getDesiredPositionZ(t));
          Point3D secondPointT2 =
              Point3D.create(
                  second.getDesiredPositionX(t + DEFAULT_TIME_DELTA),
                  second.getDesiredPositionY(t + DEFAULT_TIME_DELTA),
                  second.getDesiredPositionZ(t + DEFAULT_TIME_DELTA));

          LineParametric3D_F64 firstSeg =
              new LineParametric3D_F64(
                  firstPointT1.getX(),
                  firstPointT1.getY(),
                  firstPointT1.getZ(),
                  secondPointT1.getX() - firstPointT1.getX(),
                  secondPointT1.getY() - firstPointT1.getY(),
                  secondPointT1.getZ() - firstPointT1.getZ());
          LineParametric3D_F64 secondSeg =
              new LineParametric3D_F64(
                  firstPointT2.getX(),
                  firstPointT2.getY(),
                  firstPointT2.getZ(),
                  secondPointT2.getX() - firstPointT2.getX(),
                  secondPointT2.getY() - firstPointT2.getY(),
                  secondPointT2.getZ() - firstPointT2.getZ());

          double distance = distance(firstSeg, secondSeg);

          if (distance < minimumDistance - TestUtils.EPSILON) {
            return true;
          }
          return false;
        }
      };

  //implementation from http://geomalgorithms.com/a07-_distance.html#dist3D_Segment_to_Segment()
  private static double distance(LineParametric3D_F64 firstSeg, LineParametric3D_F64 secondSeg) {
    Vector3D_F64 u = firstSeg.getSlope();
    Vector3D_F64 v = secondSeg.getSlope();
    Vector3D_F64 w = new Vector3D_F64();
    w.minus(firstSeg.getPoint(), secondSeg.getPoint());
    double a = dot(u, u);
    double b = dot(u, v);
    double c = dot(v, v);
    double d = dot(u, w);
    double e = dot(v, w);
    double D = a * c - b * b;
    double sc, sN, sD = D;
    double tc, tN, tD = D;

    if (D < TestUtils.EPSILON) {
      sN = 0D;
      sD = 1D;
      tN = e;
      tD = c;
    } else {
      sN = (b * e - c * d);
      tN = (a * e - b * d);
      if (sN < 0D) {
        sN = 0D;
        tN = e;
        tD = c;
      } else if (sN > sD) {
        sN = sD;
        tN = e + b;
        tD = c;
      }
    }

    if (tN < 0D) {
      tN = 0D;
      if (-d < 0D) {
        sN = 0D;
      } else if (-d > a) {
        sN = sD;
      } else {
        sN = -d;
        sD = a;
      }
    } else if (tN > tD) {
      tN = tD;
      if ((-d + b) < 0D) {
        sN = 0D;
      } else if ((-d + b > a)) {
        sN = sD;
      } else {
        sN = (-d + b);
        sD = a;
      }
    }
    sc = Math.abs(sN) < TestUtils.EPSILON ? 0.0 : sN / sD;
    tc = Math.abs(tN) < TestUtils.EPSILON ? 0.0 : tN / tD;

    Vector3D_F64 scU = u.copy();
    Vector3D_F64 tcV = v.copy();

    scU.scale(sc);
    tcV.scale(-tc);

    Vector3D_F64 scu_tcvdiff = scU.plus(tcV);
    Vector3D_F64 dP = w.plus(scu_tcvdiff);

    return dP.norm();
  }

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
    return sampleForCollisions(SINGLE_SAMPLE_DISTANCE_BASED_COLLISION);
  }

  private List<Collision> sampleForCollisions(CollisionTester tester) {
    List<Collision> collisions = Lists.newArrayList();
    double finalTimePoint =
        applications.trajectory.CollisionDetector.findLastTimePoint(trajectories);
    for (double t = 0;
        t < finalTimePoint;
        t += applications.trajectory.CollisionDetector.DEFAULT_TIME_DELTA) {
      collisions.addAll(getCollisionsAtTime(t, tester));
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

  private Collection<Collision> getCollisionsAtTime(double t, CollisionTester tester) {
    if (Math.abs(t - 4.9) < 0.001) {
      System.out.println(t);
    }
    List<Collision> collT = Lists.newArrayList();
    for (int i = 0; i < trajectories.size(); i++) {
      for (int j = i + 1; j < trajectories.size(); j++) {
        if (tester.isCollision(t, trajectories.get(i), trajectories.get(j), minimumDistance)) {
          collT.add(Collision.create(t, trajectories.get(i), trajectories.get(j)));
        }
      }
    }
    return collT;
  }

  public List<Collision> findDangerouslyDisconnectedSegments() {
    return sampleForCollisions(TWO_SAMPLE_LINE_DISTANCE_BASED_COLLISION);
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

  private interface CollisionTester {
    /**
     * @param t The time point to sample.
     * @param first The first trajectory.
     * @param second The second trajectory.
     * @param minimumDistance The minimum allowed distance between two trajectories.
     * @return true if a collision occurs.
     */
    boolean isCollision(
        double t, FiniteTrajectory4d first, FiniteTrajectory4d second, double minimumDistance);
  }
}
