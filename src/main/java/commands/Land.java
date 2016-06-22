package commands;

import services.LandService;

/**
 * Command for landing.
 *
 * @author Hoang Tung Dinh
 */
public final class Land implements Command {

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
        landService.sendLandingMessage();
    }
}
