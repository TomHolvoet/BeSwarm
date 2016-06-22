package commands;

import services.ParrotTakeOffService;

/**
 * Command for taking off.
 *
 * @author Hoang Tung Dinh
 */
public final class Takeoff implements Command {

    private final ParrotTakeOffService takeoffPublisher;

    private Takeoff(ParrotTakeOffService takeoffPublisher) {
        this.takeoffPublisher = takeoffPublisher;
    }

    public static Takeoff create(ParrotTakeOffService takeoffPublisher) {
        return new Takeoff(takeoffPublisher);
    }

    @Override
    public void execute() {
        takeoffPublisher.sendTakingOffMessage();
    }
}
