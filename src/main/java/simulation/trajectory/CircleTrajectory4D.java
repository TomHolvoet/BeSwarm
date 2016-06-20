package simulation.trajectory;

import control.Trajectory1d;
import control.Trajectory4d;

/**
 * A 4D motion primitive for circle motion in 3 dimensions.
 * This circle is defined by a displacement parameter for relocating the
 * origin, a radius, a frequency and an angle of the circle towards the
 * xy-plane.
 * This angle represents the angle between the xy-plane and the plane in
 * which the circle movement is performed.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class CircleTrajectory4D extends PeriodicTrajectory
        implements Trajectory4d {
    private Point4D location;
    private CircleTrajectory2D xycircle;
    private double scalefactor;
    private Trajectory1d angularMotion;

    private CircleTrajectory4D(Point4D location, double phase, double radius,
            double frequency,
            double planeAngle) {
        super(phase, location, radius, frequency);
        this.location = location;
        this.scalefactor = StrictMath.tan(planeAngle);
        this.xycircle = CircleTrajectory2D.builder().setRadius(radius)
                .setFrequency(frequency).setOrigin(location).setPhase(phase)
                .build();
        this.angularMotion = new ConstantVelocityAngularTrajectory1D(frequency,
                phase);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Trajectory1d getTrajectoryLinearX() {
        return xycircle.getTrajectoryLinearAbscissa();
    }

    @Override
    public Trajectory1d getTrajectoryLinearY() {
        return xycircle.getTrajectoryLinearOrdinate();
    }

    @Override
    public Trajectory1d getTrajectoryLinearZ() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                return scalefactor * xycircle
                        .getTrajectoryLinearOrdinate()
                        .getDesiredPosition(timeInSeconds) - location.getY()
                        + location.getZ();
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                return scalefactor * xycircle
                        .getTrajectoryLinearOrdinate()
                        .getDesiredVelocity(timeInSeconds);
            }
        };
    }

    @Override
    public Trajectory1d getTrajectoryAngularZ() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                return angularMotion.getDesiredPosition(timeInSeconds);
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                return angularMotion.getDesiredVelocity(timeInSeconds);
            }
        };
    }

    public static class Builder {
        private Point4D location = Point4D.origin();
        private double radius = 1;
        private double frequency = 5;
        private double planeAngle = 0;
        private double phase = 0;

        public Builder setLocation(Point4D location) {
            this.location = location;
            return this;
        }

        public Builder setRadius(double radius) {
            this.radius = radius;
            return this;
        }

        public Builder setFrequency(double frequency) {
            this.frequency = frequency;
            return this;
        }

        public Builder setPlaneAngle(double planeAngle) {
            this.planeAngle = planeAngle;
            return this;
        }

        public Builder setPhase(double phase) {
            this.phase = phase;
            return this;
        }

        public CircleTrajectory4D build() {
            return new CircleTrajectory4D(location, phase, radius, frequency,
                    planeAngle);
        }
    }
}
