package applications.trajectory;

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
final class CircleTrajectory4D extends PeriodicTrajectory
        implements Trajectory4d {
    private final Point4D location;
    private final CircleTrajectory2D xycircle;
    private final double scaleFactor;
    private final Trajectory1d angularMotion;

    private CircleTrajectory4D(Point4D location, double phase, double radius,
            double frequency,
            double planeAngle) {
        super(phase, location, radius, frequency);
        this.location = location;
        this.scaleFactor = StrictMath.tan(planeAngle);
        this.xycircle = CircleTrajectory2D.builder().setRadius(radius)
                .setFrequency(frequency).setOrigin(location).setPhase(phase)
                .build();
        this.angularMotion = new ConstantVelocityAngularTrajectory1D(frequency,
                phase);
    }

    static Builder builder() {
        return new Builder();
    }

    @Override
    public double getDesiredPositionX(double timeInSeconds) {
        return xycircle.getDesiredPositionAbscissa(timeInSeconds);
    }

    @Override
    public double getDesiredVelocityX(double timeInSeconds) {
        return xycircle.getDesiredVelocityAbscissa(timeInSeconds);
    }

    @Override
    public double getDesiredPositionY(double timeInSeconds) {
        return xycircle.getDesiredPositionOrdinate(timeInSeconds);
    }

    @Override
    public double getDesiredVelocityY(double timeInSeconds) {
        return xycircle.getDesiredVelocityOrdinate(timeInSeconds);
    }

    @Override
    public double getDesiredPositionZ(double timeInSeconds) {
        return scaleFactor * xycircle
                .getDesiredPositionOrdinate(timeInSeconds) - location.getY()
                + location.getZ();
    }

    @Override
    public double getDesiredVelocityZ(double timeInSeconds) {
        return scaleFactor * xycircle.getDesiredVelocityOrdinate(timeInSeconds);
    }

    @Override
    public double getDesiredAngleZ(double timeInSeconds) {
        return angularMotion.getDesiredPosition(timeInSeconds);
    }

    @Override
    public double getDesiredAngularVelocityZ(double timeInSeconds) {
        return angularMotion.getDesiredVelocity(timeInSeconds);
    }

    /**
     * Builder class for 4D circle trajectories.
     */
    public static class Builder {
        private Point4D location = Point4D.origin();
        private double radius = 1;
        private double frequency = 5;
        private double planeAngle = 0;
        private double phase = 0;

        private Builder() {
        }

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
            return new CircleTrajectory4D(location, phase, radius,
                    frequency,
                    planeAngle);
        }
    }
}