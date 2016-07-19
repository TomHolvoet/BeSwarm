package applications.trajectory;

import applications.trajectory.points.Point4D;
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
class ConstantSwingTrajectory1D extends PeriodicTrajectory implements Trajectory1d {
    private final double freq2pi;
    private final double rfreq2pi;

    /**
     * Constructor
     *
     * @param radius    The radius of the circle.
     * @param frequency The frequency f (amount of revolutions per second).
     *                  Equals 1/period.
     */
    ConstantSwingTrajectory1D(double radius, double frequency) {
        this(Point4D.origin(), radius, frequency, 0);
    }

    ConstantSwingTrajectory1D(Point4D origin, double radius, double frequency, double phase) {
        super(phase, origin, radius, frequency);
        this.freq2pi = frequency * TWOPI;
        this.rfreq2pi = frequency * radius * TWOPI;
        checkArgument(Math.abs(rfreq2pi) < MAX_ABSOLUTE_VELOCITY,
                "Absolute speed should not be larger than "
                        + "MAX_ABSOLUTE_VELOCITY,"
                        + " which is: "
                        + MAX_ABSOLUTE_VELOCITY);
    }

    @Override
    public double getDesiredPosition(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getLinearDisplacement().getX() + getRadius() * StrictMath
                .cos(freq2pi * currentTime + getPhaseDisplacement());
    }

    @Override
    public double getDesiredVelocity(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return -rfreq2pi * StrictMath.sin(freq2pi * currentTime + getPhaseDisplacement());
    }
}