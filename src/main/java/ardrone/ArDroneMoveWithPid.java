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

        final PidParameters pidParameters = PidParameters.builder()
                .kp(0.1)
                .kd(0)
                .ki(0)
                .minVelocity(-1)
                .maxVelocity(1)
                .minIntegralError(-5)
                .maxIntegralError(5)
                .build();

        final Pose goalPose = Pose.builder().x(10).y(-10).z(10).yaw(1).build();
        final Velocity goalVelocity = Velocity.builder().linearX(0).linearY(0).linearZ(0).angularZ(0).build();

        final PidController4D pidController4D = PidController4D.builder()
                .linearXParameters(pidParameters)
                .linearYParameters(pidParameters)
                .linearZParameters(pidParameters)
                .angularZParameters(pidParameters)
                .goalPose(goalPose)
                .goalVelocity(goalVelocity)
                .build();

        final Subscriber<ModelStates> subscriber = connectedNode.newSubscriber("/gazebo/model_states",
                ModelStates._TYPE);

        subscriber.addMessageListener(new MessageListener<ModelStates>() {
            @Override
            public void onNewMessage(ModelStates modelStates) {
                final String name = "quadrotor";
                final List<String> names = modelStates.getName();
                final int index = names.indexOf(name);

                final geometry_msgs.Pose currentPose = modelStates.getPose().get(index);
                final Twist currentTwist = modelStates.getTwist().get(index);

                final Point currentPoint = currentPose.getPosition();
                final Quaternion currentOrientation = currentPose.getOrientation();
                final Pose pose = Pose.builder()
                        .x(currentPoint.getX())
                        .y(currentPoint.getY())
                        .z(currentPoint.getZ())
                        .yaw(currentOrientation.getZ())
                        .build();
                final Velocity velocity = Velocity.builder()
                        .linearX(currentTwist.getLinear().getX())
                        .linearY(currentTwist.getLinear().getY())
                        .linearZ(currentTwist.getLinear().getZ())
                        .angularZ(currentTwist.getAngular().getZ())
                        .build();

                final Velocity nextVelocity = pidController4D.compute(pose, velocity);

                final Twist nextTwist = pilotingPublisher.newMessage();
                nextTwist.getAngular().setZ(nextVelocity.angularZ());
                nextTwist.getLinear().setX(nextVelocity.linearX());
                nextTwist.getLinear().setY(nextVelocity.linearY());
                nextTwist.getLinear().setZ(nextVelocity.linearZ());

                pilotingPublisher.publish(nextTwist);
            }
        });

        connectedNode.shutdown();
    }
}
