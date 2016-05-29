package bebopbehavior;

import comm.VelocityPublisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class RotateCounterClockwise implements Command {

    private final VelocityPublisher velocityPublisher;
    private final double speed;
    private final double durationInSeconds;

    private RotateCounterClockwise(VelocityPublisher velocityPublisher, double speed, double durationInSeconds) {
        this.velocityPublisher = velocityPublisher;
        this.speed = speed;
        this.durationInSeconds = durationInSeconds;
    }

    public static RotateCounterClockwise create(VelocityPublisher velocityPublisher, double speed,
            double durationInSeconds) {
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        return new RotateCounterClockwise(velocityPublisher, speed, durationInSeconds);
    }

    @Override
    public void execute() {
        final Velocity velocity = Velocity.builder().angularZ(speed).build();
        final Command move = Move.create(velocityPublisher, velocity, durationInSeconds);
        move.execute();
    }
}
