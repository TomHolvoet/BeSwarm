package comm;

import bebopbehavior.Pose;
import gazebo_msgs.ModelStates;
import geom.Transformations;
import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import geometry_msgs.Twist;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Hoang Tung Dinh
 */
public final class ModelStateSubscriber {
    private final AtomicReference<Pose> atomicPose;
    private final Subscriber<ModelStates> subscriber;

    private ModelStateSubscriber(AtomicReference<Pose> atomicPose, Subscriber<ModelStates> subscriber) {
        this.atomicPose = atomicPose;
        this.subscriber = subscriber;
    }

    public static ModelStateSubscriber create(AtomicReference<Pose> pose, Subscriber<ModelStates> subscriber) {
        return new ModelStateSubscriber(pose, subscriber);
    }

    public void startListeningToModelState() {
        subscriber.addMessageListener(ModelStatesMessageListener.create(atomicPose));
    }

    private static final class ModelStatesMessageListener implements MessageListener<ModelStates> {
        private final AtomicReference<Pose> atomicPose;

        private ModelStatesMessageListener(AtomicReference<Pose> atomicPose) {
            this.atomicPose = atomicPose;
        }

        public static ModelStatesMessageListener create(AtomicReference<Pose> pose) {
            return new ModelStatesMessageListener(pose);
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

            atomicPose.set(newPose);
        }
    }
}
