package applications.trajectory;

import control.Trajectory1d;
import control.Trajectory4d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Trajectory represent a straight line in space using a given speed.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class StraightLineTrajectory4D implements Trajectory4d {
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
        this.hold = true;
    }

    private void setHoldPosition(boolean arg) {
        this.hold = arg;
    }

    @Override
    public Trajectory1d getTrajectoryLinearX() {
        if (!hold) {
            return this.xComp;
        }
        return holdTraj.getTrajectoryLinearX();
    }

    @Override
    public Trajectory1d getTrajectoryLinearY() {
        if (!hold) {
            return this.yComp;
        }
        return holdTraj.getTrajectoryLinearY();
    }

    @Override
    public Trajectory1d getTrajectoryLinearZ() {
        if (!hold) {
            return this.zComp;
        }
        return holdTraj.getTrajectoryLinearZ();
    }

    @Override
    public Trajectory1d getTrajectoryAngularZ() {
        if (!hold) {
            return this.angleComp;
        }
        return holdTraj.getTrajectoryAngularZ();
    }

    private class holdPositionForwarder
            extends Trajectory1DForwardingDecorator {
        private final double endTime;

        public holdPositionForwarder(double srcComp, double speedComp,
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
