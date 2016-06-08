package simulation.trajectory;

import control.Trajectory2d;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class CircleTrajectory2DTest {
    private Trajectory2d highFrequencyCircle;
    private Trajectory2d lowFrequencyCircle;
    private final double lowFreq = 1 / 10;
    private final double highFreq = 1.5;
    private final double radius = 0.1;

    @Before
    public void setUp() throws Exception {
        highFrequencyCircle = new CircleTrajectory2D(radius, highFreq, false);
        lowFrequencyCircle = new CircleTrajectory2D(radius, lowFreq, false);

    }

    @Test
    public void getTrajectoryLinearX() throws Exception {
        for (double i = 0; i < 3;
             i += 0.66) {
            Assert.assertEquals(radius,
                    highFrequencyCircle.getTrajectoryLinearAbscissa()
                            .getDesiredPosition(i), 0.01);
        }

        for (double i = 0; i < 30;
             i += 10) {
            Assert.assertEquals(radius,
                    lowFrequencyCircle.getTrajectoryLinearAbscissa()
                            .getDesiredPosition(i), 0.01);
        }

        for (double i = 0; i < 30;
             i += 2) {

            Assert.assertTrue(
                    Math.abs(highFrequencyCircle.getTrajectoryLinearAbscissa()
                            .getDesiredVelocity(i)) < 1);
        }

        for (double i = 0; i < 30;
             i += 2) {

            Assert.assertTrue(
                    Math.abs(lowFrequencyCircle.getTrajectoryLinearAbscissa()
                            .getDesiredVelocity(i)) < 1);
        }
    }

    @Test
    public void getTrajectoryLinearY() throws Exception {
        for (double i = 0; i < 1;
             i += 0.33) {
            Assert.assertEquals(0,
                    highFrequencyCircle.getTrajectoryLinearOrdinate()
                            .getDesiredPosition(i), 0.01);
        }

        for (double i = 0; i < 30;
             i += 10) {
            Assert.assertEquals(0,
                    lowFrequencyCircle.getTrajectoryLinearOrdinate()
                            .getDesiredPosition(i), 0.01);
        }

        for (double i = 0; i < 30;
             i += 2) {

            Assert.assertTrue(
                    Math.abs(highFrequencyCircle.getTrajectoryLinearOrdinate()
                            .getDesiredVelocity(i)) < 1);
        }

        for (double i = 0; i < 30;
             i += 2) {

            Assert.assertTrue(
                    Math.abs(lowFrequencyCircle.getTrajectoryLinearOrdinate()
                            .getDesiredVelocity(i)) < 1);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithHighThanAllowedSpeedRate() {
        Trajectory2d target = new CircleTrajectory2D(5, 1, true);
    }

}