package commands;

import com.google.common.base.Optional;
import control.dto.DroneStateStamped;
import control.dto.Pose;
import control.localization.StateEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.VelocityService;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Command for hovering. This command requests the drone to hover by publishing a zero velocity and wait for a
 * certain amount of time.
 *
 * @author Hoang Tung Dinh
 */
public final class Hover implements Command {
    private static final Logger logger = LoggerFactory.getLogger(Hover.class);

    private final VelocityService velocityService;
    private final StateEstimator stateEstimator;
    private final double durationInSeconds;

    private Hover(VelocityService velocityService, StateEstimator stateEstimator, double durationInSeconds) {
        this.velocityService = velocityService;
        this.stateEstimator = stateEstimator;
        this.durationInSeconds = durationInSeconds;
    }

    /**
     * Creates a hover command.
     *
     * @param velocityService the velocity service of the drone
     * @param stateEstimator the state estimator of the drone
     * @param durationInSeconds the duration that the drone will hover
     * @return a hover command
     */
    public static Hover create(VelocityService velocityService, StateEstimator stateEstimator,
            double durationInSeconds) {
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f", durationInSeconds));
        return new Hover(velocityService, stateEstimator, durationInSeconds);
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
        final Command moveToPose = MoveToPose.builder()
                .withVelocityService(velocityService)
                .withStateEstimator(stateEstimator)
                .withGoalPose(currentPose)
                .withDurationInSeconds(durationInSeconds)
                .build();

        moveToPose.execute();
    }
}
