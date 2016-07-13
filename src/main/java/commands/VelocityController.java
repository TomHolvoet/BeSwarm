package commands;

import control.dto.DroneStateStamped;

/**
 * @author Hoang Tung Dinh
 */
public interface VelocityController {
    /**
     * Computes and sends a velocity to a velocity service based on the current time and the current state of the drone.
     *
     * @param currentTimeInSeconds the current time
     * @param currentState         the current state of the drone
     */
    void computeAndSendVelocity(double currentTimeInSeconds, DroneStateStamped currentState);
}
