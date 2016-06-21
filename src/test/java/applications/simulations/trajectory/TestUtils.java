package applications.simulations.trajectory;

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
}
