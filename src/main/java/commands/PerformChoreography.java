package commands;

import control.DefaultPidParameters;
import control.FiniteTrajectory4d;
import control.PidParameters;
import control.localization.StateEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.VelocityService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Hoang Tung Dinh
 */
public final class PerformChoreography implements Command {

    private static final Logger logger = LoggerFactory.getLogger(PerformChoreography.class);

    private final Command followTrajectory;

    private static final double DEFAULT_CONTROL_RATE_IN_SECONDS = 0.05;
    private static final double DEFAULT_DRONE_STATE_LIFE_DURATION_IN_SECONDS = 0.1;

    private PerformChoreography(Builder builder) {
        followTrajectory = FollowTrajectory.builder()
                .velocityService(builder.velocityService)
                .stateEstimator(builder.stateEstimator)
                .pidLinearXParameters(builder.pidLinearXParameters)
                .pidLinearYParameters(builder.pidLinearYParameters)
                .pidLinearZParameters(builder.pidLinearZParameters)
                .pidAngularZParameters(builder.pidAngularZParameters)
                .trajectory4d(builder.finiteTrajectory4d)
                .durationInSeconds(builder.finiteTrajectory4d.getTrajectoryDuration())
                .controlRateInSeconds(builder.controlRateInSeconds)
                .droneStateLifeDurationInSeconds(builder.droneStateLifeDurationInSeconds)
                .build();
    }

    public static Builder builder() {
        return new Builder().controlRateInSeconds(DEFAULT_CONTROL_RATE_IN_SECONDS)
                .pidLinearXParameters(DefaultPidParameters.LINEAR_X.getParameters())
                .pidLinearYParameters(DefaultPidParameters.LINEAR_Y.getParameters())
                .pidLinearZParameters(DefaultPidParameters.LINEAR_Z.getParameters())
                .pidAngularZParameters(DefaultPidParameters.ANGULAR_Z.getParameters())
                .droneStateLifeDurationInSeconds(DEFAULT_DRONE_STATE_LIFE_DURATION_IN_SECONDS);
    }

    @Override
    public void execute() {
        logger.debug("Execute perform choreography command.");
        followTrajectory.execute();
    }

    /**
     * {@code FollowTrajectory} builder static inner class.
     */
    public static final class Builder {
        private VelocityService velocityService;
        private StateEstimator stateEstimator;
        private PidParameters pidLinearXParameters;
        private PidParameters pidLinearYParameters;
        private PidParameters pidLinearZParameters;
        private PidParameters pidAngularZParameters;
        private FiniteTrajectory4d finiteTrajectory4d;
        private Double controlRateInSeconds;
        private Double droneStateLifeDurationInSeconds;

        private Builder() {}

        /**
         * Sets the {@code velocityService} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code velocityService} to set
         * @return a reference to this Builder
         */
        public Builder velocityService(VelocityService val) {
            velocityService = val;
            return this;
        }

        /**
         * Sets the {@code stateEstimator} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code stateEstimator} to set
         * @return a reference to this Builder
         */
        public Builder stateEstimator(StateEstimator val) {
            stateEstimator = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearXParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code pidLinearXParameters} to set
         * @return a reference to this Builder
         */
        public Builder pidLinearXParameters(PidParameters val) {
            pidLinearXParameters = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearYParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code pidLinearYParameters} to set
         * @return a reference to this Builder
         */
        public Builder pidLinearYParameters(PidParameters val) {
            pidLinearYParameters = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearZParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code pidLinearZParameters} to set
         * @return a reference to this Builder
         */
        public Builder pidLinearZParameters(PidParameters val) {
            pidLinearZParameters = val;
            return this;
        }

        /**
         * Sets the {@code pidAngularZParameters} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code pidAngularZParameters} to set
         * @return a reference to this Builder
         */
        public Builder pidAngularZParameters(PidParameters val) {
            pidAngularZParameters = val;
            return this;
        }

        /**
         * Sets the {@code finiteTrajectory4d} and returns a reference to this Builder so that the methods can be
         * chained
         * together.
         *
         * @param val the {@code finiteTrajectory4d} to set
         * @return a reference to this Builder
         */
        public Builder finiteTrajectory4d(FiniteTrajectory4d val) {
            finiteTrajectory4d = val;
            return this;
        }

        /**
         * Sets the {@code controlRateInSeconds} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code controlRateInSeconds} to set
         * @return a reference to this Builder
         */
        public Builder controlRateInSeconds(double val) {
            controlRateInSeconds = val;
            return this;
        }

        /**
         * Sets the {@code droneStateLifeDurationInSeconds} and returns a reference to this Builder so that the
         * methods can be chained together.
         *
         * @param val the {@code droneStateLifeDurationInSeconds} to set
         * @return a reference to this Builder
         */
        public Builder droneStateLifeDurationInSeconds(double val) {
            droneStateLifeDurationInSeconds = val;
            return this;
        }

        /**
         * Returns a {@code PerformChoreography} built from the parameters previously set.
         *
         * @return a {@code PerformChoreography} built with parameters of this {@code FollowTrajectory.Builder}
         */
        public PerformChoreography build() {
            checkNotNull(velocityService, "missing velocityService");
            checkNotNull(stateEstimator, "missing stateEstimator");
            checkNotNull(pidLinearXParameters, "missing pidLinearXParameters");
            checkNotNull(pidLinearYParameters, "missing pidLinearYParameters");
            checkNotNull(pidLinearZParameters, "missing pidLinearZParameters");
            checkNotNull(pidAngularZParameters, "missing pidAngularZParameters");
            checkNotNull(finiteTrajectory4d, "missing trajectory4d");
            checkNotNull(controlRateInSeconds, "missing controlRateInSeconds");
            checkNotNull(droneStateLifeDurationInSeconds, "missing droneStateLifeDurationInSeconds");
            return new PerformChoreography(this);
        }
    }
}
