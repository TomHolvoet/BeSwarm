package applications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import commands.Command;
import commands.FollowTrajectory;
import commands.Hover;
import commands.Land;
import commands.Takeoff;
import control.localization.PoseEstimator;
import control.Trajectory4d;
import control.localization.VelocityEstimator;
import control.dto.Pose;
import control.dto.Velocity;
import geometry_msgs.PoseStamped;
import geometry_msgs.Twist;
import nav_msgs.Odometry;
import services.LandService;
import services.TakeoffService;
import services.VelocityService;
import services.parrot.ParrotLandService;
import services.parrot.ParrotServiceFactory;
import services.parrot.ParrotTakeOffService;
import services.parrot.ParrotVelocityService;
import services.ros_subscribers.MessagesSubscriberService;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;

/**
 * Application to fly the Bebop drone in a straighline
 *
 * @author mhct
 */
public final class BebopLinePattern extends AbstractNodeMain {
	private final Logger logger = LoggerFactory.getLogger(BebopLinePattern.class);
	
	private final String droneName = "/bebop";
	private String POSE_TOPIC;
	
    private TakeoffService takeoffService;
    private VelocityService velocityService;
    private LandService landService;

    private MessagesSubscriberService<PoseStamped> poseSubscriber;
	private MessagesSubscriberService<Odometry> odometrySubscriber;

	private double flightDuration = 100; //Want to have a 100 seconds flight

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopLinePattern");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
//    	POSE_TOPIC = connectedNode.getParameterTree().getString("/BebopLinePattern/pose_topic");
    	POSE_TOPIC = "/arlocros/pose";
    	initializeComm(connectedNode);
        warmUp();
        final Task flyTask = createFlyTask();

        final TaskExecutor taskExecutor = TaskExecutorService.create();
        taskExecutor.submitTask(flyTask);
    }

    private Task createFlyTask() {
    	logger.info("Creating command");
        final Collection<Command> commands = new ArrayList<>();

        final Command takeOff = Takeoff.create(takeoffService);
        final Command hoverFiveSeconds = Hover.create(velocityService, 5);
        final Command followTrajectory = getFollowTrajectoryCommand();
        final Command land = Land.create(landService);

        commands.add(takeOff);
        commands.add(hoverFiveSeconds);
        commands.add(followTrajectory);
        commands.add(land);

        return Task.create(ImmutableList.copyOf(commands), TaskType.NORMAL_TASK);
    }

    private Command getFollowTrajectoryCommand() {

        final PoseEstimator poseEstimator = new PoseEstimator() {
        	@Override
			public Optional<Pose> getCurrentPose() {
        		logger.trace("INNER pose getCurrent called");
				Optional<PoseStamped> poseStamped = poseSubscriber.getMostRecentMessage();

				if (poseStamped.isPresent()) {
					logger.trace("Got PoseStamped");
					return Optional.of(Pose.create(poseStamped.get()));
				} else {
					logger.trace("Cannot get PoseStamped");
					return Optional.absent();
				}
			}
		};

//		Optional<Pose> ret = poseEstimator.getCurrentPose();
//		if (ret.isPresent()) {
//			logger.info(ret.get().toString());
//		}
		
		final VelocityEstimator velocityEstimator = new VelocityEstimator() {

			@Override
			public Optional<Velocity> getCurrentVelocity() {
				Optional<Odometry> odometry = odometrySubscriber.getMostRecentMessage();

				if (odometry.isPresent()) {
					logger.trace("Got Odometry");
					return Optional.of(Velocity.createLocalVelocityFrom(odometry.get().getTwist().getTwist()));
				} else {
					return Optional.absent();
				}
			}
		};

        final Trajectory4d trajectory = LineTrajectory.create(flightDuration, 2.0);
        logger.info("finished creating trajectory");
        return FollowTrajectory.builder()
                .poseEstimator(poseEstimator)
                .velocityEstimator(velocityEstimator)
                .velocityPublisher(velocityService)
                .trajectory4d(trajectory)
                .durationInSeconds(flightDuration )
                .build();
    }

    private static void warmUp() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            // TODO write to log
        }
    }

    private void initializeComm(ConnectedNode connectedNode) {
    	ParrotServiceFactory serviceFactory = ParrotServiceFactory.create(droneName, connectedNode);
    	
    	Publisher<Twist> cameraControl = connectedNode.<Twist>newPublisher("/bebop/camera_control", Twist._TYPE);
    	
    	//
    	// Points camera downwards check (http://bebop-autonomy.readthedocs.io/en/latest/piloting.html#moving-the-virtual-camera)
    	//
    	Twist msg = cameraControl.newMessage();
    	msg.getAngular().setY(-50);
    	cameraControl.publish(msg);
    	
    	landService = serviceFactory.createLandService();
    	takeoffService = serviceFactory.createTakeoffService();
        velocityService = serviceFactory.createVelocityService();

        poseSubscriber = MessagesSubscriberService.<PoseStamped>create(connectedNode.<PoseStamped>newSubscriber(POSE_TOPIC, PoseStamped._TYPE));
        odometrySubscriber = MessagesSubscriberService.<Odometry>create(connectedNode.<Odometry>newSubscriber(droneName + "/odom", Odometry._TYPE));
        
    	poseSubscriber.startListeningToMessages();
    	odometrySubscriber.startListeningToMessages();
    }
}
