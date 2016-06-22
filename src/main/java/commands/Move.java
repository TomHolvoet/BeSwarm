package commands;

import static com.google.common.base.Preconditions.checkArgument;

import commands.schedulers.PeriodicTaskRunner;
import control.dto.Velocity;
import services.VelocityService;

/**
 * Command for moving. This command sends velocity message to the drone at a fixed rate in a fixed duration. (e.g.,
 * send message after each 50 milliseconds (20Hz) in 10 seconds).
 *
 * @author Hoang Tung Dinh
 */
public final class Move implements Command {

    private final VelocityService velocityService;
    private final Velocity velocity;
    private final double durationInSeconds;
    private final double sendingRateInSeconds;

    private static final double DEFAULT_SENDING_RATE_IN_SECONDS = 0.05;

    private Move(Builder builder) {
        velocityService = builder.velocityService;
        velocity = builder.velocity;
        durationInSeconds = builder.durationInSeconds;
        sendingRateInSeconds = builder.sendingRateInSeconds;
        checkArgument(durationInSeconds > 0,
                String.format("Duration must be a positive value, but it is %f", durationInSeconds));
        checkArgument(sendingRateInSeconds > 0,
                String.format("Sending rate must be a positive value, but it is %f", sendingRateInSeconds));
    }

    /**
     * {@link Builder#sendingRateInSeconds(double)} is optional. All other parameters are mandatory.
     *
     * @return
     */
    public static Builder builder() {
        return new Builder().sendingRateInSeconds(DEFAULT_SENDING_RATE_IN_SECONDS);
    }

    @Override
    public void execute() {
        final Runnable sendCommand = new SendCommand();
        PeriodicTaskRunner.run(sendCommand, sendingRateInSeconds, durationInSeconds);
        final Command stopMoving = StopMoving.create(velocityService);
        stopMoving.execute();
    }

    private final class SendCommand implements Runnable {
        private SendCommand() {}

        @Override
        public void run() {
            velocityService.sendVelocityMessage(velocity);
        }
    }

    /**
     * {@code Move} builder static inner class.
     */
    public static final class Builder {
        private VelocityService velocityService;
        private Velocity velocity;
        private double durationInSeconds;
        private double sendingRateInSeconds;

        private Builder() {}

        /**
         * Sets the {@code velocityService} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code velocityService} to set
         * @return a reference to this Builder
         */
        public Builder velocityPublisher(VelocityService val) {
            velocityService = val;
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
         * Sets the {@code sendingRateInSeconds} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code sendingRateInSeconds} to set
         * @return a reference to this Builder
         */
        public Builder sendingRateInSeconds(double val) {
            sendingRateInSeconds = val;
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
