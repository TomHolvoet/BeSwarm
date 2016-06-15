package control;

import commands.Pose;
import commands.Velocity;

/**
 * @author Hoang Tung Dinh
 */
public final class SinglePointTrajectory4d implements Trajectory4d {
    private final Trajectory1d trajectoryLinearX;
    private final Trajectory1d trajectoryLinearY;
    private final Trajectory1d trajectoryLinearZ;
    private final Trajectory1d trajectoryAngularZ;

    private SinglePointTrajectory4d(Pose desiredPose, Velocity desiredVelocity) {
        trajectoryLinearX = SinglePointTrajectory1d.create(desiredPose.x(), desiredVelocity.linearX());
        trajectoryLinearY = SinglePointTrajectory1d.create(desiredPose.y(), desiredVelocity.linearY());
        trajectoryLinearZ = SinglePointTrajectory1d.create(desiredPose.z(), desiredVelocity.linearZ());
        trajectoryAngularZ = SinglePointTrajectory1d.create(desiredPose.yaw(), desiredVelocity.angularZ());
    }

    public static SinglePointTrajectory4d create(Pose desiredPose, Velocity desiredVelocity) {
        return new SinglePointTrajectory4d(desiredPose, desiredVelocity);
    }

    @Override
    public Trajectory1d getTrajectoryLinearX() {
        return trajectoryLinearX;
    }

    @Override
    public Trajectory1d getTrajectoryLinearY() {
        return trajectoryLinearY;
    }

    @Override
    public Trajectory1d getTrajectoryLinearZ() {
        return trajectoryLinearZ;
    }

    @Override
    public Trajectory1d getTrajectoryAngularZ() {
        return trajectoryAngularZ;
    }
}
