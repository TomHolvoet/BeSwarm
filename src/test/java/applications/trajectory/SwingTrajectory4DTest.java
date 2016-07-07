package applications.trajectory;

import applications.trajectory.points.Point4D;
import com.google.common.collect.Lists;
import control.Trajectory4d;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static applications.trajectory.TestUtils.assertBounds;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class SwingTrajectory4DTest {

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
    public void getTrajectoryLinearXTestBounds() throws Exception {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(target.getDesiredPositionX(i / 10d));
        }
        assertBounds(l, origin.getX() - radius, origin.getX() + radius);
    }

    @Test
    public void getTrajectoryLinearYTestBounds() throws Exception {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(target.getDesiredPositionY(i / 10d));
        }
        assertBounds(l, origin.getY(), origin.getY());
    }

    @Test
    public void getTrajectoryLinearZTestBounds() throws Exception {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(target.getDesiredPositionZ(i / 10d));
        }
        assertBounds(l, origin.getZ() - radius, origin.getZ());
    }

    @Test
    public void getTrajectoryAngularZTestBounds() throws Exception {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(target.getDesiredPositionZ(i / 10d));
        }
        assertBounds(l, 0,
                Math.PI * 2);
    }

}