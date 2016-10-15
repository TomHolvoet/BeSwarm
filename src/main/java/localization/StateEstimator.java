package localization;

import control.dto.DroneStateStamped;

import java.util.Optional;

/** @author Hoang Tung Dinh */
public interface StateEstimator {
  /**
   * Gets the current estimated state.
   *
   * @return the current estimated state of the drone, could be absent
   */
  Optional<DroneStateStamped> getCurrentState();
}
