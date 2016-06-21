package applications.simulations.trajectory;

import com.google.common.collect.Lists;
import control.Trajectory4d;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static applications.simulations.trajectory.TestUtils.assertBounds;

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
                .newCircleTrajectory4D(origin, radius, frequency,
                        0);
        //TODO test this as well.
        targetPlaneShift = Trajectories
                .newCircleTrajectory4D(origin, radius, frequency,
                        planeshift);

    }

    @Test
    public void getTrajectoryLinearXTestBounds() throws Exception {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(target.getTrajectoryLinearX().getDesiredPosition(i / 10d));
        }
        assertBounds(l, origin.getX() - radius, origin.getX() + radius);
    }

    @Test
    public void getTrajectoryLinearYTestBounds() throws Exception {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(target.getTrajectoryLinearY()
                    .getDesiredPosition(i / 10d));
        }
        assertBounds(l, origin.getY() - radius, origin.getY() + radius);
    }

    @Test
    public void getTrajectoryLinearZTestBounds() throws Exception {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(target.getTrajectoryLinearZ()
                    .getDesiredPosition(i / 10d));
        }
        assertBounds(l, origin.getZ() - StrictMath.tan(planeshift) * radius,
                origin.getZ() + StrictMath.tan(planeshift) * radius);
    }

    @Test
    public void getTrajectoryAngularZTestBounds() throws Exception {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(target.getTrajectoryLinearZ()
                    .getDesiredPosition(i / 10d));
        }
        assertBounds(l, 0,
                Math.PI * 2);
    }

}