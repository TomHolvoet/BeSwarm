package applications.parrot.bebop;

import commands.Command;
import commands.Land;
import commands.Takeoff;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.CommonServiceFactory;
import services.FlyingStateService;
import services.LandService;
import services.TakeOffService;
import services.parrot.BebopServiceFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
public final class BebopLandingCheck extends AbstractNodeMain {
    private static final Logger logger = LoggerFactory.getLogger(BebopLandingCheck.class);
    private static final String DRONE_NAME = "bebop";

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopSimpleLinePattern");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        try {
            final CommonServiceFactory serviceFactory = BebopServiceFactory.create(connectedNode, DRONE_NAME);
            final TakeOffService takeoffService = serviceFactory.createTakeOffService();
            final LandService landService = serviceFactory.createLandService();
            final FlyingStateService flyingStateService = serviceFactory.createFlyingStateService();

            TimeUnit.SECONDS.sleep(3);

            final Collection<Command> commands = new ArrayList<>();
            final Command takeOff = Takeoff.create(takeoffService);
            commands.add(takeOff);
            final Command land = Land.create(landService, flyingStateService);
            commands.add(land);

            for (final Command cmd : commands) {
                cmd.execute();
            }
        } catch (InterruptedException e) {
            logger.info("Warm up time is interrupted.", e);
            Thread.currentThread().interrupt();
        }
    }
}
