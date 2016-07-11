package services;

/**
 * @author Hoang Tung Dinh
 */
public interface CommonServiceFactory {
    TakeOffService createTakeOffService();

    LandService createLandService();

    FlyingStateService createFlyingStateService();
}
