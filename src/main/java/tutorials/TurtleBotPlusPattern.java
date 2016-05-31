package tutorials;

import bebopbehavior.Command;
import bebopbehavior.Hover;
import bebopbehavior.MoveBackward;
import bebopbehavior.MoveForward;
import bebopbehavior.MoveLeft;
import bebopbehavior.MoveRight;
import comm.VelocityPublisher;
import geometry_msgs.Twist;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple node that control the turtle bot moving with a plus pattern.
 *
 * @author Hoang Tung Dinh
 */
public class TurtleBotPlusPattern extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("TungsTurtleBot");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final VelocityPublisher velocityPublisher = VelocityPublisher.builder()
                .publisher(connectedNode.<Twist>newPublisher("/turtle1/cmd_vel", Twist._TYPE))
                .build();

        final List<Command> commands = new ArrayList<>();
        final Command hoverOneSecond = Hover.create(velocityPublisher, 1);
        commands.add(hoverOneSecond);
        final Command moveForwardOneSecond = MoveForward.create(velocityPublisher, 0.5, 1);
        commands.add(moveForwardOneSecond);
        commands.add(hoverOneSecond);
        final Command moveBackwardTwoSeconds = MoveBackward.create(velocityPublisher, 0.5, 2);
        commands.add(moveBackwardTwoSeconds);
        commands.add(hoverOneSecond);
        commands.add(moveForwardOneSecond);
        final Command moveLeftOneSecond = MoveLeft.create(velocityPublisher, 0.5, 1);
        commands.add(moveLeftOneSecond);
        commands.add(hoverOneSecond);
        final Command moveRightTwoSeconds = MoveRight.create(velocityPublisher, 0.5, 2);
        commands.add(moveRightTwoSeconds);
        commands.add(hoverOneSecond);
        commands.add(moveLeftOneSecond);
        commands.add(hoverOneSecond);

        for (final Command command : commands) {
            command.execute();
        }

        connectedNode.shutdown();
    }
}