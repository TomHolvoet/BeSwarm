package control.localization;

import com.google.common.base.Optional;
import control.dto.Pose;
import gazebo_msgs.ModelStates;
import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import services.ros_subscribers.MessagesSubscriberService;
import utils.math.Transformations;

/**
 * @author Hoang Tung Dinh
 */
public final class ModelStatePoseEstimator implements PoseEstimator {
    private final MessagesSubscriberService<ModelStates> modelStateSubscriber;
    private final String modelName;

    private ModelStatePoseEstimator(MessagesSubscriberService<ModelStates> modelStateSubscriber, String modelName) {
        this.modelStateSubscriber = modelStateSubscriber;
        this.modelName = modelName;
        this.modelStateSubscriber.startListeningToMessages();
    }

    public static ModelStatePoseEstimator create(MessagesSubscriberService<ModelStates> modelStateSubscriber,
            String modelName) {
        return new ModelStatePoseEstimator(modelStateSubscriber, modelName);
    }

    @Override
    public Optional<Pose> getCurrentPose() {
        final Optional<ModelStates> modelStateOptional = modelStateSubscriber.getMostRecentMessage();
        if (modelStateOptional.isPresent()) {
            final ModelStates modelStates = modelStateOptional.get();
            final int index = modelStates.getName().indexOf(modelName);
            if (index == -1) {
                return Optional.absent();
            }
            final geometry_msgs.Pose gazeboPose = modelStates.getPose().get(index);
            final Pose currentPose = getDronePoseInEulerRepresentation(gazeboPose);
            return Optional.of(currentPose);
        } else {
            return Optional.absent();
        }
    }

    private static Pose getDronePoseInEulerRepresentation(geometry_msgs.Pose gazeboPose) {
        final Point currentPoint = gazeboPose.getPosition();
        final Quaternion currentOrientation = gazeboPose.getOrientation();
        final double currentYaw = Transformations.quaternionToEulerAngle(currentOrientation).angleZ();
        return Pose.builder()
                .x(currentPoint.getX())
                .y(currentPoint.getY())
                .z(currentPoint.getZ())
                .yaw(currentYaw)
                .build();
    }
}
