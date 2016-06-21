package commands;

import control.dto.Velocity;
import services.VelocityService;

/**
 * Command for stopping moving. It publishes a velocity message.
 *
 * @author Hoang Tung Dinh
 */
public final class StopMoving implements Command {

    private final VelocityService velocityPublisher;

    private StopMoving(VelocityService velocityPublisher) {
        this.velocityPublisher = velocityPublisher;
    }

    public static StopMoving create(VelocityService velocityPublisher) {
        return new StopMoving(velocityPublisher);
    }

    @Override
    public void execute() {
        final Velocity velocity = Velocity.builder().linearX(0).linearY(0).linearZ(0).angularZ(0).build();
        velocityPublisher.publishVelocityMessage(velocity);
    }
}
