package services;

/**
 * Represents the take off service.
 *
 * @author Hoang Tung Dinh
 */
public interface TakeOffService {
    /**
     * Send a message to require the drone to take off. The drone will take off and hover at a
     * default altitude.
     */
    void sendTakingOffMessage();

    /**
     * Sends a message to require the drone to take off and then hover at a desired altitude.
     *
     * @param desiredAltitude the altitude which the drone will hover at
     */
    void sendTakingOffMessage(double desiredAltitude);
}
