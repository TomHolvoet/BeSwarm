package services;

import com.google.common.base.Optional;
import services.ros_subscribers.FlyingState;

/**
 * @author Hoang Tung Dinh
 */
public interface FlyingStateService {
    Optional<FlyingState> getCurrentFlyingState();
}
