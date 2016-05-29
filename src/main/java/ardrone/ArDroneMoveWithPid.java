package ardrone;

import bebopcontrol.Command;
import bebopcontrol.Hover;
import bebopcontrol.Pose;
import bebopcontrol.Takeoff;
import bebopcontrol.Velocity;
import gazebo_msgs.ModelStates;
import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import geometry_msgs.Twist;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import pidcontroller.CoordinateTransformer;
import pidcontroller.PidController4D;
import pidcontroller.PidParameters;
import std_msgs.Empty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hoang Tung Dinh
 */
public class ArDroneMoveWithPid extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ArDroneMoveWithPid");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final Publisher<Empty> takeoffPublisher = connectedNode.newPublisher("/ardrone/takeoff", Empty._TYPE);
        final Publisher<Twist> pilotingPublisher = connectedNode.newPublisher("/cmd_vel", Twist._TYPE);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO write to log
        }

        final List<Command> commands = new ArrayList<>();
        final Command takeOff = Takeoff.create(takeoffPublisher);
        commands.add(takeOff);
        final Command hoverFiveSecond = Hover.create(pilotingPublisher, 5);
        commands.add(hoverFiveSecond);

        for (final Command command : commands) {
            command.execute();
        }

        final PidParameters pidLinearParameters = PidParameters.builder()
                .kp(0.5)
                .kd(1)
                .ki(0)
                .minVelocity(-9999999)
                .maxVelocity(9999999)
                .minIntegralError(-5)
                .maxIntegralError(5)
                .build();

        final PidParameters pidAngularParameters = PidParameters.builder()
                .kp(0.1)
                .kd(2)
                .ki(0)
                .minVelocity(-9999999)
                .maxVelocity(9999999)
                .minIntegralError(-5)
                .maxIntegralError(5)
                .build();

        final Pose goalPose = Pose.builder().x(3).y(-3).z(3).yaw(1).build();
        final Velocity goalVelocity = Velocity.builder().linearX(0).linearY(0).linearZ(0).angularZ(0).build();

        final PidController4D pidController4D = PidController4D.builder()
                .linearXParameters(pidLinearParameters)
                .linearYParameters(pidLinearParameters)
                .linearZParameters(pidLinearParameters)
                .angularZParameters(pidAngularParameters)
                .goalPose(goalPose)
                .goalVelocity(goalVelocity)
                .build();

        final Subscriber<ModelStates> subscriber = connectedNode.newSubscriber("/gazebo/model_states",
                ModelStates._TYPE);

        subscriber.addMessageListener(new MessageListener<ModelStates>() {
            private long lastTime = 0;

            @Override
            public void onNewMessage(ModelStates modelStates) {
                final String name = "quadrotor";
                final List<String> names = modelStates.getName();
                final int index = names.indexOf(name);

                final geometry_msgs.Pose currentPose = modelStates.getPose().get(index);
                final Twist currentTwist = modelStates.getTwist().get(index);

                if (System.currentTimeMillis() - lastTime < 50) {
                    return;
                }

                lastTime = System.currentTimeMillis();

                final Point currentPoint = currentPose.getPosition();
                final Quaternion currentOrientation = currentPose.getOrientation();
                final double currentYaw = computeYawFromQuaternion(currentOrientation);
                System.out.println("CURRENT YAW: " + currentYaw);

                final Pose pose = Pose.builder()
                        .x(currentPoint.getX())
                        .y(currentPoint.getY())
                        .z(currentPoint.getZ())
                        .yaw(currentYaw)
                        .build();

                final Velocity velocity = Velocity.builder()
                        .linearX(currentTwist.getLinear().getX())
                        .linearY(currentTwist.getLinear().getY())
                        .linearZ(currentTwist.getLinear().getZ())
                        .angularZ(currentTwist.getAngular().getZ())
                        .build();

                final Velocity nextGlobalVelocity = pidController4D.compute(pose, velocity);
                final Velocity nextLocalVelocity = CoordinateTransformer.globalToLocalVelocity(nextGlobalVelocity,
                        currentYaw);

                final Twist nextTwist = pilotingPublisher.newMessage();
                nextTwist.getLinear().setX(getRefinedVelocity(nextLocalVelocity.linearX()));
                nextTwist.getLinear().setY(getRefinedVelocity(nextLocalVelocity.linearY()));
                nextTwist.getLinear().setZ(getRefinedVelocity(nextLocalVelocity.linearZ()));
                nextTwist.getAngular().setZ(getRefinedVelocity(nextLocalVelocity.angularZ()));

                pilotingPublisher.publish(nextTwist);
            }
        });
    }

    private static double getRefinedVelocity(double initialVelocity) {
        double refinedVelocity = initialVelocity;
        if (refinedVelocity > 1) {
            refinedVelocity = 1;
        } else if (refinedVelocity < -1) {
            refinedVelocity = -1;
        }

        return refinedVelocity;
    }

    private static double computeYawFromQuaternion(Quaternion quaternion) {
        final double q0 = quaternion.getW();
        final double q1 = quaternion.getX();
        final double q2 = quaternion.getY();
        final double q3 = quaternion.getZ();

        return StrictMath.atan2(2 * (q0 * q3 + q1 * q2), 1 - 2 * (q2 * q2 + q3 * q3));
    }
}
