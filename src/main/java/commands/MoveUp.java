package commands;

import static com.google.common.base.Preconditions.checkArgument;

import control.dto.Velocity;
import services.ParrotVelocityService;

/**
 * Command for moving up (increasing the altitude). It is a facade which uses {@link Move}.
 *
 * @author Hoang Tung Dinh
 */
public final class MoveUp implements Command {

    private final ParrotVelocityService velocityPublisher;
    private final double speed;
    private final double durationInSeconds;

    private MoveUp(ParrotVelocityService velocityPublisher, double speed, double durationInSeconds) {
        this.velocityPublisher = velocityPublisher;
        this.speed = speed;
        this.durationInSeconds = durationInSeconds;
    }

    public static MoveUp create(ParrotVelocityService velocityPublisher, double speed, double durationInSeconds) {
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f", durationInSeconds));
        checkArgument(speed > 0, String.format("Speed must be a positive value, but it is %f", speed));
        return new MoveUp(velocityPublisher, speed, durationInSeconds);
    }

    @Override
    public void execute() {
        final Velocity velocity = Velocity.builder().linearZ(speed).build();
        final Command move = Move.builder()
                .velocityPublisher(velocityPublisher)
                .velocity(velocity)
                .durationInSeconds(durationInSeconds)
                .build();
        move.execute();
    }
}
