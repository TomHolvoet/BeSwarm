package applications.simulations;

import com.google.common.collect.ImmutableList;

import commands.Command;
import commands.FollowTrajectory;
import commands.Hover;
import commands.Land;
import commands.MoveToPose;
import commands.Takeoff;
import control.ModelStatePoseEstimator;
import control.ModelStateVelocityEstimator;
import control.PoseEstimator;
import control.Trajectory4d;
import control.VelocityEstimator;
import control.dto.Pose;
import control.dto.Velocity;
import gazebo_msgs.ModelStates;
import geometry_msgs.Twist;
import keyboard.Key;
import services.LandService;
import services.ParrotLandService;
import services.ParrotTakeOffService;
import services.ParrotVelocityService;
import services.TakeOffService;
import services.VelocityService;
import services.ros_subscribers.KeyboardSubscriber;
import services.ros_subscribers.ModelStateSubscriber;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import std_msgs.Empty;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;
import taskexecutor.interruptors.KeyboardEmergency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * This class is for running the simulation with the AR drone.
 *
 * @author Hoang Tung Dinh
 * @see <a href="https://github.com/dougvk/tum_simulator">The simulator</a>
 */
public final class ArDroneMoveWithPid extends AbstractNodeMain {
    private TakeOffService takeOffService;
    private LandService landService;
    private VelocityService velocityService;
    private ModelStateSubscriber modelStateSubscriber;
    private KeyboardSubscriber keyboardSubscriber;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ArDroneMoveWithPid");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        initializeComm(connectedNode);
        warmUp();

        final Task flyTask = createFlyTask();
        final Task emergencyTask = createEmergencyTask();

        final TaskExecutor taskExecutor = TaskExecutorService.create();

        final KeyboardEmergency keyboardEmergencyNotifier = createKeyboardEmergencyNotifier(emergencyTask);
        keyboardEmergencyNotifier.registerTaskExecutor(taskExecutor);

        taskExecutor.submitTask(flyTask);
    }

    private KeyboardEmergency createKeyboardEmergencyNotifier(Task emergencyTask) {
        final KeyboardEmergency keyboardEmergency = KeyboardEmergency.create(emergencyTask);
        keyboardSubscriber.startListeningToMessages();
        keyboardSubscriber.registerObserver(keyboardEmergency);
        return keyboardEmergency;
    }

    private Task createEmergencyTask() {
        final Command land = Land.create(landService);
        return Task.create(ImmutableList.of(land), TaskType.FIRST_ORDER_EMERGENCY);
    }

    private Task createFlyTask() {
        final Collection<Command> commands = new ArrayList<>();

        final Command takeOff = Takeoff.create(takeOffService);
        commands.add(takeOff);

        final Command hoverFiveSecond = Hover.create(velocityService, 5);
        commands.add(hoverFiveSecond);

//        final Command moveToPose = getMoveToPoseCommand();
//        commands.add(moveToPose);

        final Command followTrajectory = getFollowTrajectoryCommand();
        commands.add(followTrajectory);

        return Task.create(ImmutableList.copyOf(commands), TaskType.NORMAL_TASK);
    }

    private Command getMoveToPoseCommand() {
        final String modelName = "quadrotor";
        final PoseEstimator poseEstimator = ModelStatePoseEstimator.create(modelStateSubscriber, modelName);
        final VelocityEstimator velocityEstimator = ModelStateVelocityEstimator.create(modelStateSubscriber, modelName);
        final Pose goalPose = Pose.builder().x(3).y(-3).z(3).yaw(1).build();
        final Velocity goalVelocity = Velocity.builder().linearX(0).linearY(0).linearZ(0).angularZ(0).build();
        return MoveToPose.builder()
                .poseEstimator(poseEstimator)
                .velocityEstimator(velocityEstimator)
                .velocityPublisher(velocityService)
                .goalPose(goalPose)
                .goalVelocity(goalVelocity)
                .durationInSeconds(60)
                .build();
    }

    private Command getFollowTrajectoryCommand() {
        final String modelName = "quadrotor";
        final PoseEstimator poseEstimator = ModelStatePoseEstimator.create(modelStateSubscriber, modelName);
        final VelocityEstimator velocityEstimator = ModelStateVelocityEstimator.create(modelStateSubscriber, modelName);
        final Trajectory4d trajectory = ExampleTrajectory2.create();
        return FollowTrajectory.builder()
                .poseEstimator(poseEstimator)
                .velocityEstimator(velocityEstimator)
                .velocityPublisher(velocityService)
                .trajectory4d(trajectory)
                .durationInSeconds(60)
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
        takeOffService = ParrotTakeOffService.create(connectedNode.<Empty>newPublisher("/ardrone/takeoff", Empty._TYPE));
        velocityService = ParrotVelocityService.builder()
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
        modelStateSubscriber = ModelStateSubscriber.create(
                connectedNode.<ModelStates>newSubscriber("/gazebo/model_states", ModelStates._TYPE));
        landService = ParrotLandService.create(connectedNode.<Empty>newPublisher("/ardrone/land", Empty._TYPE));
        keyboardSubscriber = KeyboardSubscriber.create(
                connectedNode.<Key>newSubscriber("/keyboard/keydown", Key._TYPE));
    }
}
