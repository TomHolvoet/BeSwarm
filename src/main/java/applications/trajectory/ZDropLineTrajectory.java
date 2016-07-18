package applications.trajectory;

import applications.trajectory.points.Point4D;
import control.FiniteTrajectory4d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class ZDropLineTrajectory extends BasicTrajectory implements FiniteTrajectory4d {

    private final StraightLineTrajectory4D target;
    private final LinearTrajectory1D zComp;
    private final double segmentLength;

    public ZDropLineTrajectory(Point4D before, Point4D after, double speed, double drops,
            double dropDistance) {
        checkArgument(before.getZ() == after.getZ(),
                "Origin and destination should be in the same horizontal plane.");
        this.target = new StraightLineTrajectory4D(before, after, speed, 1);
        this.segmentLength = target.getTotalDistance() / drops;
        this.zComp = new LinearTrajectory1D(-target.getTotalDistance(),
                segmentLength);
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
        final double currentTime = getRelativeTime(timeInSeconds) % segmentLength;
        return getZcomponent().getDesiredPosition(currentTime);
    }

    @Override
    public double getDesiredVelocityZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds) % segmentLength;
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
        return "ZDropLineTrajectory{" + "velocity=" + getTargetTrajectory().getVelocity()
                + ", src point=" + getTargetTrajectory().getSrcpoint() + ", target point="
                + getTargetTrajectory().getTargetpoint() + '}';
    }

    @Override
    public double getTrajectoryDuration() {
        return getTargetTrajectory().getTrajectoryDuration();
    }

    public StraightLineTrajectory4D getTargetTrajectory() {
        return this.target;
    }

    public LinearTrajectory1D getZcomponent() {
        return this.zComp;
    }
}
