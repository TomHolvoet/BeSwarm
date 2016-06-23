package commands;

import applications.simulations.CratesSimulatorExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.VelocityService;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Command for hovering. This command requests the drone to hover by publishing a zero velocity and wait for a
 * certain amount of time.
 *
 * @author Hoang Tung Dinh
 */
public final class Hover implements Command {
    private static final Logger logger = LoggerFactory.getLogger(Hover.class);

    private final VelocityService velocityService;
    private final double durationInSeconds;

    private Hover(VelocityService velocityService, double durationInSeconds) {
        this.velocityService = velocityService;
        this.durationInSeconds = durationInSeconds;
    }

    /**
     * @param velocityService the velocity publisher
     * @param durationInSeconds the duration that the drone hovers
     * @return an instance of the hovering command
     */
    public static Hover create(VelocityService velocityService, double durationInSeconds) {
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f", durationInSeconds));
        return new Hover(velocityService, durationInSeconds);
    }

    @Override
    public void execute() {
        final Command stopMoving = StopMoving.create(velocityService);
        stopMoving.execute();
        final long durationInMilliSeconds = (long) (durationInSeconds * 1000);
        try {
            MILLISECONDS.sleep(durationInMilliSeconds);
        } catch (InterruptedException e) {
            logger.info("Hovering is interrupted", e);
        }
    }
}
