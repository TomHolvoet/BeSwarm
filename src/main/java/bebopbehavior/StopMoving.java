package bebopbehavior;

import comm.VelocityPublisher;

/**
 * @author Hoang Tung Dinh
 */
public final class StopMoving implements Command {

    private final VelocityPublisher velocityPublisher;

    private StopMoving(VelocityPublisher velocityPublisher) {
        this.velocityPublisher = velocityPublisher;
    }

    public static StopMoving create(VelocityPublisher velocityPublisher) {return new StopMoving(velocityPublisher);}

    @Override
    public void execute() {
        final Velocity velocity = Velocity.builder().linearX(0).linearY(0).linearZ(0).angularZ(0).build();
        velocityPublisher.publishVelocityCommand(velocity);
    }
}
