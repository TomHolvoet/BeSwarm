package control.localization;

import com.google.common.base.Optional;
import control.dto.DroneState;

/**
 * @author Hoang Tung Dinh
 */
public interface StateEstimator {
    Optional<DroneState> getCurrentState();
}
