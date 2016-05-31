package control;

import bebopbehavior.Pose;
import com.google.common.base.MoreObjects;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Hoang Tung Dinh
 */
public final class AtomicPose {
    private final AtomicReference<Pose> pose = new AtomicReference<>();

    private AtomicPose() {}

    public static AtomicPose create() {
        return new AtomicPose();
    }

    /**
     * Update the pose of the drone.
     *
     * @param newPose the new pose of the drone
     */
    public void updatePose(Pose newPose) {
        pose.set(newPose);
    }

    /**
     * @return the most recent pose of the drone
     */
    public Pose getMostRecentPose() {
        return pose.get();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("pose", pose).toString();
    }
}
