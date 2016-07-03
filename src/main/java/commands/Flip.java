package commands;

import control.dto.FlipDirection;
import services.FlipService;

/**
 * Command for flipping.
 *
 * @author Hoang Tung Dinh
 */
public final class Flip implements Command {

    private final FlipService flipService;
    private final FlipDirection flipDirection;

    private Flip(FlipService flipService, FlipDirection flipDirection) {
        this.flipService = flipService;
        this.flipDirection = flipDirection;
    }

    /**
     * Create a flipping command.
     *
     * @param flipService the flip publisher
     * @param flipDirection  the flipping flipDirection
     * @return an instance of the flipping command
     */
    public static Flip create(FlipService flipService, FlipDirection flipDirection) {
        return new Flip(flipService, flipDirection);
    }

    @Override
    public void execute() {
        flipService.sendFlipMessage(flipDirection);
    }
}
