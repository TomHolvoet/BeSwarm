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
        target = new StraightLineTrajectory4D(before, after, speed);
        target2 = new StraightLineTrajectory4D(before, after2, speed);
        init();
    }

    private void init() {
        target.getTrajectoryLinearX().getDesiredPosition(0);
        target.getTrajectoryLinearY().getDesiredPosition(0);
        target.getTrajectoryLinearZ().getDesiredPosition(0);
        target.getTrajectoryAngularZ().getDesiredPosition(0);
        target2.getTrajectoryLinearX().getDesiredPosition(0);
        target2.getTrajectoryLinearY().getDesiredPosition(0);
        target2.getTrajectoryLinearZ().getDesiredPosition(0);
        target2.getTrajectoryAngularZ().getDesiredPosition(0);

    }

    @Test
    public void getTrajectoryLinearX() throws Exception {
        assertEquals(5, target.getTrajectoryLinearX().getDesiredPosition(5),
                0);
        assertEquals(10, target.getTrajectoryLinearX().getDesiredPosition(10),
                0);

        assertEquals(1, target.getTrajectoryLinearX().getDesiredVelocity(5),
                0);

        double t = 5;
        double toCalc = 10d / Math.sqrt(300d) * t;
        assertEquals(toCalc,
                target2.getTrajectoryLinearX().getDesiredPosition(t),
                0.01);
        t = 10;
        toCalc = (10d / Math.sqrt(300)) * t;
        assertEquals(toCalc,
                target2.getTrajectoryLinearX().getDesiredPosition(t),
                0.01);

    }

    @Test
    public void getTrajectoryLinearXTestHoldAtEnd() throws Exception {
        target.getTrajectoryLinearX().getDesiredPosition(15);
        assertEquals(10,
                target.getTrajectoryLinearX().getDesiredPosition(15),
                0.01);
    }

    @Test
    public void getTrajectoryLinearY() throws Exception {
        assertEquals(0, target.getTrajectoryLinearY().getDesiredPosition(5),
                0);
        assertEquals(0, target.getTrajectoryLinearY().getDesiredPosition(10),
                0);
        assertEquals(1, target.getTrajectoryLinearY().getDesiredVelocity(5),
                0);

        double t = 5;
        double toCalc = 10d / Math.sqrt(300d) * t;
        assertEquals(toCalc,
                target2.getTrajectoryLinearY().getDesiredPosition(t),
                0.01);
        t = 10;
        toCalc = 10d / Math.sqrt(300d) * t;
        assertEquals(toCalc,
                target2.getTrajectoryLinearY().getDesiredPosition(t),
                0.01);
    }

    @Test
    public void getTrajectoryLinearZ() throws Exception {
        assertEquals(0, target.getTrajectoryLinearZ().getDesiredPosition(5),
                0);
        assertEquals(0, target.getTrajectoryLinearZ().getDesiredPosition(10),
                0);
        assertEquals(1, target.getTrajectoryLinearZ().getDesiredVelocity(5),
                0);

        double t = 5;
        double toCalc = 10d / Math.sqrt(300d) * t;
        assertEquals(toCalc,
                target2.getTrajectoryLinearZ().getDesiredPosition(t),
                0.01);
        t = 10;
        toCalc = 10d / Math.sqrt(300d) * t;
        assertEquals(toCalc,
                target2.getTrajectoryLinearZ().getDesiredPosition(t),
                0.01);
    }

    @Test
    public void getTrajectoryAngularZ() throws Exception {
        assertEquals(Math.PI / 4,
                target.getTrajectoryAngularZ().getDesiredPosition(5),
                0);
        assertEquals(Math.PI / 2,
                target.getTrajectoryAngularZ().getDesiredPosition(10),
                0);
    }

}