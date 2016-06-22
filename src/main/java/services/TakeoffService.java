package services;

/**
 * @author Hoang Tung Dinh
 */
public interface TakeOffService {
    void sendTakingOffMessage();

    void sendTakingOffMessage(double desiredAltitude);
}
