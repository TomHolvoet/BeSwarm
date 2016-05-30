package bebopbehavior;

import comm.TakeoffPublisher;

/**
 * Command for taking off.
 *
 * @author Hoang Tung Dinh
 */
public final class Takeoff implements Command {

    private final TakeoffPublisher takeoffPublisher;

    private Takeoff(TakeoffPublisher takeoffPublisher) {
        this.takeoffPublisher = takeoffPublisher;
    }

    public static Takeoff create(TakeoffPublisher takeoffPublisher) {
        return new Takeoff(takeoffPublisher);
    }

    @Override
    public void execute() {
        takeoffPublisher.publishTakingOffMessage();
    }
}
