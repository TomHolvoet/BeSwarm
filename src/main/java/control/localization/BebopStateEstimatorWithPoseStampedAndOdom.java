package control.localization;

import com.google.common.base.Optional;
import control.dto.BodyFrameVelocity;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import geometry_msgs.PoseStamped;
import nav_msgs.Odometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ros_subscribers.MessagesSubscriberService;
import utils.math.Transformations;

/**
 * @author Hoang Tung Dinh
 */
public final class BebopStateEstimatorWithPoseStampedAndOdom implements StateEstimator {

    private static final Logger logger = LoggerFactory.getLogger(BebopStateEstimatorWithPoseStampedAndOdom.class);

    private final MessagesSubscriberService<PoseStamped> poseSubscriber;
    private final MessagesSubscriberService<Odometry> odometrySubscriber;

    private BebopStateEstimatorWithPoseStampedAndOdom(MessagesSubscriberService<PoseStamped> poseSubscriber,
            MessagesSubscriberService<Odometry> odometrySubscriber) {
        this.poseSubscriber = poseSubscriber;
        this.odometrySubscriber = odometrySubscriber;
    }

    public static BebopStateEstimatorWithPoseStampedAndOdom create(
            MessagesSubscriberService<PoseStamped> poseSubscriber,
            MessagesSubscriberService<Odometry> odometrySubscriber) {
        return new BebopStateEstimatorWithPoseStampedAndOdom(poseSubscriber, odometrySubscriber);
    }

    @Override
    public Optional<DroneStateStamped> getCurrentState() {
        final Optional<PoseStamped> poseStamped = poseSubscriber.getMostRecentMessage();

        if (!poseStamped.isPresent()) {
            return Optional.absent();
        }

        final Pose pose = Pose.create(poseStamped.get());
        final Optional<InertialFrameVelocity> inertialFrameVelocity = getVelocity(pose);

        if (!inertialFrameVelocity.isPresent()) {
            return Optional.absent();
        }

        final DroneStateStamped droneState = DroneStateStamped.create(pose, inertialFrameVelocity.get(),
                poseStamped.get().getHeader().getStamp().toSeconds());
        return Optional.of(droneState);
    }

    private Optional<InertialFrameVelocity> getVelocity(Pose pose) {
        final Optional<Odometry> odometryOptional = odometrySubscriber.getMostRecentMessage();
        if (odometryOptional.isPresent()) {
            final BodyFrameVelocity bodyFrameVelocity = Velocity.createLocalVelocityFrom(
                    odometryOptional.get().getTwist().getTwist());
            final InertialFrameVelocity inertialFrameVelocity = Transformations
                    .bodyFrameVelocityToInertialFrameVelocity(
                    bodyFrameVelocity, pose);
            return Optional.of(inertialFrameVelocity);
        } else {
            logger.debug("Cannot get Bebop odometry.");
            return Optional.absent();
        }
    }
}
