package control;

import commands.Pose;
import com.google.common.base.Optional;

/**
 * @author Hoang Tung Dinh
 */
public interface PoseEstimator {
    Optional<Pose> getCurrentPose();
}
