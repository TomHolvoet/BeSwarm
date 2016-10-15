package localization;

import control.dto.BodyFrameVelocity;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import hal_quadrotor.State;
import org.ros.time.TimeProvider;
import services.rossubscribers.MessagesSubscriberService;
import utils.math.Transformations;

import java.util.Optional;

/** @author Hoang Tung Dinh */
public final class CratesSimStateEstimator implements StateEstimator {
  private final MessagesSubscriberService<State> stateSubscriber;
  private final TimeProvider timeProvider;

  private CratesSimStateEstimator(
      MessagesSubscriberService<State> stateSubscriber, TimeProvider timeProvider) {
    this.stateSubscriber = stateSubscriber;
    this.timeProvider = timeProvider;
  }

  /**
   * Creates a new state estimator for a drone in the Crates simulator.
   *
   * @param stateSubscriber the state subscriber
   * @param timeProvider the time provider
   * @return a state estimator
   */
  public static CratesSimStateEstimator create(
      MessagesSubscriberService<State> stateSubscriber, TimeProvider timeProvider) {
    return new CratesSimStateEstimator(stateSubscriber, timeProvider);
  }

  @Override
  public Optional<DroneStateStamped> getCurrentState() {
    final Optional<State> stateOptional = stateSubscriber.getMostRecentMessage();

    if (!stateOptional.isPresent()) {
      return Optional.empty();
    }

    final State state = stateOptional.get();

    final Pose pose =
        Pose.builder()
            .setX(state.getX())
            .setY(state.getY())
            .setZ(state.getZ())
            .setYaw(state.getYaw())
            .build();

    final BodyFrameVelocity bodyFrameVelocity =
        Velocity.builder()
            .setLinearX(state.getU())
            .setLinearY(state.getV())
            .setLinearZ(state.getW())
            .setAngularZ(state.getR())
            .build();

    final InertialFrameVelocity inertialFrameVelocity =
        Transformations.bodyFrameVelocityToInertialFrameVelocity(bodyFrameVelocity, pose);

    final double timeStampInSeconds = timeProvider.getCurrentTime().toSeconds();
    return Optional.of(DroneStateStamped.create(pose, inertialFrameVelocity, timeStampInSeconds));
  }
}
