package services;

import com.google.common.base.Optional;

/**
 * @author Hoang Tung Dinh
 */
public interface ServiceFactory {
    Optional<TakeOffService> createTakeOffService();

    Optional<LandService> createLandService();

    Optional<VelocityService> createVelocityService();

    Optional<FlipService> createFlipService();
}
