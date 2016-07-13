package services.parrot;

import ardrone_autonomy.Navdata;
import geometry_msgs.Twist;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FlyingStateService;
import services.Velocity4dService;
import services.rossubscribers.MessagesSubscriberService;

/**
 * @author Hoang Tung Dinh
 */
public final class TumSimServiceFactory extends ParrotServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(TumSimServiceFactory.class);
    private static final String DRONE_NAME = "ardrone";

    private TumSimServiceFactory(ConnectedNode connectedNode) {
        super(connectedNode, DRONE_NAME);
    }

    public static TumSimServiceFactory create(ConnectedNode connectedNode) {
        return new TumSimServiceFactory(connectedNode);
    }

    @Override
    public Velocity4dService createVelocity4dService() {
        final String topicName = "/cmd_vel";
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

    @Override
    public FlyingStateService createFlyingStateService() {
        final String topicName = "/" + DRONE_NAME + "/navdata";
        final MessagesSubscriberService<Navdata> flyingStateSubscriber = MessagesSubscriberService.create(
                getConnectedNode().<Navdata>newSubscriber(topicName, Navdata._TYPE));

        return TumSimFlyingStateService.create(flyingStateSubscriber);
    }
}
