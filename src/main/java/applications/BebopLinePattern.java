package applications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import commands.Command;
import commands.FollowTrajectory;
import commands.Hover;
import commands.Land;
import commands.Takeoff;
import control.PoseEstimator;
import control.Trajectory4d;
import control.VelocityEstimator;
import control.dto.Pose;
import control.dto.Velocity;
import geometry_msgs.PoseStamped;
import geometry_msgs.Twist;
import nav_msgs.Odometry;
import services.LandService;
import services.TakeoffService;
import services.VelocityService;
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
	private final String droneName = "/bebop";
	
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
        initializeComm(connectedNode);
        warmUp();

        final Task flyTask = createFlyTask();

        final TaskExecutor taskExecutor = TaskExecutorService.create();
        taskExecutor.submitTask(flyTask);
    }

    private Task createFlyTask() {
        final Collection<Command> commands = new ArrayList<>();

        final Command takeOff = Takeoff.create(takeoffService);
        final Command hoverFiveSeconds = Hover.create(velocityService, 5);
        final Command followTrajectory = getFollowTrajectoryCommand();
        final Command land = Land.create(landService);
        
        commands.add(takeOff);
        commands.add(hoverFiveSeconds);
//        commands.add(followTrajectory);
        commands.add(land);
        
        return Task.create(ImmutableList.copyOf(commands), TaskType.NORMAL_TASK);
    }

    private Command getFollowTrajectoryCommand() {
        
        final PoseEstimator poseEstimator = new PoseEstimator() {
			
        	@Override
			public Optional<Pose> getCurrentPose() {
				Optional<PoseStamped> poseStamped = poseSubscriber.getMostRecentMessage();
				
				if (poseStamped.isPresent()) {
					return Optional.of(Pose.create(poseStamped.get()));
				} else {
					return Optional.absent();
				}
			}
		};
		
		final VelocityEstimator velocityEstimator = new VelocityEstimator() {
			
			@Override
			public Optional<Velocity> getCurrentVelocity() {
				Optional<Odometry> odometry = odometrySubscriber.getMostRecentMessage();
				
				if (odometry.isPresent()) {
					return Optional.of(Velocity.createLocalVelocityFrom(odometry.get().getTwist().getTwist()));
				} else {
					return Optional.absent();
				}
			}
		};
        		
        final Trajectory4d trajectory = LineTrajectory.create(10.0, 2.0);
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
    	landService = LandService.createService(droneName, connectedNode);
    	takeoffService = TakeoffService.createService(droneName, connectedNode);
        velocityService = VelocityService.builder()
                .publisher(connectedNode.<Twist>newPublisher("/cmd_vel", Twist._TYPE))
                .minLinearX(-1)
                .minLinearY(-1)
                .minLinearZ(-1)
                .minAngularZ(-1)
                .maxLinearX(1)
                .maxLinearY(1)
                .maxLinearZ(1)
                .maxAngularZ(1)
                .build();
        
        poseSubscriber = MessagesSubscriberService.<PoseStamped>create(connectedNode.<PoseStamped>newSubscriber("/arlocros/pose", PoseStamped._TYPE));
        odometrySubscriber = MessagesSubscriberService.<Odometry>create(connectedNode.<Odometry>newSubscriber(droneName + "/odom", Odometry._TYPE));
    }
}
