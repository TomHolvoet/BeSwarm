package control.localization;

import com.google.common.base.Optional;
import control.dto.Pose;
import hal_quadrotor.State;
import services.ros_subscribers.MessagesSubscriberService;

/**
 * @author Hoang Tung Dinh
 */
public final class CratesStatePoseEstimator implements PoseEstimator {
    private final MessagesSubscriberService<State> stateSubscriber;

    private CratesStatePoseEstimator(MessagesSubscriberService<State> stateSubscriber) {
        this.stateSubscriber = stateSubscriber;
    }

    public static CratesStatePoseEstimator create(MessagesSubscriberService<State> stateSubscriber) {
        return new CratesStatePoseEstimator(stateSubscriber);
    }

    @Override
    public Optional<Pose> getCurrentPose() {
        final Optional<State> stateOptional = stateSubscriber.getMostRecentMessage();
        if (stateOptional.isPresent()) {
            final State state = stateOptional.get();
            final Pose pose = Pose.builder()
                    .x(state.getX())
                    .y(state.getY())
                    .z(state.getZ())
                    .yaw(state.getYaw())
                    .build();
            return Optional.of(pose);
        } else {
            return Optional.absent();
        }
    }
}
