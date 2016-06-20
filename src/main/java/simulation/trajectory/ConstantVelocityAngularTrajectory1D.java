package simulation.trajectory;

import com.google.common.annotations.VisibleForTesting;
import control.Trajectory1d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A trajectory in 1 dimensions of motion specified in a frequency
 * (How many revolutions per second).
 * This can be used for specifying angular motion.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class ConstantVelocityAngularTrajectory1D extends PeriodicTrajectory
        implements Trajectory1d {
    private static final double TWOPI = Math.PI * 2;

    /**
     * Constructor
     *
     * @param frequency The frequency f (amount of revolutions per second).
     *                  Equals 1/period.
     * @param frequency The frequency f (amount of revolutions per second).
     *                  Equals 1/period.
     */
    public ConstantVelocityAngularTrajectory1D(
            double frequency, double phase) {
        this(Math.PI, frequency, phase);
    }

    @VisibleForTesting
    ConstantVelocityAngularTrajectory1D(double radius, double frequency,
            double phase) {
        super(phase, Point4D.origin(), radius, frequency);
        checkArgument(Math.abs(radius * frequency * TWOPI) < MAX_ABSOLUTE_SPEED,
                "Absolute speed should not be larger than MAX_ABSOLUTE_SPEED,"
                        + " which is: "
                        + MAX_ABSOLUTE_SPEED);
    }

    public double getRadius() {
        return radius;
    }

    public double getFrequency() {
        return frequency;
    }

    @Override
    public double getDesiredPosition(double timeInSeconds) {
        setStartTime(timeInSeconds);

        final double currentTime = timeInSeconds - getStartTime();
        return Math.PI * StrictMath
                .sin(TWOPI * getFrequency() * currentTime
                        + getPhaseDisplacement());
    }

    @Override
    public double getDesiredVelocity(double timeInSeconds) {
        setStartTime(timeInSeconds);
        //d(omega)/dt = |v|*sin(pi/2)/|r| =
        final double currentTime = timeInSeconds - getStartTime();
        return TWOPISQUARED * getFrequency() * StrictMath
                .cos(TWOPI * getFrequency() * currentTime
                        + getPhaseDisplacement());
    }
}