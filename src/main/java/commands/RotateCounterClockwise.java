package commands;

import static com.google.common.base.Preconditions.checkArgument;

import control.dto.InertialFrameVelocity;
import services.VelocityService;

/**
 * Command for rotating counterclockwise. It is a facade which uses {@link Move}.
 *
 * @author Hoang Tung Dinh
 */
public final class RotateCounterClockwise implements Command {

    private final VelocityService velocityService;
    private final double speed;
    private final double durationInSeconds;

    private RotateCounterClockwise(VelocityService velocityService, double speed, double durationInSeconds) {
        this.velocityService = velocityService;
        this.speed = speed;
        this.durationInSeconds = durationInSeconds;
    }

    public static RotateCounterClockwise create(VelocityService velocityService, double speed,
            double durationInSeconds) {
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f", durationInSeconds));
        checkArgument(speed > 0, String.format("Speed must be a positive value, but it is %f", speed));
        return new RotateCounterClockwise(velocityService, speed, durationInSeconds);
    }

    @Override
    public void execute() {
        final InertialFrameVelocity velocity = InertialFrameVelocity.builder().angularZ(speed).build();
        final Command move = Move.builder()
                .velocityPublisher(velocityService)
                .velocity(velocity)
                .durationInSeconds(durationInSeconds)
                .build();
        move.execute();
    }
}
