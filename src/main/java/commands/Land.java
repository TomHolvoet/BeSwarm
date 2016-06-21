package commands;

import services.LandService;

/**
 * Command for landing.
 *
 * @author Hoang Tung Dinh
 */
public final class Land implements Command {

    private final LandService landPublisher;

    private Land(LandService landPublisher) {
        this.landPublisher = landPublisher;
    }

    /**
     * @param landPublisher the land publisher
     * @return an instance of the landing command
     */
    public static Land create(LandService landPublisher) {
        return new Land(landPublisher);
    }

    @Override
    public void execute() {
        landPublisher.publishLandingMessage();
    }
}
