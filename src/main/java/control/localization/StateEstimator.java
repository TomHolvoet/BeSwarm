package control.localization;

import com.google.common.base.Optional;
import control.dto.DroneStateStamped;

/**
 * @author Hoang Tung Dinh
 */
public interface StateEstimator {
    Optional<DroneStateStamped> getCurrentState();
}
