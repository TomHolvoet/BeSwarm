package simulation.trajectory;

import control.Trajectory1d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A swing trajectory in 1 dimensions of motion specified in a frequency
 * (How many revolutions per second) and a radius
 * (half of the total distance covered).
 * This swing trajectory follows a constant angular velocity function over the
 * radian cirle.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class ConstantSwingTrajectory1D extends PeriodicTrajectory
        implements Trajectory1d {
    public static final double MAX_ABSOLUTE_SPEED = 1;
    private static final double TWOPI = Math.PI * 2;
    private final double freq2pi;
    private final double rfreq2pi;

    /**
     * Constructor
     *
     * @param radius    The radius of the circle.
     * @param frequency The frequency f (amount of revolutions per second).
     *                  Equals 1/period.
     */
    public ConstantSwingTrajectory1D(double radius, double frequency) {
        this(radius, frequency, 0);
    }

    public ConstantSwingTrajectory1D(double radius, double frequency,
            double phase) {
        super(phase, Point4D.origin(), radius, frequency);
        this.freq2pi = frequency * TWOPI;
        this.rfreq2pi = frequency * radius * TWOPI;
        checkArgument(Math.abs(rfreq2pi) < MAX_ABSOLUTE_SPEED,
                "Absolute speed should not be larger than MAX_ABSOLUTE_SPEED,"
                        + " which is: "
                        + MAX_ABSOLUTE_SPEED);
    }

    @Override
    public double getDesiredPosition(double timeInSeconds) {
        setStartTime(timeInSeconds);

        final double currentTime = timeInSeconds - getStartTime();
        return getRadius() * StrictMath
                .cos(freq2pi * currentTime + getPhaseDisplacement());
    }

    @Override
    public double getDesiredVelocity(double timeInSeconds) {
        setStartTime(timeInSeconds);

        final double currentTime = timeInSeconds - getStartTime();
        return -rfreq2pi * StrictMath
                .sin(freq2pi * currentTime + getPhaseDisplacement());
    }
}