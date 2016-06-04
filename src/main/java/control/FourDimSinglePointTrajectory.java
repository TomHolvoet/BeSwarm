package control;

import commands.Pose;
import commands.Velocity;

/**
 * @author Hoang Tung Dinh
 */
public final class FourDimSinglePointTrajectory implements FourDimensionalTrajectory {
    private final OneDimensionalTrajectory trajectoryLinearX;
    private final OneDimensionalTrajectory trajectoryLinearY;
    private final OneDimensionalTrajectory trajectoryLinearZ;
    private final OneDimensionalTrajectory trajectoryAngularZ;

    private FourDimSinglePointTrajectory(Pose desiredPose, Velocity desiredVelocity) {
        trajectoryLinearX = OneDimSinglePointTrajectory.create(desiredPose.x(), desiredVelocity.linearX());
        trajectoryLinearY = OneDimSinglePointTrajectory.create(desiredPose.y(), desiredVelocity.linearY());
        trajectoryLinearZ = OneDimSinglePointTrajectory.create(desiredPose.z(), desiredVelocity.linearZ());
        trajectoryAngularZ = OneDimSinglePointTrajectory.create(desiredPose.yaw(), desiredVelocity.angularZ());
    }

    public static FourDimSinglePointTrajectory create(Pose desiredPose, Velocity desiredVelocity) {
        return new FourDimSinglePointTrajectory(desiredPose, desiredVelocity);
    }

    @Override
    public OneDimensionalTrajectory getTrajectoryLinearX() {
        return trajectoryLinearX;
    }

    @Override
    public OneDimensionalTrajectory getTrajectoryLinearY() {
        return trajectoryLinearY;
    }

    @Override
    public OneDimensionalTrajectory getTrajectoryLinearZ() {
        return trajectoryLinearZ;
    }

    @Override
    public OneDimensionalTrajectory getTrajectoryAngularZ() {
        return trajectoryAngularZ;
    }
}
