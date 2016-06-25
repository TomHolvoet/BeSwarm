package services;

import control.dto.InertialFrameVelocity;
import control.dto.Pose;

/**
 * @author Hoang Tung Dinh
 */
public interface VelocityService {
    void sendVelocityMessage(InertialFrameVelocity inertialFrameVelocity, Pose pose);
}
