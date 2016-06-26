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

    public static Takeoff create(TakeOffService takeOffService) {
        return new Takeoff(takeOffService);
    }

    @Override
    public void execute() {
        logger.debug("Start taking off.");
        takeOffService.sendTakingOffMessage();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.info("Take off command is interrupted.", e);
        }
    }
}
