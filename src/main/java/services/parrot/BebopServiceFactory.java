package services.parrot;

import bebop_msgs.Ardrone3PilotingStateFlyingStateChanged;
import geometry_msgs.Twist;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FlipService;
import services.FlyingStateService;
import services.Velocity4dService;
import services.ros_subscribers.MessagesSubscriberService;
import std_msgs.UInt8;

/**
 * @author Hoang Tung Dinh
 */
public final class BebopServiceFactory extends ParrotServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(BebopServiceFactory.class);

    private BebopServiceFactory(ConnectedNode connectedNode, String droneName) {
        super(connectedNode, droneName);
    }

    /**
     * Creates a service factory for the bebop drone.
     *
     * @param connectedNode the ros node
     * @param droneName     the name of the drone
     * @return a service factory for the bebop drone
     */
    public static BebopServiceFactory create(ConnectedNode connectedNode, String droneName) {
        return new BebopServiceFactory(connectedNode, droneName);
    }

    @Override
    public Velocity4dService createVelocity4dService() {
        final String topicName = "/" + getDroneName() + "/cmd_vel";
        final Velocity4dService velocity4dService = ParrotVelocity4dService.builder()
                .publisher(getConnectedNode().<Twist>newPublisher(topicName, Twist._TYPE))
                .minLinearX(-1)
                .minLinearY(-1)
                .minLinearZ(-1)
                .minAngularZ(-1)
                .maxLinearX(1)
                .maxLinearY(1)
                .maxLinearZ(1)
                .maxAngularZ(1)
                .build();
        logger.info("Velocity service connected to {}", topicName);
        return velocity4dService;
    }

    public FlipService createFlipService() {
        final String topicName = "/" + getDroneName() + "/flip";
        final FlipService flipService = ParrotFlipService.create(
                getConnectedNode().<UInt8>newPublisher(topicName, UInt8._TYPE));
        logger.info("Flip service connected to {}", topicName);
        return flipService;
    }

    @Override
    public FlyingStateService createFlyingStateService() {
        final String topicName = "/" + getDroneName() + "/states/ARDrone3/PilotingState/FlyingStateChanged";
        final MessagesSubscriberService<Ardrone3PilotingStateFlyingStateChanged> flyingStateSubscriber =
                MessagesSubscriberService
                .create(getConnectedNode().<Ardrone3PilotingStateFlyingStateChanged>newSubscriber(topicName,
                        Ardrone3PilotingStateFlyingStateChanged._TYPE));

        return BebopFlyingStateService.create(flyingStateSubscriber);
    }
}
