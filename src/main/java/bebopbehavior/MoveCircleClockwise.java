package bebopbehavior;

import comm.VelocityPublisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class MoveCircleClockwise implements Command {

    private final VelocityPublisher velocityPublisher;
    private final double forwardSpeed;
    private final double rotationSpeed;
    private final double durationInSeconds;

    private MoveCircleClockwise(VelocityPublisher velocityPublisher, double forwardSpeed, double rotationSpeed,
            double durationInSeconds) {
        this.velocityPublisher = velocityPublisher;
        this.forwardSpeed = forwardSpeed;
        this.rotationSpeed = rotationSpeed;
        this.durationInSeconds = durationInSeconds;
    }

    public static MoveCircleClockwise create(VelocityPublisher velocityPublisher, double forwardSpeed,
            double rotationSpeed, double durationInSeconds) {
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        return new MoveCircleClockwise(velocityPublisher, forwardSpeed, rotationSpeed, durationInSeconds);
    }

    @Override
    public void execute() {
        final Velocity velocity = Velocity.builder().linearX(forwardSpeed).angularZ(-rotationSpeed).build();
        final Command move = Move.create(velocityPublisher, velocity, durationInSeconds);
        move.execute();
    }
}
