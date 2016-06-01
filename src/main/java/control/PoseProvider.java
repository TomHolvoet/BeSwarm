package control;

import bebopbehavior.Pose;

/**
 * @author Hoang Tung Dinh
 */
public interface PoseProvider {
    Pose getMostRecentPose();
}
