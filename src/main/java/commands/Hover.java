package commands;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import services.ParrotVelocityService;

/**
 * Command for hovering. This command requests the drone to hover by publishing a zero velocity and wait for a
 * certain amount of time.
 *
 * @author Hoang Tung Dinh
 */
public final class Hover implements Command {

    private final ParrotVelocityService velocityPublisher;
    private final double durationInSeconds;

    private Hover(ParrotVelocityService velocityPublisher, double durationInSeconds) {
        this.velocityPublisher = velocityPublisher;
        this.durationInSeconds = durationInSeconds;
    }

    /**
     * @param velocityPublisher the velocity publisher
     * @param durationInSeconds the duration that the drone hovers
     * @return an instance of the hovering command
     */
    public static Hover create(ParrotVelocityService velocityPublisher, double durationInSeconds) {
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f", durationInSeconds));
        return new Hover(velocityPublisher, durationInSeconds);
    }

    @Override
    public void execute() {
        final Command stopMoving = StopMoving.create(velocityPublisher);
        stopMoving.execute();
        final long durationInMilliSeconds = (long) (durationInSeconds * 1000);
        try {
            MILLISECONDS.sleep(durationInMilliSeconds);
        } catch (InterruptedException e) {
            // TODO write to log
        }
    }
}
