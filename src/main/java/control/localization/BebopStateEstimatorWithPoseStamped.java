package control.localization;

import com.google.common.base.Optional;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import geometry_msgs.PoseStamped;
import services.rossubscribers.MessagesSubscriberService;

import javax.annotation.Nullable;

/**
 * A localization that gets both pose and velocity from ArMarker.
 *
 * @author Hoang Tung Dinh
 */
public final class BebopStateEstimatorWithPoseStamped implements StateEstimator {

  private final MessagesSubscriberService<PoseStamped> poseSubscriber;
  @Nullable private PoseStamped mostRecentPoseStamped;
  @Nullable private PoseStamped secondMostRecentPoseStamped;

  private BebopStateEstimatorWithPoseStamped(
      MessagesSubscriberService<PoseStamped> poseSubscriber) {
    this.poseSubscriber = poseSubscriber;
  }

  public static BebopStateEstimatorWithPoseStamped create(
      MessagesSubscriberService<PoseStamped> poseSubscriber) {
    return new BebopStateEstimatorWithPoseStamped(poseSubscriber);
  }

  @Override
  public Optional<DroneStateStamped> getCurrentState() {
    final Optional<PoseStamped> poseStamped = poseSubscriber.getMostRecentMessage();

    // if there is no pose available yet
    if (!poseStamped.isPresent()) {
      return Optional.absent();
    }

    // if there is no pose stored yet
    if (mostRecentPoseStamped == null) {
      mostRecentPoseStamped = poseStamped.get();
      return Optional.absent();
    }

    // if there is at least one pose stored and the pose now is different from the most recent pose
    if (!mostRecentPoseStamped
        .getHeader()
        .getStamp()
        .equals(poseStamped.get().getHeader().getStamp())) {
      secondMostRecentPoseStamped = mostRecentPoseStamped;
      mostRecentPoseStamped = poseStamped.get();
    }

    // if we only have one pose
    if (secondMostRecentPoseStamped == null) {
      return Optional.absent();
    }

    // now finally, the case when we have two poses
    final double timeDelta =
        mostRecentPoseStamped.getHeader().getStamp().toSeconds()
            - secondMostRecentPoseStamped.getHeader().getStamp().toSeconds();
    final Pose mostRecentPose = Pose.create(mostRecentPoseStamped);
    final Pose secondMostRecentPose = Pose.create(secondMostRecentPoseStamped);
    final InertialFrameVelocity inertialFrameVelocity =
        getVelocity(mostRecentPose, secondMostRecentPose, timeDelta);

    return Optional.of(
        DroneStateStamped.create(
            mostRecentPose,
            inertialFrameVelocity,
            mostRecentPoseStamped.getHeader().getStamp().toSeconds()));
  }

  private static InertialFrameVelocity getVelocity(
      Pose mostRecentPose, Pose secondMostRecentPose, double timeDelta) {
    final double velLinearX = (mostRecentPose.x() - secondMostRecentPose.x()) / timeDelta;
    final double velLinearY = (mostRecentPose.y() - secondMostRecentPose.y()) / timeDelta;
    final double velLinearZ = (mostRecentPose.z() - secondMostRecentPose.z()) / timeDelta;
    final double velAngularZ = (mostRecentPose.yaw() - secondMostRecentPose.yaw()) / timeDelta;
    return Velocity.builder()
        .setLinearX(velLinearX)
        .setLinearY(velLinearY)
        .setLinearZ(velLinearZ)
        .setAngularZ(velAngularZ)
        .build();
  }
}
