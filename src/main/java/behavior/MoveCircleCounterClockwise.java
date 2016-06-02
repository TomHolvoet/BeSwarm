package behavior;

import comm.VelocityPublisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Command for moving counterclockwise. It is a facade which uses {@link Move}.
 *
 * @author Hoang Tung Dinh
 */
public final class MoveCircleCounterClockwise implements Command {

    private final VelocityPublisher velocityPublisher;
    private final double forwardSpeed;
    private final double rotationSpeed;
    private final double durationInSeconds;

    private MoveCircleCounterClockwise(VelocityPublisher velocityPublisher, double forwardSpeed, double rotationSpeed,
            double durationInSeconds) {
        this.velocityPublisher = velocityPublisher;
        this.forwardSpeed = forwardSpeed;
        this.rotationSpeed = rotationSpeed;
        this.durationInSeconds = durationInSeconds;
    }

    public static MoveCircleCounterClockwise create(VelocityPublisher velocityPublisher, double forwardSpeed,
            double rotationSpeed, double durationInSeconds) {
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f.", durationInSeconds));
        checkArgument(forwardSpeed > 0,
                String.format("Forward speed must be a positive value, but it is %f.", forwardSpeed));
        checkArgument(rotationSpeed > 0,
                String.format("Rotation speed must be a positive value, but it is %f.", rotationSpeed));
        return new MoveCircleCounterClockwise(velocityPublisher, forwardSpeed, rotationSpeed, durationInSeconds);
    }

    @Override
    public void execute() {
        final Velocity velocity = Velocity.builder().linearX(forwardSpeed).angularZ(rotationSpeed).build();
        final Command move = Move.builder()
                .velocityPublisher(velocityPublisher)
                .velocity(velocity)
                .durationInSeconds(durationInSeconds)
                .build();
        move.execute();
    }
}
