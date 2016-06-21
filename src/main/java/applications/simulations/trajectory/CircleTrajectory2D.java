package applications.simulations.trajectory;

import com.google.common.annotations.VisibleForTesting;
import control.Trajectory1d;
import control.Trajectory2d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A circular trajectory in 2 dimensions of motion specified in a frequency
 * (How many revolutions per second) and a radius.
 * Created by Kristof Coninx.
 */
public class CircleTrajectory2D extends PeriodicTrajectory
        implements Trajectory2d {
    public static final double MAX_ABSOLUTE_SPEED = 1;
    private final double freq2pi;
    private final double rfreq2pi;

    /**
     * Constructor
     *
     * @param radius    The radius of the circle.
     * @param frequency The frequency f (amount of revolutions per second).
     *                  Equals 1/period.
     * @param origin    The origin point around which to form the trajectory.
     *                  Represents a linear displacement.
     * @param clockwise Turn right hand if true;
     */
    private CircleTrajectory2D(double radius, double frequency, Point4D origin,
            double phase,
            boolean clockwise) {
        super(phase, origin, radius, frequency);
        this.freq2pi = frequency * TWOPI * (clockwise ? 1 : -1);
        this.rfreq2pi = frequency * radius * TWOPI * (clockwise ? 1 : -1);
        checkArgument(Math.abs(rfreq2pi) < MAX_ABSOLUTE_SPEED,
                "Absolute speed should not be larger than MAX_ABSOLUTE_SPEED,"
                        + " which is: "
                        + MAX_ABSOLUTE_SPEED);
    }

    @VisibleForTesting
    CircleTrajectory2D(double radius, double frequency) {
        this(radius, frequency, Point4D.origin(), 0, true);
    }

    @Override
    public Trajectory1d getTrajectoryLinearAbscissa() {
        return new Trajectory1d() {
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
        };
    }

    @Override
    public Trajectory1d getTrajectoryLinearOrdinate() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                setStartTime(timeInSeconds);

                final double currentTime = timeInSeconds - getStartTime();
                return getRadius() * StrictMath
                        .sin(freq2pi * currentTime + getPhaseDisplacement());
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                setStartTime(timeInSeconds);

                final double currentTime = timeInSeconds - getStartTime();
                return rfreq2pi * StrictMath
                        .cos(freq2pi * currentTime + getPhaseDisplacement());
            }
        };
    }

    public static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private double radius = 1;
        private double frequency = 5;
        private Point4D origin = Point4D.origin();
        private boolean clockwise = true;
        private double phase = 0;

        public Builder setRadius(double radius) {
            this.radius = radius;
            return this;
        }

        public Builder setFrequency(double frequency) {
            this.frequency = frequency;
            return this;
        }

        public Builder setOrigin(Point4D origin) {
            this.origin = origin;
            return this;
        }

        public Builder setClockwise(boolean clockwise) {
            this.clockwise = clockwise;
            return this;
        }

        public Builder setPhase(double phase) {
            this.phase = phase;
            return this;
        }

        public CircleTrajectory2D build() {
            return new CircleTrajectory2D(radius, frequency, origin, phase,
                    clockwise);
        }
    }
}