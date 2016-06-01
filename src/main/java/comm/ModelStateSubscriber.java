package comm;

import bebopbehavior.Pose;
import control.PoseUpdater;
import gazebo_msgs.ModelStates;
import geom.Transformations;
import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import geometry_msgs.Twist;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.List;

/**
 * @author Hoang Tung Dinh
 */
public final class ModelStateSubscriber {
    private final PoseUpdater poseUpdater;
    private final Subscriber<ModelStates> subscriber;

    private ModelStateSubscriber(PoseUpdater poseUpdater, Subscriber<ModelStates> subscriber) {
        this.poseUpdater = poseUpdater;
        this.subscriber = subscriber;
    }

    public static ModelStateSubscriber create(PoseUpdater poseUpdater, Subscriber<ModelStates> subscriber) {
        return new ModelStateSubscriber(poseUpdater, subscriber);
    }

    public void startListeningToModelState() {
        subscriber.addMessageListener(ModelStatesMessageListener.create(poseUpdater));
    }

    private static final class ModelStatesMessageListener implements MessageListener<ModelStates> {
        private final PoseUpdater poseUpdater;

        private ModelStatesMessageListener(PoseUpdater poseUpdater) {
            this.poseUpdater = poseUpdater;
        }

        public static ModelStatesMessageListener create(PoseUpdater poseUpdater) {
            return new ModelStatesMessageListener(poseUpdater);
        }

        @Override
        public void onNewMessage(ModelStates modelStates) {
            final String name = "quadrotor";
            final List<String> names = modelStates.getName();
            final int index = names.indexOf(name);

            final geometry_msgs.Pose newPoseMessage = modelStates.getPose().get(index);
            final Twist newTwist = modelStates.getTwist().get(index);

            final Point newPoint = newPoseMessage.getPosition();
            final Quaternion newOrientation = newPoseMessage.getOrientation();
            final double newYaw = Transformations.computeEulerAngleFromQuaternionAngle(newOrientation).angleZ();

            final Pose newPose = Pose.builder()
                    .x(newPoint.getX())
                    .y(newPoint.getY())
                    .z(newPoint.getZ())
                    .yaw(newYaw)
                    .build();

            poseUpdater.updatePose(newPose);
        }
    }
}
