package applications.trajectory;

import control.Trajectory4d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Trajectory represent a straight line in space using a given speed.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
class StraightLineTrajectory4D implements Trajectory4d {
    private final Trajectory4d moveTraj;
    private final Trajectory4d holdTraj;
    private Trajectory4d currentTraj;

    StraightLineTrajectory4D(Point4D srcpoint, Point4D targetpoint,
            double velocity) {
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
        final double endTime = totalDistance / speed;
        checkArgument(totalDistance > 0, "Distance to travel cannot be zero.");
        Point4D speedComponent = Point4D
                .create(velocity * (diff.getX() / totalDistance),
                        velocity * (diff.getY() / totalDistance),
                        velocity * (diff.getZ() / totalDistance),
                        diff.getAngle() / endTime);
        this.holdTraj = new HoldPositionTrajectory4D(targetpoint);
        this.moveTraj = new HoldPositionForwarder2(srcpoint, speedComponent,
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
        return currentTraj.getDesiredPositionX(timeInSeconds);
    }

    @Override
    public double getDesiredVelocityX(double timeInSeconds) {
        return currentTraj.getDesiredVelocityX(timeInSeconds);
    }

    @Override
    public double getDesiredPositionY(double timeInSeconds) {
        return currentTraj.getDesiredPositionY(timeInSeconds);
    }

    @Override
    public double getDesiredVelocityY(double timeInSeconds) {
        return currentTraj.getDesiredVelocityY(timeInSeconds);
    }

    @Override
    public double getDesiredPositionZ(double timeInSeconds) {
        return currentTraj.getDesiredPositionZ(timeInSeconds);
    }

    @Override
    public double getDesiredVelocityZ(double timeInSeconds) {
        return currentTraj
                .getDesiredVelocityZ(timeInSeconds);
    }

    @Override
    public double getDesiredAngleZ(double timeInSeconds) {
        return currentTraj.getDesiredAngleZ(timeInSeconds);
    }

    @Override
    public double getDesiredAngularVelocityZ(double timeInSeconds) {
        return currentTraj
                .getDesiredAngularVelocityZ(timeInSeconds);
    }

    private class HoldPositionForwarder2
            extends Trajectory4DForwardingDecorator {
        private final double endTime;

        HoldPositionForwarder2(Point4D srcComp, Point4D speedComp,
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