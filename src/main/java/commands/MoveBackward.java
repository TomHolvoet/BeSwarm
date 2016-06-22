package commands;

import control.dto.Velocity;
import services.VelocityService;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Command for moving backward. It is a facade which uses {@link Move}.
 *
 * @author Hoang Tung Dinh
 */
public final class MoveBackward implements Command {

    private final VelocityService velocityService;
    private final double speed;
    private final double durationInSeconds;

    private MoveBackward(VelocityService velocityService, double speed, double durationInSeconds) {
        this.velocityService = velocityService;
        this.speed = speed;
        this.durationInSeconds = durationInSeconds;
    }

    /**
     * @param velocityService the velocity publisher
     * @param speed             the speed for moving backward, must be positive, unit: meter per second
     * @param durationInSeconds the duration for moving backward
     * @return an instance of the moving backward command
     */
    public static MoveBackward create(VelocityService velocityService, double speed, double durationInSeconds) {
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f", durationInSeconds));
        checkArgument(speed > 0, String.format("Speed must be a positive value, but it is %f", speed));
        return new MoveBackward(velocityService, speed, durationInSeconds);
    }

    @Override
    public void execute() {
        final Velocity velocity = Velocity.builder().linearX(-speed).build();
        final Command move = Move.builder()
                .velocityPublisher(velocityService)
                .velocity(velocity)
                .durationInSeconds(durationInSeconds)
                .build();
        move.execute();
    }
}
