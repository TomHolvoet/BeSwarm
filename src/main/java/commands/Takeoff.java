package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.TakeOffService;

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
        logger.debug("Execute take off command.");
        takeOffService.sendTakingOffMessage();
    }
}
