package applications.trajectory;

import control.Trajectory1d;
import org.junit.Assert;

import java.util.Collections;
import java.util.List;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public final class TestUtils {
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
            Assert.assertEquals(radius,
                    target.getDesiredPosition(i), 0.01);
        }
    }

    public static void testVelocityFrequencyRadiusRelation(double frequency,
            Trajectory1d target) {
        for (double i = 0; i < 30;
             i += 1 / frequency) {
            Assert.assertEquals(0,
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
}
