package applications.parrot.bebop;

import commands.Command;
import commands.Land;
import commands.MoveToPose;
import commands.Takeoff;
import control.PidParameters;
import control.dto.Pose;
import control.localization.BebopStateEstimatorWithPoseStampedAndOdom;
import control.localization.StateEstimator;
import geometry_msgs.PoseStamped;
import nav_msgs.Odometry;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FlyingStateService;
import services.LandService;
import services.ServiceFactory;
import services.TakeOffService;
import services.VelocityService;
import services.parrot.BebopServiceFactory;
import services.parrot.ParrotServiceFactory;
import services.ros_subscribers.MessagesSubscriberService;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;

import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
public class BebopHover extends AbstractNodeMain {
    private static final Logger logger = LoggerFactory.getLogger(BebopHover.class);
    private static final String DRONE_NAME = "bebop";

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopHover");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final double pidLinearXKP = connectedNode.getParameterTree().getDouble("beswarm/pid_linear_x_kp");
        final double pidLinearXKI = connectedNode.getParameterTree().getDouble("beswarm/pid_linear_x_ki");
        final double pidLinearXKD = connectedNode.getParameterTree().getDouble("beswarm/pid_linear_x_kd");
        final double pidLinearYKP = connectedNode.getParameterTree().getDouble("beswarm/pid_linear_y_kp");
        final double pidLinearYKI = connectedNode.getParameterTree().getDouble("beswarm/pid_linear_y_ki");
        final double pidLinearYKD = connectedNode.getParameterTree().getDouble("beswarm/pid_linear_y_kd");
        final double flightDuration = connectedNode.getParameterTree().getDouble("beswarm/flight_duration");
        final double locationX = connectedNode.getParameterTree().getDouble("beswarm/location_x");
        final double locationY = connectedNode.getParameterTree().getDouble("beswarm/location_y");
        final double locationZ = connectedNode.getParameterTree().getDouble("beswarm/location_z");
        final double locationYaw = connectedNode.getParameterTree().getDouble("beswarm/location_yaw");

        logger.info("target location: (x,y,z,yaw) ({},{}, {}, {})", locationX, locationY, locationZ, locationYaw);

        final ParrotServiceFactory parrotServiceFactory = BebopServiceFactory.create(connectedNode, DRONE_NAME);
        TakeOffService takeoffService = parrotServiceFactory.createTakeOffService();
        VelocityService velocityService = parrotServiceFactory.createVelocityService();
        LandService landService = parrotServiceFactory.createLandService();
        final FlyingStateService flyingStateService = parrotServiceFactory.createFlyingStateService();

        final StateEstimator stateEstimator = BebopStateEstimatorWithPoseStampedAndOdom.create(
                getPoseSubscriber(connectedNode), getOdometrySubscriber(connectedNode));
        // without this code, the take off message cannot be sent properly (I don't understand why).
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            logger.info("Warm up time is interrupted.", e);
        }

        Command takeoff = Takeoff.create(takeoffService);
        Command moveToPose = MoveToPose.builder()
                .withVelocityService(velocityService)
                .withStateEstimator(stateEstimator)
                .withGoalPose(Pose.builder().x(locationX).y(locationY).z(locationZ).yaw(locationYaw).build())
                .withDurationInSeconds(flightDuration)
                .withPidLinearXParameters(
                        PidParameters.builder().kp(pidLinearXKP).ki(pidLinearXKI).kd(pidLinearXKD).build())
                .withPidLinearYParameters(
                        PidParameters.builder().kp(pidLinearYKP).ki(pidLinearYKI).kd(pidLinearYKD).build())
                .build();
        Command land = Land.create(landService, flyingStateService);

        final TaskExecutor taskExecutor = TaskExecutorService.create();
        taskExecutor.submitTask(Task.create(TaskType.NORMAL_TASK, takeoff, moveToPose, land));
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
