package applications.trajectory;

import applications.trajectory.points.Point4D;
import com.google.common.collect.Lists;
import control.FiniteTrajectory4d;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static applications.trajectory.CorkscrewTrajectory4D.newCache;
import static applications.trajectory.TestUtils.EPSILON;
import static applications.trajectory.TestUtils.assertBounds;
import static org.junit.Assert.assertEquals;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class CorkscrewTrajectory4DTest {
    private final double speed = 1;
    private final double radius = 0.5;
    private final double frequency = 0.3;
    private final int phase = 0;
    private FiniteTrajectory4d trajectory;
    private final double zDistance = 10;
    private final double startDistance = 10;

    @Before
    public void setUp() throws Exception {
        this.trajectory = new CorkscrewTrajectory4D(Point4D.create(0, 0, startDistance, 0),
                Point4D.create(0, 0, startDistance + zDistance, 0),
                speed, radius, frequency, phase);
        initialize();
    }

    private void initialize() {
        trajectory.getDesiredPositionX(0);
        trajectory.getDesiredPositionY(0);
        trajectory.getDesiredPositionZ(0);
        trajectory.getDesiredAngleZ(0);
    }

    @Test
    public void testTrajectoryBoundsX() {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(trajectory.getDesiredPositionX(i / 10d));
        }
        assertBounds(l, 0 - radius, 0 + radius);
    }

    @Test
    public void testTrajectoryBoundsY() {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(trajectory.getDesiredPositionY(i / 10d));
        }
        assertBounds(l, 0 - radius, 0 + radius);
    }

    @Test
    public void testTrajectoryBoundsZ() {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(trajectory.getDesiredPositionZ(i / 10d));
        }
        assertBounds(l, startDistance, startDistance + zDistance);
    }

    @Test
    public void testTrajectoryZ() {
        double time = 5;
        assertEquals(startDistance + time, trajectory.getDesiredPositionZ(time), EPSILON);
    }

    @Test
    public void testTrajectoryBoundsZVelocity() {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < trajectory.getTrajectoryDuration() * 10; i++) {
            //            assertEquals(0.01 * i, trajectory.getDesiredPositionZ(i / 10d),
            // TestUtils.EPSILON);
            l.add(trajectory.getDesiredVelocityZ(i / 10d));
        }
        System.out.println(l);
        assertBounds(l, speed, speed);
    }

    @Test
    public void testAutoValueCache() {
        CorkscrewTrajectory4D.Point4DCache c = newCache(Point4D.create(2, 2, 2, 2),
                Point4D.create(5, 5, 5, 5), 1);
        assertEquals(2, c.getDestinationPoint().getX(), 0);
        assertEquals(5, c.getVelocityPoint().getX(), 0);
    }
}