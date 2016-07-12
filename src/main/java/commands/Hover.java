package commands;

import com.google.common.base.Optional;
import control.dto.DroneStateStamped;
import control.dto.Pose;
import control.localization.StateEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Velocity4dService;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Command for hovering. This command requests the drone to hover by publishing a zero velocity and wait for a
 * certain amount of time.
 *
 * @author Hoang Tung Dinh
 */
public final class Hover implements Command {
    private static final Logger logger = LoggerFactory.getLogger(Hover.class);

    private final Velocity4dService velocity4dService;
    private final StateEstimator stateEstimator;
    private final double durationInSeconds;

    private Hover(Velocity4dService velocity4dService, StateEstimator stateEstimator, double durationInSeconds) {
        this.velocity4dService = velocity4dService;
        this.stateEstimator = stateEstimator;
        this.durationInSeconds = durationInSeconds;
    }

    public static Hover create(Velocity4dService velocity4dService, StateEstimator stateEstimator,
            double durationInSeconds) {
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f", durationInSeconds));
        return new Hover(velocity4dService, stateEstimator, durationInSeconds);
    }

    @Override
    public void execute() {
        logger.debug("Execute hover command.");
        final Optional<DroneStateStamped> currentState = stateEstimator.getCurrentState();
        if (!currentState.isPresent()) {
            logger.info("Cannot get the current state. Hover command will be ignored.");
            return;
        }

        final Pose currentPose = currentState.get().pose();
        // TODO do we need customized pid params here?
        final Command moveToPose = MoveToPose.builder()
                .withVelocity4dService(velocity4dService)
                .withStateEstimator(stateEstimator)
                .withGoalPose(currentPose)
                .withDurationInSeconds(durationInSeconds)
                .build();

        moveToPose.execute();
    }
}
