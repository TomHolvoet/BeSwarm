package applications.trajectory;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class PendulumTrajectory2DTest extends Periodic2DTest {

    @Before
    public void setUp() throws Exception {
        highFrequencyCircle = new PendulumTrajectory2D(
                radius, highFreq);
        lowFrequencyCircle = new PendulumTrajectory2D(
                radius, lowFreq);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorTooHighSpeedRate() {
        new PendulumTrajectory2D(
                1, 1);
    }

}
