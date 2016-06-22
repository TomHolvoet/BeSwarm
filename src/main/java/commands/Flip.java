package commands;

import control.dto.Direction;
import services.FlipService;

/**
 * Command for flipping.
 *
 * @author Hoang Tung Dinh
 */
public final class Flip implements Command {

    private final FlipService flipService;
    private final Direction direction;

    private Flip(FlipService flipService, Direction direction) {
        this.flipService = flipService;
        this.direction = direction;
    }

    /**
     * Create a flipping command.
     *
     * @param flipService the flip publisher
     * @param direction  the flipping direction
     * @return an instance of the flipping command
     */
    public static Flip create(FlipService flipService, Direction direction) {
        return new Flip(flipService, direction);
    }

    @Override
    public void execute() {
        flipService.sendFlipMessage(direction);
    }
}
