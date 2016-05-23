package simpletest;

import bebopcontrol.Command;
import bebopcontrol.Hover;
import bebopcontrol.Land;
import bebopcontrol.MoveBackward;
import bebopcontrol.MoveForward;
import bebopcontrol.MoveLeft;
import bebopcontrol.MoveRight;
import bebopcontrol.Takeoff;
import geometry_msgs.Twist;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import std_msgs.Empty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hoang Tung Dinh
 */
public class BebopPlusPattern extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopController");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final Publisher<Empty> takeoffPublisher = connectedNode.newPublisher("/bebop/takeoff", Empty._TYPE);
        final Publisher<Empty> landPublisher = connectedNode.newPublisher("/bebop/land", Empty._TYPE);
        final Publisher<Twist> pilotingPublisher = connectedNode.newPublisher("/bebop/cmd_vel", Twist._TYPE);

        final List<Command> commands = new ArrayList<>();
        final Command takeOff = Takeoff.create(takeoffPublisher);
        commands.add(takeOff);
        final Command hoverOneSecond = Hover.create(pilotingPublisher, 1);
        commands.add(hoverOneSecond);
        final Command moveForwardOneSecond = MoveForward.create(pilotingPublisher, 0.5, 1);
        commands.add(moveForwardOneSecond);
        commands.add(hoverOneSecond);
        final Command moveBackwardTwoSeconds = MoveBackward.create(pilotingPublisher, 0.5, 2);
        commands.add(moveBackwardTwoSeconds);
        commands.add(hoverOneSecond);
        commands.add(moveForwardOneSecond);
        final Command moveLeftOneSecond = MoveLeft.create(pilotingPublisher, 0.5, 1);
        commands.add(moveLeftOneSecond);
        commands.add(hoverOneSecond);
        final Command moveRightTwoSeconds = MoveRight.create(pilotingPublisher, 0.5, 2);
        commands.add(moveRightTwoSeconds);
        commands.add(hoverOneSecond);
        commands.add(moveLeftOneSecond);
        commands.add(hoverOneSecond);
        final Command land = Land.create(landPublisher);
        commands.add(land);

        for (final Command command : commands) {
            command.execute();
        }

        connectedNode.shutdown();
    }
}
