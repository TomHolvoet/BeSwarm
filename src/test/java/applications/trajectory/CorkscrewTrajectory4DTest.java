package applications.trajectory;

import applications.trajectory.points.Point3D;
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
    private final double x = 0;
    private final double y = 0;

    @Before
    public void setUp() throws Exception {
        this.trajectory = CorkscrewTrajectory4D.builder()
                .setOrigin(Point4D.create(x, y, startDistance, Math.PI))
                .setDestination(Point3D.create(x, y, startDistance + zDistance))
                .setSpeed(speed)
                .setRadius(radius).setFrequency(frequency).setPhase(phase)
                .build();
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
        assertBounds(l, x - radius, x + radius);
    }

    @Test
    public void testTrajectoryBoundsY() {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(trajectory.getDesiredPositionY(i / 10d));
        }
        assertBounds(l, y - radius, y + radius);
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
        assertBounds(l, speed, speed);
    }

    @Test
    public void testAutoValueCache() {
        CorkscrewTrajectory4D.Point4DCache c = newCache(Point4D.create(2, 2, 2, 2),
                Point4D.create(5, 5, 5, 5), 1);
        assertEquals(2, c.getDestinationPoint().getX(), 0);
        assertEquals(5, c.getVelocityPoint().getX(), 0);
    }

    @Test
    public void testDefault() {
        trajectory = CorkscrewTrajectory4D.builder().build();
        assertEquals(0, trajectory.getTrajectoryDuration(), 0);
    }

    @Test
    public void testAngularMovement() {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < trajectory.getTrajectoryDuration() * 10; i++) {
            //            assertEquals(0.01 * i, trajectory.getDesiredPositionZ(i / 10d),
            // TestUtils.EPSILON);
            l.add(trajectory.getDesiredAngleZ(i / 10d));
        }
        assertBounds(l, Math.PI, Math.PI);
    }

    @Test
    public void testTrajectoryVelocityBoundsSimple() {
        testVelocity(this.trajectory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTrajectoryVelocityBoundsComplexTrajectory1() {
        this.trajectory = CorkscrewTrajectory4D.builder()
                .setOrigin(Point4D.create(x, y, startDistance, Math.PI))
                .setDestination(Point3D.create(x, y + 15, startDistance + zDistance))
                .setSpeed(speed)
                .setRadius(radius).setFrequency(frequency).setPhase(phase)
                .build();
        testVelocity(this.trajectory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTrajectoryVelocityBoundsComplexTrajectory2() {
        this.trajectory = CorkscrewTrajectory4D.builder()
                .setOrigin(Point4D.create(0, 0, 0, Math.PI))
                .setDestination(Point3D.create(15, 15, 15))
                .setSpeed(speed)
                .setRadius(radius).setFrequency(frequency).setPhase(phase)
                .build();
        testVelocity(this.trajectory);
    }

    @Test
    public void testTrajectoryVelocityBoundsAngle() {
        List<Double> l = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            l.add(trajectory.getDesiredAngularVelocityZ(i / 10d));
        }
        assertBounds(l, 0, 0);
    }

    private void testVelocity(FiniteTrajectory4d trajectory) {
        List<Double> lx = Lists.newArrayList();
        List<Double> ly = Lists.newArrayList();
        List<Double> lz = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            lx.add(trajectory.getDesiredVelocityX(i / 10d));
            ly.add(trajectory.getDesiredVelocityY(i / 10d));
            lz.add(trajectory.getDesiredVelocityZ(i / 10d));

        }
        assertBounds(lx, -1, 1);
        assertBounds(ly, -1, 1);
        assertBounds(lz, -1, 1);
    }
}