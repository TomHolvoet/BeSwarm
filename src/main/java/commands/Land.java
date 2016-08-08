package commands;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FlyingStateService;
import services.LandService;
import services.rossubscribers.FlyingState;

import java.util.concurrent.TimeUnit;

/**
 * Command for landing.
 *
 * @author Hoang Tung Dinh
 */
public final class Land implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Land.class);
    private static final int SLEEP_DURATION_IN_MILLISECONDS = 50;

    private final LandService landService;
    private final FlyingStateService flyingStateService;

    private Land(LandService landService, FlyingStateService flyingStateService) {
        this.landService = landService;
        this.flyingStateService = flyingStateService;
    }

    /**
     * Creates a land command.
     *
     * @param landService the land service of the drone
     * @param flyingStateService the flying state service of the drone
     * @return a land command
     */
    public static Land create(LandService landService, FlyingStateService flyingStateService) {
        return new Land(landService, flyingStateService);
    }

    @Override
    public void execute() {
        logger.debug("Execute land command.");

        while (true) {
            final Optional<FlyingState> currentFlyingState = flyingStateService.getCurrentFlyingState();

            if (currentFlyingState.isPresent()) {
                if (currentFlyingState.get() == FlyingState.LANDED) {
                    logger.info("Successfully landed.");
                    return;
                }

                if (currentFlyingState.get() != FlyingState.LANDING) {
                    logger.trace("Current state is {}. Send a landing message.", currentFlyingState.get());
                    landService.sendLandingMessage();
                }
            } else {
                logger.trace("Cannot get flying state. Send a landing message.");
                landService.sendLandingMessage();
            }

            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_DURATION_IN_MILLISECONDS);
            } catch (InterruptedException e) {
                logger.debug("Sleep is interrupted in land command.", e);
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
