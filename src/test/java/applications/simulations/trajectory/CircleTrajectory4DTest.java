package applications.simulations.trajectory;

import com.google.common.collect.Lists;
import control.Trajectory4d;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class CircleTrajectory4DTest {

    private Trajectory4d target;
    private Trajectory4d targetPlaneShift;
    private Point4D origin = Point4D.create(0, 0, 5, 0);
    private double radius = 1d;
    private double frequency = 0.1;
    private double planeshift = Math.PI / 6d;

    @Before
    public void setup() {
        target = Trajectories
                .newPendulumSwingTrajectory(origin, radius, frequency,
                        0);
        //TODO test this as well.
        targetPlaneShift = Trajectories
                .newPendulumSwingTrajectory(origin, radius, frequency,
                        planeshift);

    }

    @Test
    public void getTrajectoryLinearX() throws Exception {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(target.getTrajectoryLinearX().getDesiredPosition(i / 10d));
        }
        assertBounds(l, origin.getX() - radius, origin.getX() + radius);
    }

    @Test
    public void getTrajectoryLinearY() throws Exception {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(target.getTrajectoryLinearY()
                    .getDesiredPosition(i / 10d));
        }
        assertBounds(l, origin.getY(), origin.getY());
    }

    @Test
    public void getTrajectoryLinearZ() throws Exception {

    }

    @Test
    public void getTrajectoryLinearZTestBounds() throws Exception {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(target.getTrajectoryLinearZ().getDesiredPosition(i / 10d));
        }
        assertBounds(l, origin.getZ() - radius, origin.getZ());
    }

    @Test
    public void getTrajectoryAngularZ() throws Exception {

    }

    public void assertBounds(List<Double> results, double min, double max) {
        for (Double d : results) {
            Assert.assertTrue(Collections.min(results) >= min);
            Assert.assertTrue(Collections.max(results) <= max);
        }
    }

}