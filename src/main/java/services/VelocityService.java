package services;

import control.dto.InertialFrameVelocity;

/**
 * @author Hoang Tung Dinh
 */
public interface VelocityService {
    void sendVelocityMessage(InertialFrameVelocity inertialFrameVelocity);
}
