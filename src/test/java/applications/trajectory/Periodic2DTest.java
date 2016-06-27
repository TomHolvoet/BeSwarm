package applications.trajectory;

import control.Trajectory1d;
import control.Trajectory2d;
import org.junit.Test;

import static applications.trajectory.TestUtils.*;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public abstract class Periodic2DTest {
    protected final double lowFreq = 1 / 10;
    protected final double highFreq = 1.5;
    protected final double radius = 0.065;
    protected Trajectory2d highFrequencyCircle;
    protected Trajectory2d lowFrequencyCircle;

    @Test
    public void getTrajectoryLinearAbscissaTestFrequencyAndRadiusRelation()
            throws Exception {

        testPositionFrequencyRadiusRelation(highFreq, radius,
                new HighFreq1D());
        testPositionFrequencyRadiusRelation(lowFreq, radius,
                new LowFreq1D());
    }

    @Test
    public void getTrajectoryAbscissaVelocityTestFrequencyAndRadiusRelation() {
        testSpeedBounds(new HighFreq1D(),
                BasicTrajectory.MAX_ABSOLUTE_VELOCITY);
        testVelocityFrequencyRadiusRelation(highFreq,
                new HighFreq1D());

        testSpeedBounds(new LowFreq1D(),
                BasicTrajectory.MAX_ABSOLUTE_VELOCITY);
        testVelocityFrequencyRadiusRelation(lowFreq,
                new LowFreq1D());

    }

    @Test
    public void getTrajectoryLinearOrdinateTestFrequencyAndRadiusRelation()
            throws Exception {

        testPositionFrequencyRadiusRelation(highFreq, 0,
                new HighFreq1D());
        testPositionFrequencyRadiusRelation(lowFreq, 0,
                new LowFreq1D());
    }

    @Test
    public void getTrajectoryOrdinateVelocityTestFrequencyAndRadiusRelation() {
        testSpeedBounds(new HighFreq1D(),
                BasicTrajectory.MAX_ABSOLUTE_VELOCITY);

        testSpeedBounds(new LowFreq1D(),
                BasicTrajectory.MAX_ABSOLUTE_VELOCITY);
    }

    private class HighFreq1D implements Trajectory1d {
        @Override
        public double getDesiredPosition(double
                timeInSeconds) {
            return highFrequencyCircle
                    .getDesiredPositionAbscissa
                            (timeInSeconds);
        }

        @Override
        public double getDesiredVelocity(double
                timeInSeconds) {
            return highFrequencyCircle
                    .getDesiredVelocityAbscissa
                            (timeInSeconds);
        }
    }

    private class LowFreq1D implements Trajectory1d {
        @Override
        public double getDesiredPosition(double
                timeInSeconds) {
            return lowFrequencyCircle
                    .getDesiredPositionAbscissa
                            (timeInSeconds);
        }

        @Override
        public double getDesiredVelocity(double
                timeInSeconds) {
            return lowFrequencyCircle
                    .getDesiredVelocityAbscissa
                            (timeInSeconds);
        }
    }
}
