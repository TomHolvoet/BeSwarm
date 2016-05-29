package bebopbehavior;

import comm.VelocityPublisher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class Move implements Command {

    private final VelocityPublisher velocityPublisher;
    private final Velocity velocity;
    private final double durationInSeconds;
    private final long sendingRateInMilliSeconds;

    private static final long DEFAULT_SENDING_RATE_IN_MILLISECONDS = 50;

    private Move(Builder builder) {
        velocityPublisher = builder.velocityPublisher;
        velocity = builder.velocity;
        durationInSeconds = builder.durationInSeconds;
        sendingRateInMilliSeconds = builder.sendingRateInMilliSeconds;
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        checkArgument(sendingRateInMilliSeconds > 0, "Sending rate must be a positive value");
    }

    public static Builder builder() {
        return new Builder().sendingRateInMilliSeconds(DEFAULT_SENDING_RATE_IN_MILLISECONDS);
    }

    @Override
    public void execute() {
        final Runnable publishCommand = new Runnable() {
            @Override
            public void run() {
                velocityPublisher.publishVelocityCommand(velocity);
            }
        };

        final long durationInMilliSeconds = (long) (durationInSeconds * 1000);
        final Future<?> task = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(publishCommand, 0, sendingRateInMilliSeconds, TimeUnit.MILLISECONDS);

        try {
            task.get(durationInMilliSeconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // TODO add log
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            task.cancel(true);
        }

        final Command stopMoving = StopMoving.create(velocityPublisher);
        stopMoving.execute();
    }

    /**
     * {@code Move} builder static inner class.
     */
    public static final class Builder {
        private VelocityPublisher velocityPublisher;
        private Velocity velocity;
        private double durationInSeconds;
        private long sendingRateInMilliSeconds;

        private Builder() {}

        /**
         * Sets the {@code velocityPublisher} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code velocityPublisher} to set
         * @return a reference to this Builder
         */
        public Builder velocityPublisher(VelocityPublisher val) {
            velocityPublisher = val;
            return this;
        }

        /**
         * Sets the {@code velocity} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code velocity} to set
         * @return a reference to this Builder
         */
        public Builder velocity(Velocity val) {
            velocity = val;
            return this;
        }

        /**
         * Sets the {@code durationInSeconds} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code durationInSeconds} to set
         * @return a reference to this Builder
         */
        public Builder durationInSeconds(double val) {
            durationInSeconds = val;
            return this;
        }

        /**
         * Sets the {@code sendingRateInMilliSeconds} and returns a reference to this Builder so that the methods can
         * be chained together.
         *
         * @param val the {@code sendingRateInMilliSeconds} to set
         * @return a reference to this Builder
         */
        public Builder sendingRateInMilliSeconds(long val) {
            sendingRateInMilliSeconds = val;
            return this;
        }

        /**
         * Returns a {@code Move} built from the parameters previously set.
         *
         * @return a {@code Move} built with parameters of this {@code Move.Builder}
         */
        public Move build() {return new Move(this);}
    }
}
