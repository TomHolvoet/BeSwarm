package control.localization;

import com.google.common.base.Optional;

import control.dto.Pose;

/**
 * @author Hoang Tung Dinh
 */
public interface PoseEstimator {
    Optional<Pose> getCurrentPose();
}
