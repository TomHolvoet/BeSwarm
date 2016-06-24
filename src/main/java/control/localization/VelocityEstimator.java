package control.localization;

import com.google.common.base.Optional;
import control.dto.InertialFrameVelocity;

/**
 * @author Hoang Tung Dinh
 */
public interface VelocityEstimator {
    Optional<InertialFrameVelocity> getCurrentVelocity();
}
