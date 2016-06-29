package applications.parrot.bebop;

import java.util.concurrent.TimeUnit;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import applications.ExampleFlight;
import applications.LineTrajectory;
import control.Trajectory4d;
import control.localization.StateEstimator;
import geometry_msgs.PoseStamped;
import nav_msgs.Odometry;
import services.ServiceFactory;
import services.parrot.BebopServiceFactory;
import services.ros_subscribers.MessagesSubscriberService;

/**
 * @author Hoang Tung Dinh
 */
public class BebopSimpleLinePattern extends AbstractNodeMain {
    private static final Logger logger = LoggerFactory.getLogger(BebopSimpleLinePattern.class);
    private static final String DRONE_NAME = "bebop";
    private static final double NANO_SECOND_TO_SECOND = 1000000000.0;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopSimpleLinePattern");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final double flightDuration = connectedNode.getParameterTree().getDouble("beswarm/flight_duration");

        
        final ServiceFactory serviceFactory = BebopServiceFactory.create(connectedNode, DRONE_NAME);
        final StateEstimator stateEstimator = applications.parrot.bebop.BebopHover.BebopStateEstimator.create(getPoseSubscriber(connectedNode),
                getOdometrySubscriber(connectedNode));
        final Trajectory4d trajectory4d = LineTrajectory.create(flightDuration, 2.0);
        final ExampleFlight exampleFlight = ExampleFlight.create(serviceFactory, stateEstimator, trajectory4d,
                connectedNode);

        // without this code, the take off message cannot be sent properly (I don't understand why).
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            logger.info("Warm up time is interrupted.", e);
        }

        exampleFlight.fly();
    }

    private static MessagesSubscriberService<PoseStamped> getPoseSubscriber(ConnectedNode connectedNode) {
        final String poseTopic = "/arlocros/pose";
        logger.info("Subscribed to {} for getting pose.", poseTopic);
        return MessagesSubscriberService.create(connectedNode.<PoseStamped>newSubscriber(poseTopic, PoseStamped._TYPE));
    }

    private static MessagesSubscriberService<Odometry> getOdometrySubscriber(ConnectedNode connectedNode) {
        final String odometryTopic = "/" + DRONE_NAME + "/odom";
        logger.info("Subscribed to {} for getting odometry", odometryTopic);
        return MessagesSubscriberService.create(connectedNode.<Odometry>newSubscriber(odometryTopic, Odometry._TYPE));
    }

}
