package commands;

import commands.schedulers.PeriodicTaskRunner;
import control.dto.DroneStateStamped;
import localization.StateEstimator;
import org.ros.time.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Follow trajectory command.
 *
 * @author Hoang Tung Dinh
 */
public abstract class AbstractFollowTrajectory implements Command {

  private static final Logger logger = LoggerFactory.getLogger(AbstractFollowTrajectory.class);

  private final StateEstimator stateEstimator;
  private final double durationInSeconds;
  private final double controlRateInSeconds;
  private final double droneStateLifeDurationInSeconds;
  private final TimeProvider timeProvider;

  protected AbstractFollowTrajectory(
      StateEstimator stateEstimator,
      double durationInSeconds,
      double controlRateInSeconds,
      double droneStateLifeDurationInSeconds,
      TimeProvider timeProvider) {
    this.stateEstimator = stateEstimator;
    this.durationInSeconds = durationInSeconds;
    this.controlRateInSeconds = controlRateInSeconds;
    this.droneStateLifeDurationInSeconds = droneStateLifeDurationInSeconds;
    this.timeProvider = timeProvider;
  }

  @Override
  public final void execute() {
    logger.debug("Execute follow trajectory command.");
    final Runnable controlLoop = createControlLoop();
    PeriodicTaskRunner.run(controlLoop, controlRateInSeconds, durationInSeconds);
  }

  protected abstract AbstractControlLoop createControlLoop();

  protected abstract class AbstractControlLoop implements Runnable {
    private final double startTimeInSeconds;
    private final int stateLifeDurationInNumberOfControlLoops;
    // assigned to 0
    private int counter;
    private double lastTimeStamp = Double.MIN_VALUE;

    protected AbstractControlLoop() {
      this.startTimeInSeconds = timeProvider.getCurrentTime().toSeconds();
      this.stateLifeDurationInNumberOfControlLoops =
          (int) Math.ceil(droneStateLifeDurationInSeconds / controlRateInSeconds);
    }

    @Override
    public void run() {
      logger.trace("Start a control loop.");
      final Optional<DroneStateStamped> currentState = stateEstimator.getCurrentState();
      if (!currentState.isPresent()) {
        logger.trace("Cannot get state. Haven't sent any velocity.");
        return;
      }

      setCounter(currentState.get());

      if (counter >= stateLifeDurationInNumberOfControlLoops) {
        logger.debug("Pose is outdated. Stop sending velocity.");
      } else {
        logger.trace("Got pose and velocity. Start computing the next velocity response.");
        final double currentTimeInSeconds =
            timeProvider.getCurrentTime().toSeconds() - startTimeInSeconds;
        computeAndSendResponse(currentTimeInSeconds, currentState.get());
      }
    }

    protected abstract void computeAndSendResponse(
        double currentTimeInSeconds, DroneStateStamped currentState);

    private void setCounter(DroneStateStamped currentState) {
      final double currentTimeStamp = currentState.getTimeStampInSeconds();
      if (currentTimeStamp == lastTimeStamp) {
        counter++;
      } else {
        counter = 0;
        lastTimeStamp = currentTimeStamp;
      }
    }
  }

  protected abstract static class AbstractBuilder<T extends AbstractBuilder> {

    private static final double DEFAULT_CONTROL_RATE_IN_SECONDS = 0.05;
    private static final double DEFAULT_DRONE_STATE_LIFE_DURATION_IN_SECONDS = 0.1;

    protected StateEstimator stateEstimator;
    protected Double durationInSeconds;
    protected Double controlRateInSeconds;
    protected Double droneStateLifeDurationInSeconds;
    protected TimeProvider timeProvider;

    protected AbstractBuilder() {
      controlRateInSeconds = DEFAULT_CONTROL_RATE_IN_SECONDS;
      droneStateLifeDurationInSeconds = DEFAULT_DRONE_STATE_LIFE_DURATION_IN_SECONDS;
    }

    protected abstract T self();

    /**
     * Sets the {@code stateEstimator} and returns a reference to this Builder so that the methods
     * can be chained together.
     *
     * @param val the {@code stateEstimator} to set
     * @return a reference to this Builder
     */
    public T withStateEstimator(StateEstimator val) {
      stateEstimator = val;
      return self();
    }

    /**
     * Sets the {@code durationInSeconds} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code durationInSeconds} to set
     * @return a reference to this Builder
     */
    public T withDurationInSeconds(double val) {
      durationInSeconds = val;
      return self();
    }

    /**
     * Sets the {@code controlRateInSeconds} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code controlRateInSeconds} to set
     * @return a reference to this Builder
     */
    public T withControlRateInSeconds(double val) {
      controlRateInSeconds = val;
      return self();
    }

    /**
     * Sets the {@code droneStateLifeDurationInSeconds} and returns a reference to this Builder so
     * that the methods can be chained together.
     *
     * @param val the {@code droneStateLifeDurationInSeconds} to set
     * @return a reference to this Builder
     */
    public T withDroneStateLifeDurationInSeconds(double val) {
      droneStateLifeDurationInSeconds = val;
      return self();
    }

    /**
     * Sets the {@code timeProvider} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code timeProvider} to set
     * @return a reference to this Builder
     */
    public T withTimeProvider(TimeProvider val) {
      timeProvider = val;
      return self();
    }
  }
}
