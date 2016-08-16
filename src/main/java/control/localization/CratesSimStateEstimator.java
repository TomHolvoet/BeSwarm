package control.localization;

import com.google.common.base.Optional;
import control.dto.BodyFrameVelocity;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import hal_quadrotor.State;
import services.rossubscribers.MessagesSubscriberService;
import utils.math.Transformations;

/** @author Hoang Tung Dinh */
public final class CratesSimStateEstimator implements StateEstimator {
  private static final double NANO_SECOND_TO_SECOND = 1000000000.0;
  private final MessagesSubscriberService<State> stateSubscriber;

  private CratesSimStateEstimator(MessagesSubscriberService<State> stateSubscriber) {
    this.stateSubscriber = stateSubscriber;
  }

  /**
   * Creates a new state estimator for a drone in the Crates simulator.
   *
   * @param stateSubscriber the state subscriber
   * @return a state estimator
   */
  public static CratesSimStateEstimator create(MessagesSubscriberService<State> stateSubscriber) {
    return new CratesSimStateEstimator(stateSubscriber);
  }

  @Override
  public Optional<DroneStateStamped> getCurrentState() {
    final Optional<State> stateOptional = stateSubscriber.getMostRecentMessage();

    if (!stateOptional.isPresent()) {
      return Optional.absent();
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

    final double timeStampInSeconds = System.nanoTime() / NANO_SECOND_TO_SECOND;
    return Optional.of(DroneStateStamped.create(pose, inertialFrameVelocity, timeStampInSeconds));
  }
}
