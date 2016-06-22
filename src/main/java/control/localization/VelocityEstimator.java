package control.localization;

import com.google.common.base.Optional;

import control.dto.Velocity;

/**
 * @author Hoang Tung Dinh
 */
public interface VelocityEstimator {
    Optional<Velocity> getCurrentVelocity();
}
