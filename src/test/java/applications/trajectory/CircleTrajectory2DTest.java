package applications.trajectory;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class CircleTrajectory2DTest extends Periodic2DTest {

    @Before
    public void setUp() throws Exception {
        highFrequencyCircle = new CircleTrajectory2D(
                radius, highFreq);
        lowFrequencyCircle = new CircleTrajectory2D(
                radius, lowFreq);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorTooHighSpeedRate() {
        new CircleTrajectory2D(
                1, 1);
    }
}
