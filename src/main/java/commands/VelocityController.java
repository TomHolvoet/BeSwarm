package commands;

import control.dto.DroneStateStamped;

/**
 * @author Hoang Tung Dinh
 */
public interface VelocityController {
    void computeAndSendVelocity(double currentTimeInSeconds, DroneStateStamped currentState);
}
