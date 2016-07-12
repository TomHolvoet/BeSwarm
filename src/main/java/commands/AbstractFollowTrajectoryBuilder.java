package commands;

import control.DefaultPidParameters;
import control.PidParameters;
import control.localization.StateEstimator;
import services.Velocity4dService;

/**
 * @author Hoang Tung Dinh
 * @author Rinde van Lon
 * @author Kristof Coninx
 */
abstract class AbstractFollowTrajectoryBuilder<T extends AbstractFollowTrajectoryBuilder<T>> {
    PidParameters pidLinearXParameters;
    PidParameters pidLinearYParameters;
    PidParameters pidLinearZParameters;
    PidParameters pidAngularZParameters;
    double controlRateInSeconds;
    double droneStateLifeDurationInSeconds;
    StateEstimator stateEstimator;
    Velocity4dService velocity4dService;

    private static final double DEFAULT_CONTROL_RATE_IN_SECONDS = 0.05;
    private static final double DEFAULT_DRONE_STATE_LIFE_DURATION_IN_SECONDS = 0.1;

    AbstractFollowTrajectoryBuilder() {
        pidLinearXParameters = DefaultPidParameters.LINEAR_X.getParameters();
        pidLinearYParameters = DefaultPidParameters.LINEAR_Y.getParameters();
        pidLinearZParameters = DefaultPidParameters.LINEAR_Z.getParameters();
        pidAngularZParameters = DefaultPidParameters.ANGULAR_Z.getParameters();
        controlRateInSeconds = DEFAULT_CONTROL_RATE_IN_SECONDS;
        droneStateLifeDurationInSeconds = DEFAULT_DRONE_STATE_LIFE_DURATION_IN_SECONDS;
    }

    abstract T self();

    /**
     * Sets the {@code pidLinearXParameters} and returns a reference to an implementation of this class so that the
     * methods can be chained together.
     *
     * @param val the {@code pidLinearXParameters} to set
     * @return a reference to an implementation of this class
     */
    public final T withPidLinearXParameters(PidParameters val) {
        pidLinearXParameters = val;
        return self();
    }

    /**
     * Sets the {@code pidLinearYParameters} and returns a reference to an implementation of this class so that the
     * methods can be chained together.
     *
     * @param val the {@code pidLinearYParameters} to set
     * @return a reference to an implementation of this class
     */
    public final T withPidLinearYParameters(PidParameters val) {
        pidLinearYParameters = val;
        return self();
    }

    /**
     * Sets the {@code pidLinearZParameters} and returns a reference to an implementation of this class so that the
     * methods can be chained together.
     *
     * @param val the {@code pidLinearZParameters} to set
     * @return a reference to an implementation of this class
     */
    public final T withPidLinearZParameters(PidParameters val) {
        pidLinearZParameters = val;
        return self();
    }

    /**
     * Sets the {@code pidAngularZParameters} and returns a reference to an implementation of this class so that the
     * methods can be chained together.
     *
     * @param val the {@code pidAngularZParameters} to set
     * @return a reference to an implementation of this class
     */
    public final T withPidAngularZParameters(PidParameters val) {
        pidAngularZParameters = val;
        return self();
    }

    /**
     * Sets the {@code controlRateInSeconds} and returns a reference to an implementation of this class so that the
     * methods can be chained together.
     *
     * @param val the {@code controlRateInSeconds} to set
     * @return a reference to an implementation of this class
     */
    public final T withControlRateInSeconds(double val) {
        controlRateInSeconds = val;
        return self();
    }

    /**
     * Sets the {@code droneStateLifeDurationInSeconds} and returns a reference to this
     * AbstractFollowTrajectoryBuilder
     * so that the methods can be chained together.
     *
     * @param val the {@code droneStateLifeDurationInSeconds} to set
     * @return a reference to an implementation of this class
     */
    public final T withDroneStateLifeDurationInSeconds(double val) {
        droneStateLifeDurationInSeconds = val;
        return self();
    }

    /**
     * Sets the {@code stateEstimator} and returns a reference to an implementation of this class so that the
     * methods
     * can be chained together.
     *
     * @param val the {@code stateEstimator} to set
     * @return a reference to an implementation of this class
     */
    public final T withStateEstimator(StateEstimator val) {
        stateEstimator = val;
        return self();
    }

    /**
     * Sets the {@code velocity4dService} and returns a reference to an implementation of this class so that the methods
     * can be chained together.
     *
     * @param val the {@code velocity4dService} to set
     * @return a reference to an implementation of this class
     */
    public final T withVelocity4dService(Velocity4dService val) {
        velocity4dService = val;
        return self();
    }

    public <U extends AbstractFollowTrajectoryBuilder<?>> T copyOf(U otherBuilder) {
        pidLinearXParameters = otherBuilder.pidLinearXParameters;
        pidLinearYParameters = otherBuilder.pidLinearYParameters;
        pidLinearZParameters = otherBuilder.pidLinearZParameters;
        pidAngularZParameters = otherBuilder.pidAngularZParameters;
        controlRateInSeconds = otherBuilder.controlRateInSeconds;
        droneStateLifeDurationInSeconds = otherBuilder.droneStateLifeDurationInSeconds;
        stateEstimator = otherBuilder.stateEstimator;
        velocity4dService = otherBuilder.velocity4dService;

        return self();
    }
}
