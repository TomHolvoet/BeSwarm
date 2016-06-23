package applications.simulations.trajectory;

import control.Trajectory2d;
import org.junit.Test;

import static applications.simulations.trajectory.TestUtils.*;

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
                highFrequencyCircle.getTrajectoryLinearAbscissa());
        testPositionFrequencyRadiusRelation(lowFreq, radius,
                lowFrequencyCircle.getTrajectoryLinearAbscissa());
    }

    @Test
    public void getTrajectoryAbscissaVelocityTestFrequencyAndRadiusRelation() {
        testSpeedBounds(highFrequencyCircle.getTrajectoryLinearAbscissa(),
                PeriodicTrajectory.MAX_ABSOLUTE_SPEED);
        testVelocityFrequencyRadiusRelation(highFreq,
                highFrequencyCircle.getTrajectoryLinearAbscissa());

        testSpeedBounds(lowFrequencyCircle.getTrajectoryLinearAbscissa(),
                PeriodicTrajectory.MAX_ABSOLUTE_SPEED);
        testVelocityFrequencyRadiusRelation(lowFreq,
                lowFrequencyCircle.getTrajectoryLinearAbscissa());

    }

    @Test
    public void getTrajectoryLinearOrdinateTestFrequencyAndRadiusRelation()
            throws Exception {

        testPositionFrequencyRadiusRelation(highFreq, 0,
                highFrequencyCircle.getTrajectoryLinearOrdinate());
        testPositionFrequencyRadiusRelation(lowFreq, 0,
                lowFrequencyCircle.getTrajectoryLinearOrdinate());
    }

    @Test
    public void getTrajectoryOrdinateVelocityTestFrequencyAndRadiusRelation() {
        testSpeedBounds(highFrequencyCircle.getTrajectoryLinearOrdinate(),
                PeriodicTrajectory.MAX_ABSOLUTE_SPEED);

        testSpeedBounds(lowFrequencyCircle.getTrajectoryLinearOrdinate(),
                PeriodicTrajectory.MAX_ABSOLUTE_SPEED);
    }
}
