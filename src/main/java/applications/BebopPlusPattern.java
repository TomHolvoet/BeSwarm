package applications;

import commands.Command;
import commands.Land;
import commands.MoveForward;
import commands.Takeoff;
import geometry_msgs.Twist;
import services.ParrotLandService;
import services.ParrotTakeOffService;
import services.ParrotVelocityService;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import std_msgs.Empty;

import java.util.ArrayList;
import java.util.List;

/**
 * This node controls the drone flying with a plus "+" patter.
 *
 * @author Hoang Tung Dinh
 */
public class BebopPlusPattern extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopController");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final ParrotTakeOffService takeoffPublisher = ParrotTakeOffService.create(
                connectedNode.<Empty>newPublisher("/bebop/takeoff", Empty._TYPE));
        final ParrotLandService landPublisher = ParrotLandService.create(
                connectedNode.<Empty>newPublisher("/bebop/land", Empty._TYPE));
        final ParrotVelocityService velocityPublisher = ParrotVelocityService.builder()
                .publisher(connectedNode.<Twist>newPublisher("/bebop/cmd_vel", Twist._TYPE))
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
//        final Command hoverOneSecond = Hover.create(velocityPublisher, 1);
//        commands.add(hoverOneSecond);
        final Command moveForwardOneSecond = MoveForward.create(velocityPublisher, 0.5, 1);
        commands.add(moveForwardOneSecond);
//        commands.add(hoverOneSecond);
//        final Command moveBackwardTwoSeconds = MoveBackward.create(velocityPublisher, 0.5, 2);
//        commands.add(moveBackwardTwoSeconds);
//        commands.add(hoverOneSecond);
//        commands.add(moveForwardOneSecond);
//        final Command moveLeftOneSecond = MoveLeft.create(velocityPublisher, 0.5, 1);
//        commands.add(moveLeftOneSecond);
//        commands.add(hoverOneSecond);
//        final Command moveRightTwoSeconds = MoveRight.create(velocityPublisher, 0.5, 2);
//        commands.add(moveRightTwoSeconds);
//        commands.add(hoverOneSecond);
//        commands.add(moveLeftOneSecond);
//        commands.add(hoverOneSecond);
        final Command land = Land.create(landPublisher);
        commands.add(land);

        for (final Command command : commands) {
            command.execute();
        }

        connectedNode.shutdown();
    }
}
