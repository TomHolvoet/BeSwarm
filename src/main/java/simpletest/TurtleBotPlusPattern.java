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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hoang Tung Dinh
 */
public class TurtleBotPlusPattern extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("TungsTurtleBot");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final Publisher<Twist> publisher = connectedNode.newPublisher("/turtle1/cmd_vel", Twist._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {
            private List<Command> commands;

            @Override
            protected void setup() {
                commands = new ArrayList<>();
                final Command hoverOneSecond = Hover.create(publisher, 1);
                commands.add(hoverOneSecond);
                final Command moveForwardOneSecond = MoveForward.create(publisher, 0.5, 1);
                commands.add(moveForwardOneSecond);
                commands.add(hoverOneSecond);
                final Command moveBackwardTwoSeconds = MoveBackward.create(publisher, 0.5, 2);
                commands.add(moveBackwardTwoSeconds);
                commands.add(hoverOneSecond);
                commands.add(moveForwardOneSecond);
                final Command moveLeftOneSecond = MoveLeft.create(publisher, 0.5, 1);
                commands.add(moveLeftOneSecond);
                commands.add(hoverOneSecond);
                final Command moveRightTwoSeconds = MoveRight.create(publisher, 0.5, 2);
                commands.add(moveRightTwoSeconds);
                commands.add(hoverOneSecond);
                commands.add(moveLeftOneSecond);
                commands.add(hoverOneSecond);
            }

            @Override
            protected void loop() {
                for (final Command command : commands) {
                    command.execute();
                }
            }
        });
    }
}
