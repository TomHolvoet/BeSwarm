package commands;

import control.DefaultPidParameters;
import control.PidParameters;
import control.localization.StateEstimator;
import services.VelocityService;
import time.TimeProvider;

/**
 * @author Hoang Tung Dinh
 * @author Rinde van Lon
 * @author Kristof Coninx
 */
abstract class AbstractFollowTrajectoryBuilder<T extends AbstractFollowTrajectoryBuilder<T>> {
  private static final double DEFAULT_CONTROL_RATE_IN_SECONDS = 0.05;
  private static final double DEFAULT_DRONE_STATE_LIFE_DURATION_IN_SECONDS = 0.1;
  PidParameters pidLinearXParameters;
  PidParameters pidLinearYParameters;
  PidParameters pidLinearZParameters;
  PidParameters pidAngularZParameters;
  Double controlRateInSeconds;
  Double droneStateLifeDurationInSeconds;
  StateEstimator stateEstimator;
  VelocityService velocityService;
  TimeProvider timeProvider;

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
   * Sets the {@code pidLinearXParameters} and returns a reference to an implementation of this
   * class so that the methods can be chained together.
   *
   * @param val the {@code pidLinearXParameters} to set
   * @return a reference to an implementation of this class
   */
  public final T withPidLinearXParameters(PidParameters val) {
    pidLinearXParameters = val;
    return self();
  }

  /**
   * Sets the {@code pidLinearYParameters} and returns a reference to an implementation of this
   * class so that the methods can be chained together.
   *
   * @param val the {@code pidLinearYParameters} to set
   * @return a reference to an implementation of this class
   */
  public final T withPidLinearYParameters(PidParameters val) {
    pidLinearYParameters = val;
    return self();
  }

  /**
   * Sets the {@code pidLinearZParameters} and returns a reference to an implementation of this
   * class so that the methods can be chained together.
   *
   * @param val the {@code pidLinearZParameters} to set
   * @return a reference to an implementation of this class
   */
  public final T withPidLinearZParameters(PidParameters val) {
    pidLinearZParameters = val;
    return self();
  }

  /**
   * Sets the {@code pidAngularZParameters} and returns a reference to an implementation of this
   * class so that the methods can be chained together.
   *
   * @param val the {@code pidAngularZParameters} to set
   * @return a reference to an implementation of this class
   */
  public final T withPidAngularZParameters(PidParameters val) {
    pidAngularZParameters = val;
    return self();
  }

  /**
   * Sets the {@code controlRateInSeconds} and returns a reference to an implementation of this
   * class so that the methods can be chained together.
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
   * AbstractFollowTrajectoryBuilder so that the methods can be chained together.
   *
   * @param val the {@code droneStateLifeDurationInSeconds} to set
   * @return a reference to an implementation of this class
   */
  public final T withDroneStateLifeDurationInSeconds(double val) {
    droneStateLifeDurationInSeconds = val;
    return self();
  }

  /**
   * Sets the {@code stateEstimator} and returns a reference to an implementation of this class so
   * that the methods can be chained together.
   *
   * @param val the {@code stateEstimator} to set
   * @return a reference to an implementation of this class
   */
  public final T withStateEstimator(StateEstimator val) {
    stateEstimator = val;
    return self();
  }

  /**
   * Sets the {@code velocityService} and returns a reference to an implementation of this class so
   * that the methods can be chained together.
   *
   * @param val the {@code velocityService} to set
   * @return a reference to an implementation of this class
   */
  public final T withVelocityService(VelocityService val) {
    velocityService = val;
    return self();
  }

  /**
   * Sets the {@code timeProvider} and returns a reference to an implementation of this class so
   * that the methods can be chained together.
   *
   * @param val the {@code timeProvider} to set
   * @return a reference to an implementation of this class
   */
  public final T withTimeProvider(TimeProvider val) {
    timeProvider = val;
    return self();
  }

  /**
   * Copies the parameter of another builder.
   *
   * @param otherBuilder the builder that parameters will be copied
   * @param <U> the type of the other builder, which must be a subclass of {@link
   *     AbstractFollowTrajectoryBuilder}
   * @return a concrete implementation of this abstract builder with all parameters copied from
   *     {@code otherBuilder}
   */
  public <U extends AbstractFollowTrajectoryBuilder<?>> T copyOf(U otherBuilder) {
    pidLinearXParameters = otherBuilder.pidLinearXParameters;
    pidLinearYParameters = otherBuilder.pidLinearYParameters;
    pidLinearZParameters = otherBuilder.pidLinearZParameters;
    pidAngularZParameters = otherBuilder.pidAngularZParameters;
    controlRateInSeconds = otherBuilder.controlRateInSeconds;
    droneStateLifeDurationInSeconds = otherBuilder.droneStateLifeDurationInSeconds;
    stateEstimator = otherBuilder.stateEstimator;
    velocityService = otherBuilder.velocityService;
    timeProvider = otherBuilder.timeProvider;

    return self();
  }
}
