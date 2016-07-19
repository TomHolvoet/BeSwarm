package applications.trajectory;

import applications.trajectory.points.Point4D;
import control.FiniteTrajectory4d;
import control.Trajectory1d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A straight line trajectory in xy plane with sudden drops in the z dimension.
 * Source and destination point shoudl be in the same z plane.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class ZDropLineTrajectory extends BasicTrajectory implements FiniteTrajectory4d {

    private final StraightLineTrajectory4D concreteTarget;
    private Trajectory1d zComp;
    private final double segmentLength;
    private boolean atEnd;

    ZDropLineTrajectory(Point4D before, Point4D after, double speed, double drops,
            double dropDistance) {
        checkArgument(before.getZ() == after.getZ(),
                "Origin and destination should be in the same horizontal plane.");
        concreteTarget = new StraightLineTrajectory4D(before, after, speed, 1);
        this.segmentLength = concreteTarget.getTotalDistance() / drops;
        this.zComp = new ObservingRingForwarder(new LinearTrajectory1D(after.getZ() - dropDistance,
                dropDistance / segmentLength), concreteTarget.getTrajectoryDuration());
        atEnd = false;
    }

    @Override
    public double getDesiredPositionX(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredPositionX(currentTime);
    }

    @Override
    public double getDesiredVelocityX(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredVelocityX(currentTime);
    }

    @Override
    public double getDesiredPositionY(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredPositionY(currentTime);
    }

    @Override
    public double getDesiredVelocityY(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredVelocityY(currentTime);
    }

    @Override
    public double getDesiredPositionZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getZcomponent().getDesiredPosition(currentTime);
    }

    @Override
    public double getDesiredVelocityZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getZcomponent().getDesiredVelocity(currentTime);
    }

    @Override
    public double getDesiredAngleZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredAngleZ(currentTime);
    }

    @Override
    public double getDesiredAngularVelocityZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredAngularVelocityZ(currentTime);
    }

    @Override
    public String toString() {
        return "ZDropLineTrajectory{" + "velocity=" + getConcreteTarget().getVelocity()
                + ", src point=" + getConcreteTarget().getSrcpoint() + ", target point="
                + getConcreteTarget().getTargetpoint() + '}';
    }

    @Override
    public double getTrajectoryDuration() {
        return getTargetTrajectory().getTrajectoryDuration();
    }

    private FiniteTrajectory4d getTargetTrajectory() {
        return getConcreteTarget();
    }

    private StraightLineTrajectory4D getConcreteTarget() {
        return this.concreteTarget;
    }

    private Trajectory1d getZcomponent() {
        return this.zComp;
    }

    public void setHoldPosition(boolean holdPosition) {
        boolean previous = atEnd;
        this.atEnd = holdPosition;
        if (previous != atEnd) {
            zComp = new HoldForwarder();
        }
    }

    private class HoldForwarder implements Trajectory1d {
        @Override
        public double getDesiredPosition(double timeInSeconds) {
            return getTargetTrajectory().getDesiredPositionZ(timeInSeconds);
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            return getTargetTrajectory().getDesiredVelocityZ(timeInSeconds);
        }
    }

    private class ObservingRingForwarder implements Trajectory1d {

        private double endTime;
        private final Trajectory1d target;

        public ObservingRingForwarder(Trajectory1d target, double endTime) {
            this.target = target;
            this.endTime = endTime;
        }

        protected void velocityDelegate(double timeInSeconds) {
            if (timeInSeconds >= endTime) {
                setHoldPosition(true);
            }
        }

        protected void positionDelegate(double timeInSeconds) {
            if (timeInSeconds >= endTime) {
                setHoldPosition(true);
            }
        }

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            positionDelegate(timeInSeconds);
            return target.getDesiredPosition(timeInSeconds % segmentLength);
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            positionDelegate(timeInSeconds);
            return target.getDesiredVelocity(timeInSeconds % segmentLength);
        }
    }
}
