package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.TakeOffService;

import java.util.concurrent.TimeUnit;

/**
 * Command for taking off.
 *
 * @author Hoang Tung Dinh
 */
public final class Takeoff implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Takeoff.class);

    private final TakeOffService takeOffService;

    private Takeoff(TakeOffService takeOffService) {
        this.takeOffService = takeOffService;
    }

    /**
     * Creates a take off command.
     *
     * @param takeOffService the take off service of the drone
     * @return a take off command
     */
    public static Takeoff create(TakeOffService takeOffService) {
        return new Takeoff(takeOffService);
    }

    @Override
    public void execute() {
        logger.debug("Execute take off command.");
        takeOffService.sendTakingOffMessage();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            logger.debug("Waiting after sending taking off message is interrupted.", e);
            Thread.currentThread().interrupt();
        }
    }
}
