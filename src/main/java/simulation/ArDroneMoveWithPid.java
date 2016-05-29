package simulation;

import bebopbehavior.Command;
import bebopbehavior.Hover;
import bebopbehavior.Pose;
import bebopbehavior.Takeoff;
import bebopbehavior.Velocity;
import comm.TakeoffPublisher;
import comm.VelocityPublisher;
import gazebo_msgs.ModelStates;
import geom.Transformations;
import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import geometry_msgs.Twist;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
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
        final TakeoffPublisher takeoffPublisher = TakeoffPublisher.create(
                connectedNode.<Empty>newPublisher("/simulation/takeoff", Empty._TYPE));
        final VelocityPublisher velocityPublisher = VelocityPublisher.builder()
                .publisher(connectedNode.<Twist>newPublisher("/cmd_vel", Twist._TYPE))
                .minLinearX(-1)
                .minLinearY(-1)
                .minLinearZ(-1)
                .minAngularZ(-1)
                .maxLinearX(1)
                .maxLinearY(1)
                .maxLinearZ(1)
                .maxAngularZ(1)
                .build();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO write to log
        }

        final List<Command> commands = new ArrayList<>();
        final Command takeOff = Takeoff.create(takeoffPublisher);
        commands.add(takeOff);
        final Command hoverFiveSecond = Hover.create(velocityPublisher, 5);
        commands.add(hoverFiveSecond);

        for (final Command command : commands) {
            command.execute();
        }

        final PidParameters pidLinearParameters = PidParameters.builder().kp(0.5).kd(1).ki(0).build();
        final PidParameters pidAngularParameters = PidParameters.builder().kp(0.1).kd(0.5).ki(0).build();

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
                final double currentYaw = Transformations.computeEulerAngleFromQuaternionAngle(currentOrientation)
                        .angleZ();
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
                final Velocity nextLocalVelocity = Transformations.computeLocalVelocityFromGlobalVelocity(
                        nextGlobalVelocity, currentYaw);
                velocityPublisher.publishVelocityCommand(nextLocalVelocity);
            }
        });
    }
}
