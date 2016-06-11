package simulation.trajectory;

import control.Trajectory1d;
import control.Trajectory2d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A Pendulum trajectory in 2 dimensions of motion specified in a frequency
 * (How many revolutions per second) and a radius
 * (the length of the virtual pendulum string).
 * The pendulum trajectory is a half circle but with with modeled transition
 * from kinetic to potential energy and back.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class PendulumTrajectory2D extends PeriodicTrajectory
        implements Trajectory2d {
    private final double radius;
    private final double frequency;
    private final double freq2pi;
    private double startTime = -1;

    /**
     * Constructor
     *
     * @param radius    The length of the virtual pendulum string (or radius).
     * @param frequency The frequency f (amount of revolutions per second).
     *                  Equals 1/period.
     */
    public PendulumTrajectory2D(double radius, double frequency) {
        super(HALFPI * 3);
        this.radius = radius;
        this.frequency = frequency;
        this.freq2pi = frequency * TWOPI;
        checkArgument(
                Math.abs(radius * frequency) < MAX_ABSOLUTE_SPEED / PISQUARED,
                "Absolute speed should not be larger than MAX_ABSOLUTE_SPEED,"
                        + " which is: "
                        + MAX_ABSOLUTE_SPEED);
    }

    private void setStartTime(double timeInSeconds) {
        if (startTime < 0) {
            startTime = timeInSeconds;
        }
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

    @Override public Trajectory1d getTrajectoryLinearAbscissa() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                setStartTime(timeInSeconds);

                final double currentTime = timeInSeconds - startTime;
                return getRadius() * StrictMath
                        .cos(getAngleFromT(currentTime)
                                + getPhaseDisplacement());
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                setStartTime(timeInSeconds);

                final double currentTime = timeInSeconds - startTime;
                return
                        PISQUARED * getFrequency() * getRadius() * StrictMath
                                .sin(freq2pi * currentTime
                                        + getPhaseDisplacement()) * StrictMath
                                .sin(HALFPI * StrictMath
                                        .cos(freq2pi * currentTime
                                                + getPhaseDisplacement()));
            }
        };
    }

    @Override public Trajectory1d getTrajectoryLinearOrdinate() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                setStartTime(timeInSeconds);

                final double currentTime = timeInSeconds - startTime;
                return -getRadius() * StrictMath
                        .sin(getAngleFromT(currentTime)
                                + getPhaseDisplacement());
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                setStartTime(timeInSeconds);

                final double currentTime = timeInSeconds - startTime;
                return
                        PISQUARED * getFrequency() * getRadius() * StrictMath
                                .sin(freq2pi * currentTime
                                        + getPhaseDisplacement()) * StrictMath
                                .cos(HALFPI * StrictMath
                                        .cos(freq2pi * currentTime
                                                + getPhaseDisplacement()));
            }
        };
    }
}