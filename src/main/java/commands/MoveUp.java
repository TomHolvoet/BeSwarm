package commands;

import static com.google.common.base.Preconditions.checkArgument;

import control.dto.InertialFrameVelocity;
import control.dto.Velocity;
import services.VelocityService;

/**
 * Command for moving up (increasing the altitude). It is a facade which uses {@link Move}.
 *
 * @author Hoang Tung Dinh
 */
public final class MoveUp implements Command {

    private final VelocityService velocityService;
    private final double speed;
    private final double durationInSeconds;

    private MoveUp(VelocityService velocityService, double speed, double durationInSeconds) {
        this.velocityService = velocityService;
        this.speed = speed;
        this.durationInSeconds = durationInSeconds;
    }

    public static MoveUp create(VelocityService velocityService, double speed, double durationInSeconds) {
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f", durationInSeconds));
        checkArgument(speed > 0, String.format("Speed must be a positive value, but it is %f", speed));
        return new MoveUp(velocityService, speed, durationInSeconds);
    }

    @Override
    public void execute() {
        // FIXME
        final InertialFrameVelocity velocity = Velocity.builder().linearZ(speed).build();
        final Command move = Move.builder()
                .velocityPublisher(velocityService)
                .velocity(velocity)
                .durationInSeconds(durationInSeconds)
                .build();
        move.execute();
    }
}
