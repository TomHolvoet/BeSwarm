package applications.parrot.bebop;

import applications.ExampleFlight;
import applications.LineTrajectory;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.localization.BebopStateEstimatorWithPoseStampedAndOdom;
import control.localization.StateEstimator;
import geometry_msgs.PoseStamped;
import nav_msgs.Odometry;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.parrot.BebopServiceFactory;
import services.parrot.ParrotServiceFactory;
import services.rossubscribers.MessagesSubscriberService;

import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
public class BebopSimpleLinePattern extends AbstractNodeMain {
    private static final Logger logger = LoggerFactory.getLogger(BebopSimpleLinePattern.class);
    private static final String DRONE_NAME = "bebop";

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopSimpleLinePattern");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final double flightDuration = connectedNode.getParameterTree()
                .getDouble("beswarm/flight_duration");

        final ParrotServiceFactory parrotServiceFactory = BebopServiceFactory.create(connectedNode,
                DRONE_NAME);
        final StateEstimator stateEstimator = BebopStateEstimatorWithPoseStampedAndOdom.create(
                getPoseSubscriber(connectedNode), getOdometrySubscriber(connectedNode));
        final FiniteTrajectory4d choreography = Choreography.builder()
                .withTrajectory(LineTrajectory.create(flightDuration, 2.0))
                .forTime(flightDuration)
                .build();
        final ExampleFlight exampleFlight = ExampleFlight.builder()
                .withConnectedNode(connectedNode)
                .withFiniteTrajectory4d(choreography)
                .withFlyingStateService(parrotServiceFactory.createFlyingStateService())
                .withLandService(parrotServiceFactory.createLandService())
                .withStateEstimator(stateEstimator)
                .withTakeOffService(parrotServiceFactory.createTakeOffService())
                .withVelocityService(parrotServiceFactory.createVelocity4dService())
                .build();

        // without this code, the take off message cannot be sent properly (I
        // don't understand why).
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            logger.info("Warm up time is interrupted.", e);
            Thread.currentThread().interrupt();
        }

        exampleFlight.fly();
    }

    private static MessagesSubscriberService<PoseStamped> getPoseSubscriber(
            ConnectedNode connectedNode) {
        final String poseTopic = "/arlocros/pose";
        logger.info("Subscribed to {} for getting pose.", poseTopic);
        return MessagesSubscriberService.create(
                connectedNode.<PoseStamped>newSubscriber(poseTopic, PoseStamped._TYPE));
    }

    private static MessagesSubscriberService<Odometry> getOdometrySubscriber(
            ConnectedNode connectedNode) {
        final String odometryTopic = "/" + DRONE_NAME + "/odom";
        logger.info("Subscribed to {} for getting odometry", odometryTopic);
        return MessagesSubscriberService.create(
                connectedNode.<Odometry>newSubscriber(odometryTopic, Odometry._TYPE));
    }

}
