package control.localization;

import com.google.common.base.Optional;
import control.dto.Velocity;
import hal_quadrotor.State;
import services.ros_subscribers.MessagesSubscriberService;

import java.util.Queue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author Hoang Tung Dinh
 */
public final class CratesStateVelocityEstimator implements VelocityEstimator {
    private final MessagesSubscriberService<State> stateSubscriber;

    private CratesStateVelocityEstimator(MessagesSubscriberService<State> stateSubscriber) {
        checkArgument(stateSubscriber.getMaxMessageQueueSize() >= 2,
                String.format("Max message queue size must be " + "at least 2, but it is %d.",
                        stateSubscriber.getMaxMessageQueueSize()));
        this.stateSubscriber = stateSubscriber;
    }

    public static CratesStateVelocityEstimator create(MessagesSubscriberService<State> stateSubscriber) {
        return new CratesStateVelocityEstimator(stateSubscriber);
    }

    @Override
    public Optional<Velocity> getCurrentVelocity() {
        final Queue<State> messageQueue = stateSubscriber.getMessageQueue();
        if (messageQueue.size() < 2) {
            return Optional.absent();
        } else {
            final State firstState = messageQueue.poll();
            final State secondState = messageQueue.poll();
            final double timeDelta = secondState.getT() - firstState.getT();
            checkState(timeDelta > 0, String.format("Time delta must be positive, but it is %f", timeDelta));
            final double velocityX = get1dVelocity(secondState.getX() - firstState.getX(), timeDelta);
            final double velocityY = get1dVelocity(secondState.getY() - firstState.getY(), timeDelta);
            final double velocityZ = get1dVelocity(secondState.getZ() - firstState.getZ(), timeDelta);
            final double velocityYaw = get1dVelocity(secondState.getYaw() - firstState.getYaw(), timeDelta);
            final Velocity currentVelocity = Velocity.builder()
                    .linearX(velocityX)
                    .linearY(velocityY)
                    .linearZ(velocityZ)
                    .angularZ(velocityYaw)
                    .build();
            return Optional.of(currentVelocity);
        }
    }

    private static double get1dVelocity(double travelledDistance, double timeDelta) {
        return travelledDistance / timeDelta;
    }
}
