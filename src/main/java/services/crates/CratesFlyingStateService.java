package services.crates;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import hal_quadrotor.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FlyingStateService;
import services.ros_subscribers.FlyingState;
import services.ros_subscribers.MessageObserver;
import services.ros_subscribers.MessagesSubscriberService;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Hoang Tung Dinh
 */
final class CratesFlyingStateService implements MessageObserver<State>, FlyingStateService {

    private static final Logger logger = LoggerFactory.getLogger(CratesFlyingStateService.class);
    private final AtomicReference<FlyingState> currentFlyingState = new AtomicReference<>();
    private static final Map<String, CratesFlyingState> FLYING_STATE_MAP = ImmutableMap.<String,
            CratesFlyingState>builder()
            .put("AnglesHeight", CratesFlyingState.ANGLES_HEIGHT)
            .put("Emergency", CratesFlyingState.EMERGENCY)
            .put("Hover", CratesFlyingState.HOVER)
            .put("Idle", CratesFlyingState.IDLE)
            .put("Land", CratesFlyingState.LAND)
            .put("Takeoff", CratesFlyingState.TAKE_OFF)
            .put("VelocityHeight", CratesFlyingState.VELOCITY_HEIGHT)
            .put("Velocity", CratesFlyingState.VELOCITY)
            .put("Waypoint", CratesFlyingState.WAYPOINT)
            .build();

    private CratesFlyingStateService() {}

    public static CratesFlyingStateService create(MessagesSubscriberService<State> flyingStateSubscriber) {
        final CratesFlyingStateService cratesFlyingStateService = new CratesFlyingStateService();
        flyingStateSubscriber.registerMessageObserver(cratesFlyingStateService);
        return cratesFlyingStateService;
    }

    @Override
    public void onNewMessage(State message) {
        final String controllerStateName = message.getController();
        if (currentFlyingState.get() == null || !currentFlyingState.get().equals(controllerStateName)) {
            currentFlyingState.set(FLYING_STATE_MAP.get(controllerStateName).getConvertedFlyingState());
        }
        logger.trace("Current crates state: {}. Current standard flying state: {}.", controllerStateName,
                currentFlyingState.get().getStateName());
    }

    @Override
    public Optional<FlyingState> getCurrentFlyingState() {
        if (currentFlyingState.get() == null) {
            return Optional.absent();
        } else {
            return Optional.of(currentFlyingState.get());
        }
    }

    private enum CratesFlyingState {
        ANGLES_HEIGHT("AnglesHeight") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.FLYING;
            }
        },

        EMERGENCY("Emergency") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.EMERGENCY;
            }
        },

        HOVER("Hover") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.HOVERING;
            }
        },

        IDLE("Idle") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.LANDED;
            }
        },

        LAND("Land") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.LANDING;
            }
        },

        TAKE_OFF("Takeoff") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.TAKING_OFF;
            }
        },

        VELOCITY_HEIGHT("VelocityHeight") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.FLYING;
            }
        },

        VELOCITY("Velocity") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.FLYING;
            }
        },

        WAYPOINT("Waypoint") {
            @Override
            FlyingState getConvertedFlyingState() {
                return FlyingState.FLYING;
            }
        };

        private final String stateName;

        CratesFlyingState(String stateName) {
            this.stateName = stateName;
        }

        public String getStateName() {
            return stateName;
        }

        abstract FlyingState getConvertedFlyingState();
    }
}
