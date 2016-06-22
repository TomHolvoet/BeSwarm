package commands;

import services.TakeOffService;

/**
 * Command for taking off.
 *
 * @author Hoang Tung Dinh
 */
public final class Takeoff implements Command {

    private final TakeOffService takeOffService;

    private Takeoff(TakeOffService takeOffService) {
        this.takeOffService = takeOffService;
    }

    public static Takeoff create(TakeOffService takeOffService) {
        return new Takeoff(takeOffService);
    }

    @Override
    public void execute() {
        takeOffService.sendTakingOffMessage();
    }
}
