package applications.trajectory;

import control.Trajectory1d;
import control.Trajectory4d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Trajectory represent a straight line in space using a given speed.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
class StraightLineTrajectory4D implements Trajectory4d {
    private final Trajectory1d xComp;
    private final Trajectory1d yComp;
    private final Trajectory1d zComp;
    private final Trajectory1d angleComp;
    private final Trajectory4d holdTraj;
    private boolean hold;

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
        this.xComp = new holdPositionForwarder(srcpoint.getX(),
                speedComponent.getX(),
                endTime);
        this.yComp = new holdPositionForwarder(srcpoint.getY(),
                speedComponent.getY(),
                endTime);
        this.zComp = new holdPositionForwarder(srcpoint.getZ(),
                speedComponent.getZ(),
                endTime);
        this.angleComp = new holdPositionForwarder(srcpoint.getAngle(),
                speedComponent.getAngle(),
                endTime);
        this.holdTraj = new HoldPositionTrajectory4D(targetpoint);
        this.hold = false;
    }

    private void setHoldPosition(boolean arg) {
        this.hold = arg;
    }

    @Override
    public double getDesiredPositionX(double timeInSeconds) {
        if (!hold) {
            return this.xComp.getDesiredPosition(timeInSeconds);
        }
        return holdTraj
                .getDesiredPositionX(timeInSeconds);
    }

    @Override
    public double getDesiredVelocityX(double timeInSeconds) {
        if (!hold) {
            return this.xComp.getDesiredVelocity(timeInSeconds);
        }
        return holdTraj
                .getDesiredVelocityX(timeInSeconds);
    }

    @Override
    public double getDesiredPositionY(double timeInSeconds) {
        if (!hold) {
            return this.yComp.getDesiredPosition(timeInSeconds);
        }
        return holdTraj.getDesiredPositionY(timeInSeconds);
    }

    @Override
    public double getDesiredVelocityY(double timeInSeconds) {
        if (!hold) {
            return this.yComp.getDesiredVelocity(timeInSeconds);
        }
        return holdTraj
                .getDesiredVelocityY(timeInSeconds);
    }

    @Override
    public double getDesiredPositionZ(double timeInSeconds) {
        if (!hold) {
            return this.zComp.getDesiredPosition(timeInSeconds);
        }
        return holdTraj.getDesiredPositionZ(timeInSeconds);
    }

    @Override
    public double getDesiredVelocityZ(double timeInSeconds) {
        if (!hold) {
            return this.zComp.getDesiredVelocity(timeInSeconds);
        }
        return holdTraj
                .getDesiredVelocityZ(timeInSeconds);
    }

    @Override
    public double getDesiredAngleZ(double timeInSeconds) {
        if (!hold) {
            return this.angleComp.getDesiredPosition(timeInSeconds);
        }
        return holdTraj.getDesiredAngleZ(timeInSeconds);
    }

    @Override
    public double getDesiredAngularVelocityZ(double timeInSeconds) {
        if (!hold) {
            return this.angleComp.getDesiredVelocity(timeInSeconds);
        }
        return holdTraj
                .getDesiredAngularVelocityZ(timeInSeconds);
    }

    private class holdPositionForwarder
            extends Trajectory1DForwardingDecorator {
        private final double endTime;

        holdPositionForwarder(double srcComp, double speedComp,
                double endTime) {
            super(new LinearTrajectory1D(srcComp,
                    speedComp));
            this.endTime = endTime;
        }

        @Override
        protected void velocityDelegate(double timeInSeconds) {
        }

        @Override
        protected void positionDelegate(double timeInSeconds) {
            if (timeInSeconds >= endTime) {
                setHoldPosition(true);
            }
        }
    }
}
