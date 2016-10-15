package services;

import services.rossubscribers.FlyingState;

import java.util.Optional;

/**
 * Represents the flying state service.
 *
 * @author Hoang Tung Dinh
 */
public interface FlyingStateService {
  /**
   * Gets the current flying state of the drone.
   *
   * @return the current flying state of the drone, can be absent
   */
  Optional<FlyingState> getCurrentFlyingState();
}
