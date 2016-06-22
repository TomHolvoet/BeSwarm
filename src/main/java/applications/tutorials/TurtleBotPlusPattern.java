package applications.tutorials;

import commands.Command;
import commands.Hover;
import commands.MoveBackward;
import commands.MoveForward;
import commands.MoveLeft;
import commands.MoveRight;
import geometry_msgs.Twist;
import services.ParrotVelocityService;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import services.VelocityService;

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
        final VelocityService velocityService = ParrotVelocityService.builder()
                .publisher(connectedNode.<Twist>newPublisher("/turtle1/cmd_vel", Twist._TYPE))
                .build();

        final List<Command> commands = new ArrayList<>();
        final Command hoverOneSecond = Hover.create(velocityService, 1);
        commands.add(hoverOneSecond);
        final Command moveForwardOneSecond = MoveForward.create(velocityService, 0.5, 1);
        commands.add(moveForwardOneSecond);
        commands.add(hoverOneSecond);
        final Command moveBackwardTwoSeconds = MoveBackward.create(velocityService, 0.5, 2);
        commands.add(moveBackwardTwoSeconds);
        commands.add(hoverOneSecond);
        commands.add(moveForwardOneSecond);
        final Command moveLeftOneSecond = MoveLeft.create(velocityService, 0.5, 1);
        commands.add(moveLeftOneSecond);
        commands.add(hoverOneSecond);
        final Command moveRightTwoSeconds = MoveRight.create(velocityService, 0.5, 2);
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
