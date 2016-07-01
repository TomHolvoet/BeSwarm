package services.parrot;

import bebop_msgs.Ardrone3PilotingStateFlyingStateChanged;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ros_subscribers.FlyingState;
import services.ros_subscribers.MessageObserver;
import services.ros_subscribers.MessagesSubscriberService;

import javax.annotation.Nullable;

/**
 * @author Hoang Tung Dinh
 */
public class BebopFlyingStateService implements MessageObserver<Ardrone3PilotingStateFlyingStateChanged> {

    private static final Logger logger = LoggerFactory.getLogger(BebopFlyingStateService.class);

    @Nullable private FlyingState currentFlyingState;
    private static final ImmutableMap<Byte, FlyingState> flyingStateMap = ImmutableMap.<Byte, FlyingState>builder()
            .put((byte) 0, FlyingState.LANDED)
            .put((byte) 1, FlyingState.TAKING_OFF)
            .put((byte) 2, FlyingState.HOVERING)
            .put((byte) 3, FlyingState.FLYING)
            .put((byte) 4, FlyingState.LANDING)
            .put((byte) 5, FlyingState.EMERGENCY)
            .put((byte) 6, FlyingState.USER_TAKEOFF)
            .build();

    private BebopFlyingStateService(
            MessagesSubscriberService<Ardrone3PilotingStateFlyingStateChanged> flyingStateSubscriber) {
        flyingStateSubscriber.registerMessageObserver(this);
    }

    @Override
    public void onNewMessage(Ardrone3PilotingStateFlyingStateChanged message) {
        currentFlyingState = flyingStateMap.get(message.getState());
        logger.info("Current flying state: {}", currentFlyingState.getStateName());
    }

    public Optional<FlyingState> getCurrentFlyingState() {
        if (currentFlyingState == null) {
            return Optional.absent();
        } else {
            return Optional.of(currentFlyingState);
        }
    }
}
