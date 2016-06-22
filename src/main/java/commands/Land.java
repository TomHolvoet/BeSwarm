package commands;

import services.ParrotLandService;

/**
 * Command for landing.
 *
 * @author Hoang Tung Dinh
 */
public final class Land implements Command {

    private final ParrotLandService landPublisher;

    private Land(ParrotLandService landPublisher) {
        this.landPublisher = landPublisher;
    }

    /**
     * @param landPublisher the land publisher
     * @return an instance of the landing command
     */
    public static Land create(ParrotLandService landPublisher) {
        return new Land(landPublisher);
    }

    @Override
    public void execute() {
        landPublisher.sendLandingMessage();
    }
}
