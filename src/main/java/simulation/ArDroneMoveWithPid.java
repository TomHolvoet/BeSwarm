package simulation;

import behavior.Command;
import behavior.Hover;
import behavior.Land;
import behavior.MoveToPose;
import behavior.Pose;
import behavior.Takeoff;
import behavior.Velocity;
import com.google.common.collect.ImmutableList;
import comm.KeyboardSubscriber;
import comm.LandPublisher;
import comm.ModelStateSubscriber;
import comm.TakeoffPublisher;
import comm.VelocityPublisher;
import control.ModelStatePoseProvider;
import control.ModelStateVelocityProvider;
import control.PoseProvider;
import control.VelocityProvider;
import gazebo_msgs.ModelStates;
import geometry_msgs.Twist;
import keyboard.Key;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import std_msgs.Empty;
import taskexecutor.KeyboardEmergency;
import taskexecutor.SimpleEmergencyAfterOneMinute;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;

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
    private TakeoffPublisher takeoffPublisher;
    private LandPublisher landPublisher;
    private VelocityPublisher velocityPublisher;
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

        final SimpleEmergencyAfterOneMinute oneMinuteEmergencyNotifier = SimpleEmergencyAfterOneMinute.create(emergencyTask);
        final KeyboardEmergency keyboardEmergencyNotifier = createKeyboardEmergencyNotifier(emergencyTask);

        oneMinuteEmergencyNotifier.registerTaskExecutor(taskExecutor);
        keyboardEmergencyNotifier.registerTaskExecutor(taskExecutor);

        taskExecutor.submitTask(flyTask);
        oneMinuteEmergencyNotifier.run();
    }

    private KeyboardEmergency createKeyboardEmergencyNotifier(Task emergencyTask) {
        final KeyboardEmergency keyboardEmergency = KeyboardEmergency.create(emergencyTask);
        keyboardSubscriber.startListeningToMessages();
        keyboardSubscriber.registerObserver(keyboardEmergency);
        return keyboardEmergency;
    }

    private Task createEmergencyTask() {
        final Command land = Land.create(landPublisher);
        return Task.create(ImmutableList.of(land), TaskType.FIRST_ORDER_EMERGENCY);
    }

    private Task createFlyTask() {
        final Collection<Command> commands = new ArrayList<>();

        final Command takeOff = Takeoff.create(takeoffPublisher);
        commands.add(takeOff);

        final Command hoverFiveSecond = Hover.create(velocityPublisher, 5);
        commands.add(hoverFiveSecond);

        final String modelName = "quadrotor";
        final PoseProvider poseProvider = ModelStatePoseProvider.create(modelStateSubscriber, modelName);
        final VelocityProvider velocityProvider = ModelStateVelocityProvider.create(modelStateSubscriber, modelName);
        final Pose goalPose = Pose.builder().x(3).y(-3).z(3).yaw(1).build();
        final Velocity goalVelocity = Velocity.builder().linearX(0).linearY(0).linearZ(0).angularZ(0).build();
        final Command moveToPose = MoveToPose.builder()
                .poseProvider(poseProvider)
                .velocityProvider(velocityProvider)
                .velocityPublisher(velocityPublisher)
                .goalPose(goalPose)
                .goalVelocity(goalVelocity)
                .durationInSeconds(60)
                .build();
        commands.add(moveToPose);
        return Task.create(ImmutableList.copyOf(commands), TaskType.NORMAL_TASK);
    }

    private static void warmUp() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            // TODO write to log
        }
    }

    private void initializeComm(ConnectedNode connectedNode) {
        takeoffPublisher = TakeoffPublisher.create(connectedNode.<Empty>newPublisher("/ardrone/takeoff", Empty._TYPE));
        velocityPublisher = VelocityPublisher.builder()
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
        landPublisher = LandPublisher.create(connectedNode.<Empty>newPublisher("/ardrone/land", Empty._TYPE));
        keyboardSubscriber = KeyboardSubscriber.create(
                connectedNode.<Key>newSubscriber("/keyboard/keydown", Key._TYPE));
    }
}
