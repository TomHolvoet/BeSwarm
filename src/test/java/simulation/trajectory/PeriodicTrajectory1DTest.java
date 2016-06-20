package simulation.trajectory;

import com.google.common.collect.Lists;
import control.Trajectory1d;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import static org.junit.Assert.fail;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
@RunWith(value = Parameterized.class)
public class PeriodicTrajectory1DTest {
    private Trajectory1d highFrequencyCircle;
    private Trajectory1d lowFrequencyCircle;
    private final double lowFreq = 1 / 10;
    private final double highFreq = 1.5;
    private final double radius = 0.065;
    private final double phase = 0;
    private Class cl;

    public PeriodicTrajectory1DTest(Class cl) {
        this.cl = cl;
        Class[] cArg = new Class[3];
        cArg[0] = double.class;
        cArg[1] = double.class;
        cArg[2] = double.class;
        try {
            highFrequencyCircle = (Trajectory1d) cl.getDeclaredConstructor(cArg)
                    .newInstance(radius, highFreq, phase);
            lowFrequencyCircle = (Trajectory1d) cl.getDeclaredConstructor(cArg)
                    .newInstance(radius, lowFreq, phase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Parameterized.Parameters
    public static Collection<? extends Class> getParams() {
        return Lists.newArrayList(ConstantSwingTrajectory1D.class,
                PendulumSwingTrajectory1D.class,
                );
    }

    @Test
    public void getTrajectoryPositionTestFrequencyAndRadiusRelation()
            throws Exception {
        for (double i = 0; i < 3;
             i += 0.66) {
            Assert.assertEquals(radius,
                    highFrequencyCircle
                            .getDesiredPosition(i), 0.01);
        }

        for (double i = 0; i < 30;
             i += 10) {
            Assert.assertEquals(radius,
                    lowFrequencyCircle
                            .getDesiredPosition(i), 0.01);
        }
    }

    @Test
    public void getTrajectoryVelocityTestFrequencyAndRadiusRelation() {
        for (double i = 0; i < 30;
             i += 2) {

            Assert.assertTrue(
                    Math.abs(highFrequencyCircle
                            .getDesiredVelocity(i))
                            < PeriodicTrajectory.MAX_ABSOLUTE_SPEED);
        }

        for (double i = 0; i < 30;
             i += 2) {

            Assert.assertTrue(
                    Math.abs(lowFrequencyCircle
                            .getDesiredVelocity(i))
                            < PeriodicTrajectory.MAX_ABSOLUTE_SPEED);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParamConstructorWithHighThanAllowedSpeedRate()
            throws Exception {
        Class[] cArg = new Class[3];
        cArg[0] = double.class;
        cArg[1] = double.class;
        cArg[2] = double.class;
        try {
            Trajectory1d target = (Trajectory1d) this.cl
                    .getDeclaredConstructor(cArg)
                    .newInstance(1, 1, 0);
        } catch (InvocationTargetException e) {
            if (new Exception(e.getCause()).getMessage()
                    .contains("MAX_ABSOLUTE_SPEED")) {
                throw new IllegalArgumentException(e.getCause());
            }
        } catch (Exception e) {
            fail();
        }
    }
}