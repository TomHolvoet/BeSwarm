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
    private static final Map<Integer, ArDroneState> FLYING_STATE_MAP = ImmutableMap.<Integer, ArDroneState>builder()
            .put(
            0, ArDroneState.UNKNOWN)
            .put(1, ArDroneState.INITED)
            .put(2, ArDroneState.LANDED)
            .put(3, ArDroneState.FLYING)
            .put(4, ArDroneState.HOVERING)
            .put(5, ArDroneState.TEST)
            .put(6, ArDroneState.TAKING_OFF)
            .put(7, ArDroneState.FLYING)
            .put(8, ArDroneState.LANDING)
            .put(9, ArDroneState.LOOPING)
            .build();

    private TumSimFlyingStateService() {}

    public static TumSimFlyingStateService create(MessagesSubscriberService<Navdata> flyingStateSubscriber) {
        final TumSimFlyingStateService tumSimFlyingStateService = new TumSimFlyingStateService();
        flyingStateSubscriber.registerMessageObserver(tumSimFlyingStateService);
        return tumSimFlyingStateService;
    }

    @Override
    public void onNewMessage(Navdata message) {
        currentFlyingState = FLYING_STATE_MAP.get(message.getState()).getConvertedFlyingState();
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

    private enum ArDroneState {
        UNKNOWN("Unknown") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.UNKNOWN;
            }
        },

        INITED("Inited") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.LANDED;
            }
        },

        LANDED("Landed") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.LANDED;
            }
        },

        FLYING("Flying") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.FLYING;
            }
        },

        HOVERING("Hovering") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.HOVERING;
            }
        },

        TEST("Test") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.UNKNOWN;
            }
        },

        TAKING_OFF("Taking off") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.TAKING_OFF;
            }
        },

        LANDING("Landing") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.LANDING;
            }
        },

        LOOPING("Looping") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.UNKNOWN;
            }
        };

        private final String stateName;

        ArDroneState(String stateName) {
            this.stateName = stateName;
        }

        public String getStateName() {
            return stateName;
        }

        abstract FlyingState getConvertedFlyingState();
    }
}
