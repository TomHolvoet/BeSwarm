package commands;

import control.VelocityController4d;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import localization.StateEstimator;
import services.Velocity4dService;
import time.TimeProvider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract follow trajectory command for parrot drones.
 *
 * @author Hoang Tung Dinh
 */
public abstract class AbstractParrotFollowTrajectory extends AbstractFollowTrajectory {

  private final VelocityController4d velocityController4d;
  private final Velocity4dService velocity4dService;

  protected AbstractParrotFollowTrajectory(
      StateEstimator stateEstimator,
      double durationInSeconds,
      double controlRateInSeconds,
      double droneStateLifeDurationInSeconds,
      TimeProvider timeProvider,
      VelocityController4d velocityController4d,
      Velocity4dService velocity4dService) {
    super(
        stateEstimator,
        durationInSeconds,
        controlRateInSeconds,
        droneStateLifeDurationInSeconds,
        timeProvider);

    this.velocityController4d = velocityController4d;
    this.velocity4dService = velocity4dService;
  }

  @Override
  protected final AbstractFollowTrajectory.AbstractControlLoop createControlLoop() {
    return new ControlLoop();
  }

  private final class ControlLoop extends AbstractFollowTrajectory.AbstractControlLoop {

    private ControlLoop() {}

    @Override
    protected void computeAndSendResponse(
        double currentTimeInSeconds, DroneStateStamped currentState) {
      final InertialFrameVelocity nextVelocity =
          velocityController4d.computeNextResponse(
              currentState.pose(), currentState.inertialFrameVelocity(), currentTimeInSeconds);
      velocity4dService.sendInertialFrameVelocity(nextVelocity, currentState.pose());
    }
  }

  /** {@code AbstractParrotFollowTrajectory} builder static inner class. */
  public abstract static class ParrotBuilder<T extends ParrotBuilder<T>>
      extends AbstractFollowTrajectory.AbstractBuilder<T> {

    protected VelocityController4d velocityController4d;
    protected Velocity4dService velocity4dService;

    protected ParrotBuilder() {}

    /**
     * Sets the {@code velocityController4d} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code velocityController4d} to set
     * @return a reference to this Builder
     */
    public T withVelocityController4d(VelocityController4d val) {
      velocityController4d = val;
      return self();
    }

    /**
     * Sets the {@code velocity4dService} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code velocity4dService} to set
     * @return a reference to this Builder
     */
    public T withVelocity4dService(Velocity4dService val) {
      velocity4dService = val;
      return self();
    }

    protected void checkMissingParameters() {
      checkNotNull(durationInSeconds);
      checkNotNull(controlRateInSeconds);
      checkNotNull(droneStateLifeDurationInSeconds);
      checkNotNull(stateEstimator);
      checkNotNull(velocityController4d);
      checkNotNull(velocity4dService);
      checkNotNull(timeProvider);
    }
  }
}
