package choreo;

import applications.trajectory.Point4D;
import applications.trajectory.Trajectories;
import control.Trajectory4d;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}