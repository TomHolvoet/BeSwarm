package bebopbehavior;

import comm.VelocityPublisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class Move implements Command {

    private final VelocityPublisher velocityPublisher;
    private final Velocity velocity;
    private final double durationInSeconds;

    private Move(VelocityPublisher velocityPublisher, Velocity velocity, double durationInSeconds) {
        this.velocityPublisher = velocityPublisher;
        this.velocity = velocity;
        this.durationInSeconds = durationInSeconds;
    }

    public static Move create(VelocityPublisher velocityPublisher, Velocity velocity, double durationInSeconds) {
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        return new Move(velocityPublisher, velocity, durationInSeconds);
    }

    @Override
    public void execute() {
        final long durationInMilliSeconds = (long) (durationInSeconds * 1000);
        final long startTime = System.currentTimeMillis();

        while (true) {
            velocityPublisher.publishVelocityCommand(velocity);
            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
                // TODO write to log
            }

            final long duration = System.currentTimeMillis() - startTime;
            if (duration >= durationInMilliSeconds) {
                break;
            }
        }

        final Command stopMoving = StopMoving.create(velocityPublisher);
        stopMoving.execute();
    }
}
