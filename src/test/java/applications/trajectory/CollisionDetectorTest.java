package applications.trajectory;

import applications.trajectory.points.Point3D;
import applications.trajectory.points.Point4D;
import choreo.Choreography;
import com.google.common.collect.Lists;
import control.FiniteTrajectory4d;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static applications.trajectory.CollisionDetector.Collision;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/** @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be> */
public class CollisionDetectorTest {

  private FiniteTrajectory4d holdPos;
  private FiniteTrajectory4d lin1;
  private FiniteTrajectory4d circ1;
  private FiniteTrajectory4d circ1_phase;
  private FiniteTrajectory4d circ2;

  @Before
  public void setup() {
    this.holdPos =
        Choreography.builder()
            .withTrajectory(Trajectories.newHoldPositionTrajectory(Point4D.create(5, 0, 5, 0)))
            .forTime(10)
            .build();
    this.lin1 =
        Trajectories.newStraightLineTrajectory(
            Point4D.create(0, 0, 5, 0), Point4D.create(5, 0, 5, 0), 0.5);
    double freq = 0.1;
    this.circ1 =
        Choreography.builder()
            .withTrajectory(
                Trajectories.circleTrajectoryBuilder()
                    .setRadius(1)
                    .setLocation(Point3D.create(1, 1, 5))
                    .setFrequency(freq)
                    .build())
            .forTime(100)
            .build();
    this.circ1_phase =
        Choreography.builder()
            .withTrajectory(
                Trajectories.circleTrajectoryBuilder()
                    .setRadius(1)
                    .setLocation(Point3D.create(1, 1, 5))
                    .setFrequency(freq)
                    .setPhase(Math.PI)
                    .build())
            .forTime(100)
            .build();
    this.circ2 =
        Choreography.builder()
            .withTrajectory(
                Trajectories.circleTrajectoryBuilder()
                    .setRadius(1)
                    .setLocation(Point3D.create(1, 1, 6))
                    .setFrequency(freq)
                    .build())
            .forTime(100)
            .build();
  }

  @Test
  public void testCollisionDetectAtPoint() {
    List<Collision> collisions =
        new CollisionDetector(Lists.newArrayList(holdPos, lin1), 1).findCollisions();
    assertTrue(isCollisionPresentFor(9.9, collisions));
    assertTrue(isCollisionPresentFor(8.5, collisions));
    assertTrue(isCollisionPresentFor(8.1, collisions));
    assertFalse(isCollisionPresentFor(8.0, collisions));
    assertFalse(isCollisionPresentFor(7.8, collisions));
  }

  private boolean isCollisionPresentFor(double timeT, List<CollisionDetector.Collision> coll) {
    for (Collision c : coll) {
      if (Math.abs(c.getTimePoint() - timeT) < TestUtils.EPSILON) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void testNoCollisionDetectedWPhaseShift() {
    List<Collision> collisions =
        new CollisionDetector(Lists.newArrayList(circ1, circ1_phase), 1).findCollisions();
    assertTrue(collisions.isEmpty());
  }

  @Test
  public void testNoCollisionDetectedWCoordShift() {
    List<Collision> collisions =
        new CollisionDetector(Lists.newArrayList(circ1, circ2), 1).findCollisions();
    assertTrue(collisions.isEmpty());
  }
}
