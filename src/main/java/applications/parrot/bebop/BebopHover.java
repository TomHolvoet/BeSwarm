package applications.parrot.bebop;

import java.util.concurrent.TimeUnit;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import commands.Command;
import commands.Land;
import commands.MoveToPose;
import commands.Takeoff;
import control.PidParameters;
import control.dto.BodyFrameVelocity;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import control.localization.StateEstimator;
import geometry_msgs.PoseStamped;
import nav_msgs.Odometry;
import services.LandService;
import services.ServiceFactory;
import services.TakeOffService;
import services.VelocityService;
import services.parrot.BebopServiceFactory;
import services.ros_subscribers.MessagesSubscriberService;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;
import utils.math.Transformations;
import control.dto.DroneStateStamped;

/**
 * @author Hoang Tung Dinh
 */
public class BebopHover extends AbstractNodeMain {
    private static final Logger logger = LoggerFactory.getLogger(BebopHover.class);
    private static final String DRONE_NAME = "bebop";

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopSimpleLinePattern");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final double pidKP = connectedNode.getParameterTree().getDouble("beswarm/pid_kp");
        final double pidKI = connectedNode.getParameterTree().getDouble("beswarm/pid_ki");
        final double pidKD = connectedNode.getParameterTree().getDouble("beswarm/pid_kd");
        final double flightDuration = connectedNode.getParameterTree().getDouble("beswarm/flight_duration");
        final double locationX = connectedNode.getParameterTree().getDouble("beswarm/location_x");
        final double locationY = connectedNode.getParameterTree().getDouble("beswarm/location_y");
        final double locationZ = connectedNode.getParameterTree().getDouble("beswarm/location_z");
        final double locationYaw = connectedNode.getParameterTree().getDouble("beswarm/location_yaw");
        
        final ServiceFactory serviceFactory = BebopServiceFactory.create(connectedNode, DRONE_NAME);
        TakeOffService takeoffService = serviceFactory.createTakeOffService();
        VelocityService velocityService = serviceFactory.createVelocityService();
        LandService landService = serviceFactory.createLandService();

        final StateEstimator stateEstimator = BebopStateEstimator.create(getPoseSubscriber(connectedNode),
                getOdometrySubscriber(connectedNode));
        // without this code, the take off message cannot be sent properly (I don't understand why).
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            logger.info("Warm up time is interrupted.", e);
        }

       
    	Command takeoff = Takeoff.create(takeoffService);
    	Command moveToPose = MoveToPose.builder().goalPose(Pose.builder().x(locationX).y(locationY).z(locationZ).yaw(locationYaw).build())
    			.velocityService(velocityService)
    			.stateEstimator(stateEstimator)
    			.pidLinearXParameters(PidParameters.builder().kp(pidKP).ki(pidKI).kd(pidKD).build())
    			.durationInSeconds(flightDuration)
    			.build();
    	Command land = Land.create(landService);
    	
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

    private static final class BebopStateEstimator implements StateEstimator {

        private static final Logger logger = LoggerFactory.getLogger(BebopStateEstimator.class);

        private final MessagesSubscriberService<PoseStamped> poseSubscriber;
        private final MessagesSubscriberService<Odometry> odometrySubscriber;

        private BebopStateEstimator(MessagesSubscriberService<PoseStamped> poseSubscriber,
                MessagesSubscriberService<Odometry> odometrySubscriber) {
            this.poseSubscriber = poseSubscriber;
            this.odometrySubscriber = odometrySubscriber;
        }

        public static BebopStateEstimator create(MessagesSubscriberService<PoseStamped> poseSubscriber,
                MessagesSubscriberService<Odometry> odometrySubscriber) {
            return new BebopStateEstimator(poseSubscriber, odometrySubscriber);
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

            final DroneStateStamped droneState = DroneStateStamped.create(pose, inertialFrameVelocity.get(), poseStamped.get().getHeader().getStamp().toSeconds());
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
}
