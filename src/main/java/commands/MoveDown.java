package commands;

import control.dto.InertialFrameVelocity;
import services.VelocityService;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Command for moving down (decreasing the altitude). It is a facade which uses {@link Move}.
 *
 * @author Hoang Tung Dinh
 */
public final class MoveDown implements Command {

    private final VelocityService velocityService;
    private final double speed;
    private final double durationInSeconds;

    private MoveDown(VelocityService velocityService, double speed, double durationInSeconds) {
        this.velocityService = velocityService;
        this.speed = speed;
        this.durationInSeconds = durationInSeconds;
    }

    public static MoveDown create(VelocityService velocityService, double speed, double durationInSeconds) {
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f", durationInSeconds));
        checkArgument(speed > 0, String.format("Speed must be a positive value, but it is %f", speed));
        return new MoveDown(velocityService, speed, durationInSeconds);
    }

    @Override
    public void execute() {
        final InertialFrameVelocity velocity = InertialFrameVelocity.builder().linearZ(-speed).build();
        final Command move = Move.builder()
                .velocityPublisher(velocityService)
                .velocity(velocity)
                .durationInSeconds(durationInSeconds)
                .build();
        move.execute();
    }
}
