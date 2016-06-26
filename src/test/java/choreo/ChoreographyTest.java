package choreo;

import applications.trajectory.Point4D;
import applications.trajectory.Trajectories;
import control.Trajectory4d;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class ChoreographyTest {

    private Choreography choreotarget;
    private double duration = 20d;
    private Trajectory4d pathTrajectory;
    private Trajectory4d holdTrajectory;
    private Point4D point;
    private double radius = 0.2d;
    private double frequency = 0.1d;

    @Before
    public void setup() {
        point = Point4D.create(5, 5, 5, 2);
        holdTrajectory = Trajectories.newHoldPositionTrajectory(
                point);
        pathTrajectory = Trajectories
                .newCircleTrajectory4D(Point4D.origin(), radius, frequency, 0);
        this.choreotarget = Choreography.builder()
                .withTrajectory(holdTrajectory).forTime(duration)
                .withTrajectory(pathTrajectory)
                .forTime(duration)
                .build();
    }

    @Test
    public void testTwoSegmentTrajectoryChoreo() {
        testTrajectoryPos4D(choreotarget, 2, Point4D.create(5, 5, 5, 2));
        //First invocation past duration still get's old point. all following
        // trigger change in segment for first call.
        choreotarget.getTrajectoryLinearX().getDesiredPosition(1d + duration);
        testTrajectoryPos4D(choreotarget, 10d + duration,
                Point4D.create(radius, 0, 0, 0));
    }

    public void testTrajectoryPos4D(Trajectory4d traj, double time,
            Point4D target) {
        assertEquals(target.getX(),
                traj.getTrajectoryLinearX().getDesiredPosition(time), 0);
        assertEquals(target.getY(),
                traj.getTrajectoryLinearY().getDesiredPosition(time), 0);
        assertEquals(target.getZ(),
                traj.getTrajectoryLinearZ().getDesiredPosition(time), 0);
        assertEquals(target.getAngle(),
                traj.getTrajectoryAngularZ().getDesiredPosition(time), 0);
    }

    @Test
    public void testComplexExample() {
        Trajectory4d first = Trajectories
                .newStraightLineTrajectory(Point4D.create(0, 0, 1.5, 0),
                        Point4D.create(5, 5, 5, 0), 0.6);
        Trajectory4d second = Trajectories
                .newCircleTrajectory4D(Point4D.create(4, 5, 5, 0), 1, 0.10,
                        Math.PI / 4);
        Trajectory4d third = Trajectories
                .newHoldPositionTrajectory(Point4D.create(1, 1, 2, 0));
        Choreography choreo = Choreography.builder().withTrajectory(first)
                .forTime(20)
                .withTrajectory(second).forTime(40).withTrajectory(third)
                .forTime(30).build();
        assertNotEquals(0, choreo.getTrajectoryLinearX().getDesiredVelocity(1));
        assertNotEquals(0, choreo.getTrajectoryLinearY().getDesiredVelocity(1));
        assertNotEquals(0, choreo.getTrajectoryLinearZ().getDesiredVelocity(1));
        assertEquals(0, choreo.getTrajectoryAngularZ().getDesiredVelocity(1),
                0);

    }
}