package commands;

import control.dto.InertialFrameVelocity;
import services.VelocityService;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Command for moving counterclockwise. It is a facade which uses {@link Move}.
 *
 * @author Hoang Tung Dinh
 */
public final class MoveCircleCounterClockwise implements Command {

    private final VelocityService velocityService;
    private final double forwardSpeed;
    private final double rotationSpeed;
    private final double durationInSeconds;

    private MoveCircleCounterClockwise(VelocityService velocityService, double forwardSpeed, double rotationSpeed,
            double durationInSeconds) {
        this.velocityService = velocityService;
        this.forwardSpeed = forwardSpeed;
        this.rotationSpeed = rotationSpeed;
        this.durationInSeconds = durationInSeconds;
    }

    public static MoveCircleCounterClockwise create(VelocityService velocityService, double forwardSpeed,
            double rotationSpeed, double durationInSeconds) {
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f.", durationInSeconds));
        checkArgument(forwardSpeed > 0,
                String.format("Forward speed must be a positive value, but it is %f.", forwardSpeed));
        checkArgument(rotationSpeed > 0,
                String.format("Rotation speed must be a positive value, but it is %f.", rotationSpeed));
        return new MoveCircleCounterClockwise(velocityService, forwardSpeed, rotationSpeed, durationInSeconds);
    }

    @Override
    public void execute() {
        final InertialFrameVelocity velocity = InertialFrameVelocity.builder().linearX(forwardSpeed).angularZ(rotationSpeed).build();
        final Command move = Move.builder()
                .velocityPublisher(velocityService)
                .velocity(velocity)
                .durationInSeconds(durationInSeconds)
                .build();
        move.execute();
    }
}
