package control;

import behavior.Pose;
import com.google.common.base.Optional;

/**
 * @author Hoang Tung Dinh
 */
public interface PoseProvider {
    Optional<Pose> getCurrentPose();
}
