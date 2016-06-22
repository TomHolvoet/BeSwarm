package commands;

import control.dto.Direction;
import services.ParrotFlipService;

/**
 * Command for flipping.
 *
 * @author Hoang Tung Dinh
 */
public final class Flip implements Command {

    private final ParrotFlipService flipPublisher;
    private final Direction direction;

    private Flip(ParrotFlipService flipPublisher, Direction direction) {
        this.flipPublisher = flipPublisher;
        this.direction = direction;
    }

    /**
     * Create a flipping command.
     *
     * @param flipPublisher the flip publisher
     * @param direction  the flipping direction
     * @return an instance of the flipping command
     */
    public static Flip create(ParrotFlipService flipPublisher, Direction direction) {
        return new Flip(flipPublisher, direction);
    }

    @Override
    public void execute() {
        flipPublisher.sendFlipMessage(direction);
    }
}
