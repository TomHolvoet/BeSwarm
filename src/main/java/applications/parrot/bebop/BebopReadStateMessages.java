package applications.parrot.bebop;

import bebop_msgs.Ardrone3PilotingStateFlyingStateChanged;
import com.google.common.base.Optional;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ros_subscribers.MessagesSubscriberService;

import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
public class BebopReadStateMessages extends AbstractNodeMain {
    private static final Logger logger = LoggerFactory.getLogger(BebopReadStateMessages.class);

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopReadStateMessages");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final MessagesSubscriberService<Ardrone3PilotingStateFlyingStateChanged> flyingStateProvider =
                MessagesSubscriberService
                .create(connectedNode.<Ardrone3PilotingStateFlyingStateChanged>newSubscriber(
                        "/bebop/states/ARDrone3/PilotingState/FlyingStateChanged",
                        Ardrone3PilotingStateFlyingStateChanged._TYPE));
        while (true) {
            final Optional<Ardrone3PilotingStateFlyingStateChanged> flyingStateOptional = flyingStateProvider
                    .getMostRecentMessage();
            if (flyingStateOptional.isPresent()) {
                logger.debug(String.valueOf(flyingStateOptional.get().getState()));
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    logger.debug("Sleep is interrupted.", e);
                }
            }
        }
    }
}
