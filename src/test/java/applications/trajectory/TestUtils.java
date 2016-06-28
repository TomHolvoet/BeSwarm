package applications.trajectory;

import control.Trajectory1d;
import control.Trajectory4d;
import org.junit.Assert;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public final class TestUtils {
    public static final double EPSILON = 0.001;

    private TestUtils() {
    }

    public static void assertBounds(List<Double> results, double min,
            double max) {
        for (Double d : results) {
            Assert.assertTrue(Collections.min(results) >= min);
            Assert.assertTrue(Collections.max(results) <= max);
        }
    }

    public static void testPositionFrequencyRadiusRelation(double frequency,
            double radius,
            Trajectory1d target) {
        for (double i = 0; i < 30;
             i += 1 / frequency) {
            assertEquals(radius,
                    target.getDesiredPosition(i), 0.01);
        }
    }

    public static void testVelocityFrequencyRadiusRelation(double frequency,
            Trajectory1d target) {
        for (double i = 0; i < 30;
             i += 1 / frequency) {
            assertEquals(0,
                    target.getDesiredVelocity(i), 0.01);
        }
    }

    public static void testSpeedBounds(Trajectory1d target, double maxspeed) {
        for (double i = 0; i < 30;
             i += 2) {
            Assert.assertTrue(
                    Math.abs(target
                            .getDesiredVelocity(i))
                            < maxspeed);
        }
    }

    public static void testTrajectoryPos4D(Trajectory4d traj, double time,
            Point4D target) {
        assertEquals(target.getX(),
                traj.getDesiredPositionX(time), EPSILON);
        assertEquals(target.getY(),
                traj.getDesiredPositionY(time), EPSILON);
        assertEquals(target.getZ(),
                traj.getDesiredPositionZ(time), EPSILON);
        assertEquals(target.getAngle(),
                traj.getDesiredAngleZ(time), EPSILON);
    }
}
