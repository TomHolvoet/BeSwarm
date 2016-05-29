package bebopcontrol;

import comm.VelocityPublisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class Hover implements Command {

    private final VelocityPublisher velocityPublisher;
    private final double durationInSeconds;

    private Hover(VelocityPublisher velocityPublisher, double durationInSeconds) {
        this.velocityPublisher = velocityPublisher;
        this.durationInSeconds = durationInSeconds;
    }

    public static Hover create(VelocityPublisher velocityPublisher, double durationInSeconds) {
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        return new Hover(velocityPublisher, durationInSeconds);
    }

    @Override
    public void execute() {
        final Command stopMoving = StopMoving.create(velocityPublisher);
        stopMoving.execute();
        final long durationInMilliSeconds = (long) (durationInSeconds * 1000);
        try {
            Thread.sleep(durationInMilliSeconds);
        } catch (InterruptedException e) {
            // TODO write to log
        }
    }
}
