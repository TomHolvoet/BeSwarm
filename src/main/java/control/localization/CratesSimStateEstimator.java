package control.localization;

import com.google.common.base.Optional;
import control.dto.BodyFrameVelocity;
import control.dto.DroneState;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import hal_quadrotor.State;
import services.ros_subscribers.MessagesSubscriberService;
import utils.math.Transformations;

/**
 * @author Hoang Tung Dinh
 */
public final class CratesSimStateEstimator implements StateEstimator {
    private final MessagesSubscriberService<State> stateSubscriber;

    private CratesSimStateEstimator(MessagesSubscriberService<State> stateSubscriber) {
        this.stateSubscriber = stateSubscriber;
    }

    public static CratesSimStateEstimator create(MessagesSubscriberService<State> stateSubscriber) {
        return new CratesSimStateEstimator(stateSubscriber);
    }

    @Override
    public Optional<DroneState> getCurrentState() {
        final Optional<State> stateOptional = stateSubscriber.getMostRecentMessage();

        if (!stateOptional.isPresent()) {
            return Optional.absent();
        }

        final State state = stateOptional.get();

        final Pose pose = Pose.builder().x(state.getX()).y(state.getY()).z(state.getZ()).yaw(state.getYaw()).build();

        final BodyFrameVelocity bodyFrameVelocity = Velocity.builder()
                .linearX(state.getU())
                .linearY(state.getV())
                .linearZ(state.getW())
                .angularZ(state.getR())
                .build();

        final InertialFrameVelocity inertialFrameVelocity = Transformations.bodyFrameVelocityToInertialFrameVelocity(
                bodyFrameVelocity, pose);

        return Optional.of(DroneState.create(pose, inertialFrameVelocity));
    }
}
