package bebopbehavior;

import comm.FlipPublisher;

/**
 * @author Hoang Tung Dinh
 */
public final class Flip implements Command {

    private final FlipPublisher flipPublisher;
    private final Direction direction;

    private Flip(FlipPublisher flipPublisher, Direction direction) {
        this.flipPublisher = flipPublisher;
        this.direction = direction;
    }

    public static Flip create(FlipPublisher flipPublisher, Direction direction) {
        return new Flip(flipPublisher, direction);
    }

    @Override
    public void execute() {
        flipPublisher.publishFlipCommand(direction);
    }
}
