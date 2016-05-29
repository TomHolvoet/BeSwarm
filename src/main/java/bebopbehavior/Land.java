package bebopbehavior;

import comm.LandPublisher;

/**
 * Command for landing
 *
 * @author Hoang Tung Dinh
 */
public final class Land implements Command {

    private final LandPublisher landPublisher;

    private Land(LandPublisher landPublisher) {
        this.landPublisher = landPublisher;
    }

    public static Land create(LandPublisher landPublisher) {
        return new Land(landPublisher);
    }

    @Override
    public void execute() {
        landPublisher.publishLandCommand();
    }
}
