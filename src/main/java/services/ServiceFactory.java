package services;

/**
 * @author Hoang Tung Dinh
 */
public interface ServiceFactory {
    TakeOffService createTakeOffService();

    LandService createLandService();

    VelocityService createVelocityService();

    FlipService createFlipService();

    FlyingStateService createFlyingStateService();
}
