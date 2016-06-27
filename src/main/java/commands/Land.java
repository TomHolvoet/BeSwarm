package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.LandService;

/**
 * Command for landing.
 *
 * @author Hoang Tung Dinh
 */
public final class Land implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Land.class);
    private final LandService landService;

    private Land(LandService landService) {
        this.landService = landService;
    }

    /**
     * @param landService the land publisher
     * @return an instance of the landing command
     */
    public static Land create(LandService landService) {
        return new Land(landService);
    }

    @Override
    public void execute() {
        logger.debug("Execute land command.");
        landService.sendLandingMessage();
    }
}
