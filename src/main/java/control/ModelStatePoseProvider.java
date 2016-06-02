package control;

import behavior.Pose;
import com.google.common.base.Optional;
import comm.ModelStateSubscriber;
import gazebo_msgs.ModelStates;
import geom.EulerAngle;
import geometry_msgs.Point;
import geometry_msgs.Quaternion;

/**
 * @author Hoang Tung Dinh
 */
public final class ModelStatePoseProvider implements PoseProvider {
    private final ModelStateSubscriber modelStateSubscriber;
    private final String modelName;

    private ModelStatePoseProvider(ModelStateSubscriber modelStateSubscriber, String modelName) {
        this.modelStateSubscriber = modelStateSubscriber;
        this.modelName = modelName;
        this.modelStateSubscriber.startListeningToModelStates();
    }

    public static ModelStatePoseProvider create(ModelStateSubscriber modelStateSubscriber, String modelName) {
        return new ModelStatePoseProvider(modelStateSubscriber, modelName);
    }

    @Override
    public Optional<Pose> getCurrentPose() {
        final Optional<ModelStates> modelStateOptional = modelStateSubscriber.getMostRecentModelStates();
        if (modelStateOptional.isPresent()) {
            final ModelStates modelStates = modelStateOptional.get();
            final int index = modelStates.getName().indexOf(modelName);
            if (index == -1) {
                return Optional.absent();
            }
            final geometry_msgs.Pose gazeboPose = modelStates.getPose().get(index);
            return getDronePoseInEulerRepresentation(gazeboPose);
        } else {
            return Optional.absent();
        }
    }

    private static Optional<Pose> getDronePoseInEulerRepresentation(geometry_msgs.Pose gazeboPose) {
        final Point currentPoint = gazeboPose.getPosition();
        final Quaternion currentOrientation = gazeboPose.getOrientation();
        final double currentYaw = EulerAngle.createFromQuaternion(currentOrientation).angleZ();
        final Pose currentPose = Pose.builder()
                .x(currentPoint.getX())
                .y(currentPoint.getY())
                .z(currentPoint.getZ())
                .yaw(currentYaw)
                .build();
        return Optional.of(currentPose);
    }
}
