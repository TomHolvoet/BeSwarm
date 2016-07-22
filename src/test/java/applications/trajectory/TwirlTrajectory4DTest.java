package applications.trajectory;

import applications.trajectory.points.Point4D;
import com.google.common.collect.Lists;
import control.FiniteTrajectory4d;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static applications.trajectory.TestUtils.assertBounds;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class TwirlTrajectory4DTest {
    private final double speed = 1;
    private final double radius = 0.5;
    private final double frequency = 0.3;
    private final int phase = 0;
    private FiniteTrajectory4d trajectory;
    private final double zDistance = 10;

    @Before
    public void setUp() throws Exception {
        this.trajectory = new TwirlTrajectory4D(Point4D.origin(),
                Point4D.create(0, 0, zDistance, 0),
                speed, radius, frequency, phase);
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
            //            assertEquals(0.01 * i, trajectory.getDesiredPositionZ(i / 10d),
            // TestUtils.EPSILON);
            l.add(trajectory.getDesiredPositionZ(i / 10d));
        }
        System.out.println(l);
        assertBounds(l, 0, zDistance);
    }

}