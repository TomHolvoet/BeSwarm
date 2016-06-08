package simulation.trajectory;

import control.Trajectory1d;
import control.Trajectory2d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A circular trajectory in 2 dimensions of motion specified in a frequency (How many revolutions per second) and a radius.
 * Created by Kristof Coninx.
 */
public class CircleTrajectory2D implements Trajectory2d {
    public static final double MAX_ABSOLUTE_SPEED = 1;
    private static final double TWOPI = Math.PI * 2;
    private final double radius;
    private final double frequency;
    private final double freq2pi;
    private final double rfreq2pi;
    private double startTime = -1;

    /**
     * Constructor
     *
     * @param radius    The radius of the circle.
     * @param frequency The frequency f (amount of revolutions per second). Equals 1/period.
     * @param clockwise Turn right hand if true;
     */
    public CircleTrajectory2D(double radius, double frequency,
            boolean clockwise) {
        this.radius = radius;
        this.frequency = frequency;
        this.freq2pi = frequency * TWOPI * (clockwise ? 1 : -1);
        this.rfreq2pi = frequency * radius * TWOPI * (clockwise ? 1 : -1);
        checkArgument(Math.abs(rfreq2pi) < MAX_ABSOLUTE_SPEED,
                "Absolute speed should not be larger than MAX_ABSOLUTE_SPEED, which is: "
                        + MAX_ABSOLUTE_SPEED);
    }

    private void setStartTime(double timeInSeconds) {
        if (startTime < 0) {
            startTime = timeInSeconds;
        }
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
                return getRadius() * StrictMath.cos(freq2pi * currentTime);
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                setStartTime(timeInSeconds);

                final double currentTime = timeInSeconds - startTime;
                return -rfreq2pi * StrictMath.sin(freq2pi * currentTime);
            }
        };
    }

    @Override public Trajectory1d getTrajectoryLinearOrdinate() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                setStartTime(timeInSeconds);

                final double currentTime = timeInSeconds - startTime;
                return getRadius() * StrictMath.sin(freq2pi * currentTime);
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                setStartTime(timeInSeconds);

                final double currentTime = timeInSeconds - startTime;
                return rfreq2pi * StrictMath.cos(freq2pi * currentTime);
            }
        };
    }
}
