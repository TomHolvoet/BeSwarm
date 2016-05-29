package bebopbehavior;

import comm.VelocityPublisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class MoveDown implements Command {

    private final VelocityPublisher velocityPublisher;
    private final double speed;
    private final double durationInSeconds;

    private MoveDown(VelocityPublisher velocityPublisher, double speed, double durationInSeconds) {
        this.velocityPublisher = velocityPublisher;
        this.speed = speed;
        this.durationInSeconds = durationInSeconds;
    }

    public static MoveDown create(VelocityPublisher velocityPublisher, double speed, double durationInSeconds) {
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        return new MoveDown(velocityPublisher, speed, durationInSeconds);
    }

    @Override
    public void execute() {
        final Velocity velocity = Velocity.builder().linearZ(-speed).build();
        final Command move = Move.create(velocityPublisher, velocity, durationInSeconds);
        move.execute();
    }
}
