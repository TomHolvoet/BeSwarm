package control.localization;

import com.google.common.base.Optional;
import control.dto.DroneState;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import gazebo_msgs.ModelStates;
import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import geometry_msgs.Twist;
import services.ros_subscribers.MessagesSubscriberService;
import utils.math.Transformations;

/**
 * @author Hoang Tung Dinh
 */
public class GazeboModelStateEstimator implements StateEstimator {
    private final MessagesSubscriberService<ModelStates> modelStateSubscriber;
    private final String modelName;

    private GazeboModelStateEstimator(MessagesSubscriberService<ModelStates> modelStateSubscriber, String modelName) {
        this.modelStateSubscriber = modelStateSubscriber;
        this.modelName = modelName;
        this.modelStateSubscriber.startListeningToMessages();
    }

    public static GazeboModelStateEstimator create(MessagesSubscriberService<ModelStates> modelStateSubscriber,
            String modelName) {
        return new GazeboModelStateEstimator(modelStateSubscriber, modelName);
    }

    @Override
    public Optional<DroneState> getCurrentState() {
        final Optional<ModelStates> modelStateOptional = modelStateSubscriber.getMostRecentMessage();
        if (!modelStateOptional.isPresent()) {
            return Optional.absent();
        }

        final ModelStates modelStates = modelStateOptional.get();
        final int index = modelStates.getName().indexOf(modelName);
        if (index == -1) {
            return Optional.absent();
        }

        final Pose pose = getDronePose(modelStates, index);
        final InertialFrameVelocity inertialFrameVelocity = getInertialFrameVelocity(modelStates, index, pose);

        return Optional.of(DroneState.create(pose, inertialFrameVelocity));
    }

    private static InertialFrameVelocity getInertialFrameVelocity(ModelStates modelStates, int index, Pose pose) {
        final Twist gazeboTwist = modelStates.getTwist().get(index);
        return InertialFrameVelocity.builder()
                .linearX(gazeboTwist.getLinear().getX())
                .linearY(gazeboTwist.getLinear().getY())
                .linearZ(gazeboTwist.getLinear().getZ())
                .angularZ(gazeboTwist.getAngular().getZ())
                .poseYaw(pose.yaw())
                .build();
    }

    private static Pose getDronePose(ModelStates modelStates, int index) {
        final geometry_msgs.Pose gazeboPose = modelStates.getPose().get(index);
        return convertGazeboPoseToDronePose(gazeboPose);
    }

    private static Pose convertGazeboPoseToDronePose(geometry_msgs.Pose gazeboPose) {
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
