package services.parrot;

import com.google.common.base.Optional;
import geometry_msgs.Twist;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FlipService;
import services.VelocityService;

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
    public VelocityService createVelocityService() {
        final String topicName = "/cmd_vel";
        final VelocityService velocityService = ParrotVelocityService.builder()
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
        return velocityService;
    }

    @Override
    public FlipService createFlipService() {
        throw new UnsupportedOperationException("Tum simulator does not support flip service.");
    }
}
