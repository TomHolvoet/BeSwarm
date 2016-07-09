package services.parrot;

import ardrone_autonomy.Navdata;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FlyingStateService;
import services.ros_subscribers.FlyingState;
import services.ros_subscribers.MessageObserver;
import services.ros_subscribers.MessagesSubscriberService;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Hoang Tung Dinh
 */
final class TumSimFlyingStateService implements MessageObserver<Navdata>, FlyingStateService {

    private static final Logger logger = LoggerFactory.getLogger(TumSimFlyingStateService.class);
    @Nullable private FlyingState currentFlyingState;
    private static final Map<Integer, FlyingState> FLYING_STATE_MAP = ImmutableMap.<Integer, FlyingState>builder().put(
            0, FlyingState.UNKNOWN)
            .put(1, FlyingState.INITED)
            .put(2, FlyingState.LANDED)
            .put(3, FlyingState.FLYING)
            .put(4, FlyingState.HOVERING)
            .put(5, FlyingState.TEST)
            .put(6, FlyingState.TAKING_OFF)
            .put(7, FlyingState.FLYING)
            .put(8, FlyingState.LANDING)
            .put(9, FlyingState.LOOPING)
            .build();

    private TumSimFlyingStateService() {}

    public static TumSimFlyingStateService create(MessagesSubscriberService<Navdata> flyingStateSubscriber) {
        final TumSimFlyingStateService tumSimFlyingStateService = new TumSimFlyingStateService();
        flyingStateSubscriber.registerMessageObserver(tumSimFlyingStateService);
        return tumSimFlyingStateService;
    }

    @Override
    public void onNewMessage(Navdata message) {
        currentFlyingState = FLYING_STATE_MAP.get(message.getState());
        logger.trace("Current flying state: {}", currentFlyingState.getStateName());
    }

    @Override
    public Optional<FlyingState> getCurrentFlyingState() {
        if (currentFlyingState == null) {
            return Optional.absent();
        } else {
            return Optional.of(currentFlyingState);
        }
    }
}
