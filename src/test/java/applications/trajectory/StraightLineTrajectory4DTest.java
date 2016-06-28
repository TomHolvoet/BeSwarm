package applications.trajectory;

import control.Trajectory4d;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class StraightLineTrajectory4DTest {
    private Point4D before;
    private Point4D after;
    private Point4D after2;
    private Trajectory4d target;
    private Trajectory4d target2;

    private final double speed = 1;

    @Before
    public void setup() {
        before = Point4D.create(0, 0, 0, 0);
        after2 = Point4D.create(10, 10, 10, 0);
        after = Point4D.create(10, 0, 0, Math.PI / 2);
        target = Trajectories.newStraightLineTrajectory(before, after, speed);
        target2 = Trajectories.newStraightLineTrajectory(before, after2, speed);
        init();
    }

    private void init() {
        target.getDesiredPositionX(0);
        target.getDesiredPositionY(0);
        target.getDesiredPositionZ(0);
        target.getDesiredAngleZ(0);
        target2.getDesiredPositionX(0);
        target2.getDesiredPositionY(0);
        target2.getDesiredPositionZ(0);
        target2.getDesiredAngleZ(0);

    }

    @Test
    public void getTrajectoryLinearX() throws Exception {
        assertEquals(5, target.getDesiredPositionX(5),
                0);
        assertEquals(10, target.getDesiredPositionX(10),
                0);

        double t = 5;
        testPartialDistanceCovered(t);
        t = 10;
        testPartialDistanceCovered(t);
    }

    @Test
    public void getTrajectoryLinearXVelocity() {
        assertEquals(1, target.getDesiredVelocityX(5),
                0);
    }

    @Test
    public void getTrajectoryLinearXTestHoldAtEnd() throws Exception {
        target.getDesiredPositionX(15);
        assertEquals(10,
                target.getDesiredPositionX(15),
                0.01);
    }

    @Test
    public void getTrajectoryLinearY() throws Exception {
        assertEquals(0, target.getDesiredPositionY(5),
                0);
        assertEquals(0, target.getDesiredPositionY(10),
                0);

        double t = 5;
        double toCalc = 10d / Math.sqrt(300d) * t;
        assertEquals(toCalc,
                target2.getDesiredPositionY(t),
                0.01);
        t = 10;
        toCalc = 10d / Math.sqrt(300d) * t;
        assertEquals(toCalc,
                target2.getDesiredPositionY(t),
                0.01);
    }

    @Test
    public void getTrajectoryLinearYVelocity() {
        assertEquals(0, target.getDesiredVelocityY(5),
                0);
    }

    @Test
    public void getTrajectoryLinearZ() throws Exception {
        assertEquals(0, target.getDesiredPositionZ(5),
                0);
        assertEquals(0, target.getDesiredPositionZ(10),
                0);

        double t = 5;
        double toCalc = 10d / Math.sqrt(300d) * t;
        assertEquals(toCalc,
                target2.getDesiredPositionZ(t),
                0.01);
        t = 10;
        toCalc = 10d / Math.sqrt(300d) * t;
        assertEquals(toCalc,
                target2.getDesiredPositionZ(t),
                0.01);
    }

    @Test
    public void getTrajectoryLinearZVelocity() {
        assertEquals(0, target.getDesiredVelocityZ(5),
                0);
    }

    @Test
    public void getTrajectoryAngularZ() throws Exception {
        assertEquals(Math.PI / 4,
                target.getDesiredAngleZ(5),
                0);
        assertEquals(Math.PI / 2,
                target.getDesiredAngleZ(10),
                0);
    }

    @Test
    public void testTrajectoryProgression() throws Exception {
        double duration = 10492;
        target = Trajectories.newStraightLineTrajectory(before, after, speed);
        target2 = Trajectories.newStraightLineTrajectory(before, after2, speed);
        assertEquals(0, target.getDesiredPositionX(duration + 0),
                0);
        assertEquals(5, target.getDesiredPositionX(duration + 5),
                0);
        assertEquals(10, target.getDesiredPositionX(duration + 10),
                0);

        double t = 0;
        testPartialDistanceCovered(t);
        t = 5;
        testPartialDistanceCovered(t);
        t = 10;
        testPartialDistanceCovered(t);
    }

    private void testPartialDistanceCovered(double t) {
        double toCalc = 10d / Math.sqrt(300d) * t;
        assertEquals(toCalc,
                target2.getDesiredPositionX(t),
                0.01);
    }

}