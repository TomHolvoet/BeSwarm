package applications.simulations.trajectory;

import control.Trajectory1d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A swing trajectory in 1 dimensions of motion specified in a frequency
 * (How many revolutions per second) and a radius
 * (half of the total distance covered).
 * This swing trajectory follows a periodic angular velocity function over the
 * radian circle which manifests in slowed movements near the edges of the
 * range of motion.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class PendulumSwingTrajectory1D extends PeriodicTrajectory
        implements Trajectory1d {
    public static final double MAX_ABSOLUTE_SPEED = 1;
    private static final double TWOPI = Math.PI * 2;
    private static final double HALFPI = Math.PI / 2;
    private static final double PISQUARED = Math.PI * Math.PI;
    private static final double MAXRANGE_VELOCITY_PERIODIC_PART = 0.649091;
    private final double radius;
    private final double frequency;
    private final double freq2pi;

    /**
     * Constructor
     *
     * @param radius    The length of the virtual pendulum string (or radius).
     * @param frequency The frequency f (amount of revolutions per second).
     *                  Equals 1/period.
     */
    public PendulumSwingTrajectory1D(double radius, double frequency) {
        this(radius, frequency, 0);
    }

    public PendulumSwingTrajectory1D(double radius, double frequency,
            double phase) {
        super((HALFPI * 3) + phase);
        this.radius = radius;
        this.frequency = frequency;
        this.freq2pi = frequency * TWOPI;
        checkArgument(Math.abs(radius * frequency) < MAX_ABSOLUTE_SPEED / (
                        PISQUARED * MAXRANGE_VELOCITY_PERIODIC_PART),
                "Absolute speed should not be larger than MAX_ABSOLUTE_SPEED,"
                        + " which is: "
                        + MAX_ABSOLUTE_SPEED);
    }

    private double getAngleFromT(double currentTime) {
        return HALFPI * StrictMath.cos(freq2pi * currentTime);
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
        return getRadius() * StrictMath
                .cos(getAngleFromT(currentTime)
                        + getPhaseDisplacement());
    }

    @Override
    public double getDesiredVelocity(double timeInSeconds) {
        setStartTime(timeInSeconds);

        final double currentTime = timeInSeconds - getStartTime();
        return
                PISQUARED * getFrequency() * getRadius() * StrictMath
                        .sin(freq2pi * currentTime + getPhaseDisplacement())
                        * StrictMath
                        .sin(HALFPI * StrictMath
                                .cos(freq2pi * currentTime
                                        + getPhaseDisplacement()));
    }
}