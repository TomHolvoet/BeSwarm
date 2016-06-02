package behavior;

import comm.LandPublisher;

/**
 * Command for landing.
 *
 * @author Hoang Tung Dinh
 */
public final class Land implements Command {

    private final LandPublisher landPublisher;

    private Land(LandPublisher landPublisher) {
        this.landPublisher = landPublisher;
    }

    /**
     * @param landPublisher the land publisher
     * @return an instance of the landing command
     */
    public static Land create(LandPublisher landPublisher) {
        return new Land(landPublisher);
    }

    @Override
    public void execute() {
        landPublisher.publishLandingMessage();
    }
}
