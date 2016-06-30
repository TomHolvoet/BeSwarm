package applications.trajectory;

import control.FiniteTrajectory4d;
import control.Trajectory4d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Trajectory represent a straight line in space between two given points at
 * a given speed.
 * Once the destination point has been reached, the trajectory enforces to
 * hold position
 * at the destination point.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
class StraightLineTrajectory4D extends BasicTrajectory
        implements FiniteTrajectory4d {
    private final Point4D srcpoint;
    private final Point4D targetpoint;
    private final double velocity;
    private final Trajectory4d moveTraj;
    private final Trajectory4d holdTraj;
    private Trajectory4d currentTraj;
    private final double endTime;

    StraightLineTrajectory4D(Point4D srcpoint, Point4D targetpoint,
            double velocity) {
        this.srcpoint = srcpoint;
        this.targetpoint = targetpoint;
        this.velocity = velocity;
        checkArgument(velocity > 0,
                "The provided velocity should be strictly greater than 0.");
        checkArgument(velocity <= BasicTrajectory.MAX_ABSOLUTE_VELOCITY,
                "The provided velocity should be smaller than BasicTrajectory"
                        + ".MAX_ABSOLUTE_VELOCITY");
        double speed = velocity;
        Point4D diff = Point4D.minus(targetpoint, srcpoint);
        double totalDistance = StrictMath.sqrt(StrictMath.pow(diff.getX(), 2) +
                StrictMath.pow(diff.getY(), 2) + StrictMath
                .pow(diff.getZ(), 2));
        this.endTime = totalDistance / speed;
        checkArgument(totalDistance > 0, "Distance to travel cannot be zero.");
        Point4D speedComponent = Point4D
                .create(velocity * (diff.getX() / totalDistance),
                        velocity * (diff.getY() / totalDistance),
                        velocity * (diff.getZ() / totalDistance),
                        diff.getAngle() / endTime);
        this.holdTraj = new HoldPositionTrajectory4D(targetpoint);
        this.moveTraj = new HoldPositionForwarder(srcpoint, speedComponent,
                endTime);
        this.currentTraj = moveTraj;
    }

    private void setHoldPosition(boolean shouldHold) {
        if (shouldHold) {
            this.currentTraj = holdTraj;
        } else {
            this.currentTraj = moveTraj;
        }
    }

    @Override
    public double getDesiredPositionX(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return currentTraj.getDesiredPositionX(currentTime);
    }

    @Override
    public double getDesiredVelocityX(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return currentTraj.getDesiredVelocityX(currentTime);
    }

    @Override
    public double getDesiredPositionY(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return currentTraj.getDesiredPositionY(currentTime);
    }

    @Override
    public double getDesiredVelocityY(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return currentTraj.getDesiredVelocityY(currentTime);
    }

    @Override
    public double getDesiredPositionZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return currentTraj.getDesiredPositionZ(currentTime);
    }

    @Override
    public double getDesiredVelocityZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return currentTraj.getDesiredVelocityZ(currentTime);
    }

    @Override
    public double getDesiredAngleZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return currentTraj.getDesiredAngleZ(currentTime);
    }

    @Override
    public double getDesiredAngularVelocityZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return currentTraj.getDesiredAngularVelocityZ(currentTime);
    }

    @Override
    public String toString() {
        return "StraightLineTrajectory4D{" +
                "velocity=" + velocity +
                ", src point=" + srcpoint +
                ", target point=" + targetpoint +
                '}';
    }

    @Override
    public double getTrajectoryDuration() {
        return this.endTime;
    }

    private class HoldPositionForwarder
            extends Trajectory4DForwardingDecorator {
        private final double endTime;

        HoldPositionForwarder(Point4D srcComp, Point4D speedComp,
                double endTime) {
            super(new LinearTrajectory4D(srcComp,
                    speedComp));
            this.endTime = endTime;
        }

        @Override
        protected void velocityDelegate(double timeInSeconds) {
            if (timeInSeconds >= endTime) {
                setHoldPosition(true);
            }
        }

        @Override
        protected void positionDelegate(double timeInSeconds) {
            if (timeInSeconds >= endTime) {
                setHoldPosition(true);
            }
        }
    }
}