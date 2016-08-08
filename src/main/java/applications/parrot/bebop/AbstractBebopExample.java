package applications.parrot.bebop;

import applications.ExampleFlight;
import applications.trajectory.TrajectoryServer;
import control.FiniteTrajectory4d;
import control.PidParameters;
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
public abstract class AbstractBebopExample extends AbstractNodeMain implements TrajectoryServer {
    private static final Logger logger = LoggerFactory.getLogger(AbstractBebopExample.class);
    private static final String DRONE_NAME = "bebop";
    private final String nodeName;

    protected AbstractBebopExample(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(nodeName);
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final PidParameters pidLinearX = getPidParameters(connectedNode, "beswarm/pid_linear_x_kp",
                "beswarm/pid_linear_x_kd", "beswarm/pid_linear_x_ki");
        final PidParameters pidLinearY = getPidParameters(connectedNode, "beswarm/pid_linear_y_kp",
                "beswarm/pid_linear_y_kd", "beswarm/pid_linear_y_ki");
        final PidParameters pidLinearZ = getPidParameters(connectedNode, "beswarm/pid_linear_z_kp",
                "beswarm/pid_linear_z_kd", "beswarm/pid_linear_z_ki");
        final PidParameters pidAngularZ = getPidParameters(connectedNode, "beswarm/pid_angular_z_kp",
                "beswarm/pid_angular_z_kd", "beswarm/pid_angular_z_ki");

        final ParrotServiceFactory parrotServiceFactory = BebopServiceFactory.create(connectedNode, DRONE_NAME);
        final StateEstimator stateEstimator = BebopStateEstimatorWithPoseStampedAndOdom.create(
                getPoseSubscriber(connectedNode), getOdometrySubscriber(connectedNode));

        final FiniteTrajectory4d trajectory4d = getConcreteTrajectory();

        final ExampleFlight exampleFlight = ExampleFlight.builder()
                .withConnectedNode(connectedNode)
                .withFiniteTrajectory4d(trajectory4d)
                .withFlyingStateService(parrotServiceFactory.createFlyingStateService())
                .withLandService(parrotServiceFactory.createLandService())
                .withPidLinearX(pidLinearX)
                .withPidLinearY(pidLinearY)
                .withPidLinearZ(pidLinearZ)
                .withPidAngularZ(pidAngularZ)
                .withStateEstimator(stateEstimator)
                .withTakeOffService(parrotServiceFactory.createTakeOffService())
                .withVelocityService(parrotServiceFactory.createVelocity4dService())
                .build();

        // without this code, the take off message cannot be sent properly (I don't understand why).
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            logger.info("Warm up time is interrupted.", e);
            Thread.currentThread().interrupt();
        }

        exampleFlight.fly();
    }

    private static PidParameters getPidParameters(ConnectedNode connectedNode, String argKp, String argKd,
            String argKi) {
        final double pidLinearXKp = connectedNode.getParameterTree().getDouble(argKp);
        final double pidLinearXKd = connectedNode.getParameterTree().getDouble(argKd);
        final double pidLinearXKi = connectedNode.getParameterTree().getDouble(argKi);
        return PidParameters.builder().setKp(pidLinearXKp).setKd(pidLinearXKd).setKi(pidLinearXKi).build();
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
