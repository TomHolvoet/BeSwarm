package control;

import commands.Pose;
import com.google.common.base.Optional;

/**
 * @author Hoang Tung Dinh
 */
public interface PoseProvider {
    Optional<Pose> getCurrentPose();
}
