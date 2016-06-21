package commands;

import control.dto.Direction;
import services.FlipService;

/**
 * Command for flipping.
 *
 * @author Hoang Tung Dinh
 */
public final class Flip implements Command {

    private final FlipService flipPublisher;
    private final Direction direction;

    private Flip(FlipService flipPublisher, Direction direction) {
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
    public static Flip create(FlipService flipPublisher, Direction direction) {
        return new Flip(flipPublisher, direction);
    }

    @Override
    public void execute() {
        flipPublisher.publishFlipMessage(direction);
    }
}
