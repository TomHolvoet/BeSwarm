package commands;

import control.dto.InertialFrameVelocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.VelocityService;

/**
 * Command for stopping moving. It publishes a velocity message.
 *
 * @author Hoang Tung Dinh
 */
public final class StopMoving implements Command {

    private static final Logger logger = LoggerFactory.getLogger(StopMoving.class);
    private final VelocityService velocityService;

    private StopMoving(VelocityService velocityService) {
        this.velocityService = velocityService;
    }

    public static StopMoving create(VelocityService velocityService) {
        return new StopMoving(velocityService);
    }

    @Override
    public void execute() {
        logger.debug("Execute stop moving command.");
        final InertialFrameVelocity velocity = InertialFrameVelocity.builder().linearX(0).linearY(0).linearZ(0)
                .angularZ(0).poseYaw(0).build();
        velocityService.sendVelocityMessage(velocity);
    }
}
