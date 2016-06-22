package applications;

import commands.Command;
import commands.Hover;
import commands.Land;
import commands.MoveCircleClockwise;
import commands.Takeoff;
import geometry_msgs.Twist;
import services.LandService;
import services.ParrotLandService;
import services.ParrotTakeOffService;
import services.ParrotVelocityService;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import services.TakeOffService;
import services.VelocityService;
import std_msgs.Empty;

import java.util.ArrayList;
import java.util.List;

/**
 * This node controls the drone flying as a circle.
 *
 * @author Hoang Tung Dinh
 */
public class BebopCirclePattern extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopController");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final TakeOffService takeOffService = ParrotTakeOffService.create(
                connectedNode.<Empty>newPublisher("/bebop/takeoff", Empty._TYPE));
        final LandService landService = ParrotLandService.create(
                connectedNode.<Empty>newPublisher("/bebop/land", Empty._TYPE));
        final VelocityService velocityService = ParrotVelocityService.builder()
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
        final Command takeOff = Takeoff.create(takeOffService);
        commands.add(takeOff);
        final Command hoverOneSecond = Hover.create(velocityService, 1);
        commands.add(hoverOneSecond);
        final Command moveCircle = MoveCircleClockwise.create(velocityService, 0.5, 0.5, 3);
        commands.add(moveCircle);
        commands.add(hoverOneSecond);
        final Command land = Land.create(landService);
        commands.add(land);

        for (final Command command : commands) {
            command.execute();
        }

        connectedNode.shutdown();
    }
}
