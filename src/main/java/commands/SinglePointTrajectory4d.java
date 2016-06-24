package commands;

import control.Trajectory1d;
import control.Trajectory4d;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;

/**
 * @author Hoang Tung Dinh
 */
public final class SinglePointTrajectory4d implements Trajectory4d {
    private final Trajectory1d trajectoryLinearX;
    private final Trajectory1d trajectoryLinearY;
    private final Trajectory1d trajectoryLinearZ;
    private final Trajectory1d trajectoryAngularZ;

    private SinglePointTrajectory4d(Pose desiredPose, InertialFrameVelocity desiredInertialFrameVelocity) {
        trajectoryLinearX = SinglePointTrajectory1d.create(desiredPose.x(), desiredInertialFrameVelocity.linearX());
        trajectoryLinearY = SinglePointTrajectory1d.create(desiredPose.y(), desiredInertialFrameVelocity.linearY());
        trajectoryLinearZ = SinglePointTrajectory1d.create(desiredPose.z(), desiredInertialFrameVelocity.linearZ());
        trajectoryAngularZ = SinglePointTrajectory1d.create(desiredPose.yaw(), desiredInertialFrameVelocity.angularZ());
    }

    public static SinglePointTrajectory4d create(Pose desiredPose, InertialFrameVelocity desiredInertialFrameVelocity) {
        return new SinglePointTrajectory4d(desiredPose, desiredInertialFrameVelocity);
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
