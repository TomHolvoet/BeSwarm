package control;

import com.google.common.base.Optional;

import control.dto.Pose;
import gazebo_msgs.ModelStates;
import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import services.ros_subscribers.ModelStateSubscriber;
import utils.math.EulerAngle;

/**
 * @author Hoang Tung Dinh
 */
public final class ModelStatePoseEstimator implements PoseEstimator {
    private final ModelStateSubscriber modelStateSubscriber;
    private final String modelName;

    private ModelStatePoseEstimator(ModelStateSubscriber modelStateSubscriber, String modelName) {
        this.modelStateSubscriber = modelStateSubscriber;
        this.modelName = modelName;
        this.modelStateSubscriber.startListeningToMessages();
    }

    public static ModelStatePoseEstimator create(ModelStateSubscriber modelStateSubscriber, String modelName) {
        return new ModelStatePoseEstimator(modelStateSubscriber, modelName);
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
