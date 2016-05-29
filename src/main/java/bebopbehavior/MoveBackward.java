package bebopbehavior;

import comm.VelocityPublisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class MoveBackward implements Command {

    private final VelocityPublisher velocityPublisher;
    private final double speed;
    private final double durationInSeconds;

    private MoveBackward(VelocityPublisher velocityPublisher, double speed, double durationInSeconds) {
        this.velocityPublisher = velocityPublisher;
        this.speed = speed;
        this.durationInSeconds = durationInSeconds;
    }

    public static MoveBackward create(VelocityPublisher velocityPublisher, double speed, double durationInSeconds) {
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        return new MoveBackward(velocityPublisher, speed, durationInSeconds);
    }

    @Override
    public void execute() {
        final Velocity velocity = Velocity.builder().linearX(-speed).build();
        final Command move = Move.create(velocityPublisher, velocity, durationInSeconds);
        move.execute();
    }
}
