package simulation.trajectory;

import com.google.common.collect.Lists;
import control.Trajectory2d;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
@RunWith(value = Parameterized.class)
public class PeriodicTrajectory2DTest {
    private Trajectory2d highFrequencyCircle;
    private Trajectory2d lowFrequencyCircle;
    private final double lowFreq = 1 / 10;
    private final double highFreq = 1.5;
    private final double radius = 0.065;

    @Before
    public void setUp() throws Exception {
        //        highFrequencyCircle = new CircleTrajectory2D(radius,
        // highFreq, false);
        //        lowFrequencyCircle = new CircleTrajectory2D(radius,
        // lowFreq, false);

    }

    public PeriodicTrajectory2DTest(Class cl) {
        Class[] cArg = new Class[2];
        cArg[0] = double.class;
        cArg[1] = double.class;
        try {
            highFrequencyCircle = (Trajectory2d) cl.getDeclaredConstructor(cArg)
                    .newInstance(radius, highFreq);
            lowFrequencyCircle = (Trajectory2d) cl.getDeclaredConstructor(cArg)
                    .newInstance(radius, lowFreq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        highFrequencyCircle = new CircleTrajectory2D(radius, highFreq,
        //                false);
        //        lowFrequencyCircle = new CircleTrajectory2D(radius,
        // lowFreq, false);
    }

    @Parameterized.Parameters
    public static Collection<? extends Class> getParams() {
        return Lists.newArrayList(PendulumTrajectory2D.class,
                CircleTrajectory2D.class);
    }

    @Test
    public void getTrajectoryLinearAbscissa() throws Exception {
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
                            .getDesiredVelocity(i))
                            < PeriodicTrajectory.MAX_ABSOLUTE_SPEED);
        }

        for (double i = 0; i < 30;
             i += 2) {

            Assert.assertTrue(
                    Math.abs(lowFrequencyCircle.getTrajectoryLinearAbscissa()
                            .getDesiredVelocity(i))
                            < PeriodicTrajectory.MAX_ABSOLUTE_SPEED);
        }
    }

    @Test
    public void getTrajectoryLinearOrdinate() throws Exception {
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
                            .getDesiredVelocity(i))
                            < PeriodicTrajectory.MAX_ABSOLUTE_SPEED);
        }

        for (double i = 0; i < 30;
             i += 2) {

            Assert.assertTrue(
                    Math.abs(lowFrequencyCircle.getTrajectoryLinearOrdinate()
                            .getDesiredVelocity(i))
                            < PeriodicTrajectory.MAX_ABSOLUTE_SPEED);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithHighThanAllowedSpeedRate() {
        Trajectory2d target = new CircleTrajectory2D(5, 1, true);
    }

}