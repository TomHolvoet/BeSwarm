package applications.parrot.bebop;

import applications.ExampleFlight;
import applications.LineTrajectory;
import choreo.Choreography;
import com.google.common.base.Optional;
import control.FiniteTrajectory4d;
import control.dto.*;
import control.localization.StateEstimator;
import geometry_msgs.PoseStamped;
import nav_msgs.Odometry;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ServiceFactory;
import services.parrot.BebopServiceFactory;
import services.ros_subscribers.MessagesSubscriberService;
import utils.math.Transformations;

import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
public class BebopSimpleLinePattern extends AbstractNodeMain {
    private static final Logger logger = LoggerFactory
            .getLogger(BebopSimpleLinePattern.class);
    private static final String DRONE_NAME = "bebop";
    private static final double NANO_SECOND_TO_SECOND = 1000000000.0;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopSimpleLinePattern");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final ServiceFactory serviceFactory = BebopServiceFactory
                .create(connectedNode, DRONE_NAME);
        final StateEstimator stateEstimator = BebopStateEstimator
                .create(getPoseSubscriber(connectedNode),
                        getOdometrySubscriber(connectedNode));
        final double flightDuration = 100;
        final FiniteTrajectory4d trajectory4d = Choreography.builder()
                .withTrajectory(LineTrajectory.create(flightDuration, 2.0))
                .forTime(flightDuration).build();
        final ExampleFlight exampleFlight = ExampleFlight
                .create(serviceFactory, stateEstimator, trajectory4d,
                        connectedNode);

        // without this code, the take off message cannot be sent properly (I
        // don't understand why).
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            logger.info("Warm up time is interrupted.", e);
        }

        exampleFlight.fly();
    }

    private static MessagesSubscriberService<PoseStamped> getPoseSubscriber(
            ConnectedNode connectedNode) {
        final String poseTopic = "/arlocros/pose";
        logger.info("Subscribed to {} for getting pose.", poseTopic);
        return MessagesSubscriberService
                .create(connectedNode.<PoseStamped>newSubscriber(poseTopic,
                        PoseStamped._TYPE));
    }

    private static MessagesSubscriberService<Odometry> getOdometrySubscriber(
            ConnectedNode connectedNode) {
        final String odometryTopic = "/" + DRONE_NAME + "/odom";
        logger.info("Subscribed to {} for getting odometry", odometryTopic);
        return MessagesSubscriberService
                .create(connectedNode.<Odometry>newSubscriber(odometryTopic,
                        Odometry._TYPE));
    }

    private static final class BebopStateEstimator implements StateEstimator {

        private static final Logger logger = LoggerFactory
                .getLogger(BebopStateEstimator.class);

        private final MessagesSubscriberService<PoseStamped> poseSubscriber;
        private final MessagesSubscriberService<Odometry> odometrySubscriber;

        private BebopStateEstimator(
                MessagesSubscriberService<PoseStamped> poseSubscriber,
                MessagesSubscriberService<Odometry> odometrySubscriber) {
            this.poseSubscriber = poseSubscriber;
            this.odometrySubscriber = odometrySubscriber;
        }

        public static BebopStateEstimator create(
                MessagesSubscriberService<PoseStamped> poseSubscriber,
                MessagesSubscriberService<Odometry> odometrySubscriber) {
            return new BebopStateEstimator(poseSubscriber, odometrySubscriber);
        }

        @Override
        public Optional<DroneStateStamped> getCurrentState() {

            final Optional<PoseStamped> poseStampedOptional = poseSubscriber
                    .getMostRecentMessage();
            if (!poseStampedOptional.isPresent()) {
                logger.debug("Cannot get Bebop pose.");
                return Optional.absent();
            }
            final Pose pose = Pose.create(poseStampedOptional.get());

            final Optional<InertialFrameVelocity> inertialFrameVelocity =
                    getVelocity(
                    pose);
            if (!inertialFrameVelocity.isPresent()) {
                return Optional.absent();
            }

            final DroneStateStamped droneState = DroneStateStamped
                    .create(pose, inertialFrameVelocity.get(),
                            poseStampedOptional.get().getHeader().getStamp()
                                    .toSeconds());
            return Optional.of(droneState);
        }

        private Optional<InertialFrameVelocity> getVelocity(Pose pose) {
            final Optional<Odometry> odometryOptional = odometrySubscriber
                    .getMostRecentMessage();
            if (odometryOptional.isPresent()) {
                final BodyFrameVelocity bodyFrameVelocity = Velocity
                        .createLocalVelocityFrom(
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
}
