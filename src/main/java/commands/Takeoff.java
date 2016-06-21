package commands;

import services.TakeoffService;

/**
 * Command for taking off.
 *
 * @author Hoang Tung Dinh
 */
public final class Takeoff implements Command {

    private final TakeoffService takeoffPublisher;

    private Takeoff(TakeoffService takeoffPublisher) {
        this.takeoffPublisher = takeoffPublisher;
    }

    public static Takeoff create(TakeoffService takeoffPublisher) {
        return new Takeoff(takeoffPublisher);
    }

    @Override
    public void execute() {
        takeoffPublisher.publishTakingOffMessage();
    }
}
