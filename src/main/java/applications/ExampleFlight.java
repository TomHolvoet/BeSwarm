package applications;

import com.google.common.collect.ImmutableList;
import commands.Command;
import commands.FollowTrajectory;
import commands.Hover;
import commands.Land;
import commands.Takeoff;
import control.DefaultPidParameters;
import control.FiniteTrajectory4d;
import control.PidParameters;
import control.localization.StateEstimator;
import keyboard.Key;
import org.ros.node.ConnectedNode;
import services.FlyingStateService;
import services.LandService;
import services.ServiceFactory;
import services.TakeOffService;
import services.VelocityService;
import services.ros_subscribers.KeyboardSubscriber;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;
import taskexecutor.interruptors.KeyboardEmergency;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class illustrates an example flight. The drone will take off, hover
 * in 5 second, follow a trajectory and then
 * land.
 *
 * @author Hoang Tung Dinh
 */
public final class ExampleFlight {

    private final TakeOffService takeOffService;
    private final LandService landService;
    private final VelocityService velocityService;
    private final FlyingStateService flyingStateService;
    private final StateEstimator stateEstimator;
    private final FiniteTrajectory4d finiteTrajectory4d;
    private final ConnectedNode connectedNode;

    private final PidParameters pidLinearX;
    private final PidParameters pidLinearY;
    private final PidParameters pidLinearZ;
    private final PidParameters pidAngularZ;

    private ExampleFlight(ServiceFactory serviceFactory, StateEstimator stateEstimator,
            FiniteTrajectory4d finiteTrajectory4d, ConnectedNode connectedNode, PidParameters pidLinearX,
            PidParameters pidLinearY, PidParameters pidLinearZ, PidParameters pidAngularZ) {
        this.takeOffService = serviceFactory.createTakeOffService();
        this.landService = serviceFactory.createLandService();
        this.velocityService = serviceFactory.createVelocityService();
        this.flyingStateService = serviceFactory.createFlyingStateService();
        this.stateEstimator = stateEstimator;
        this.finiteTrajectory4d = finiteTrajectory4d;
        this.connectedNode = connectedNode;
        this.pidLinearX = pidLinearX;
        this.pidLinearY = pidLinearY;
        this.pidLinearZ = pidLinearZ;
        this.pidAngularZ = pidAngularZ;
    }

    public static ExampleFlight create(ServiceFactory serviceFactory, StateEstimator stateEstimator,
            FiniteTrajectory4d finiteTrajectory4d, ConnectedNode connectedNode) {
        return new ExampleFlight(serviceFactory, stateEstimator, finiteTrajectory4d, connectedNode,
                DefaultPidParameters.LINEAR_X.getParameters(), DefaultPidParameters.LINEAR_Y.getParameters(),
                DefaultPidParameters.LINEAR_Z.getParameters(), DefaultPidParameters.ANGULAR_Z.getParameters());
    }

    public static ExampleFlight create(ServiceFactory serviceFactory, StateEstimator stateEstimator,
            FiniteTrajectory4d finiteTrajectory4d, ConnectedNode connectedNode, PidParameters pidLinearX,
            PidParameters pidLinearY, PidParameters pidLinearZ, PidParameters pidAngularZ) {
        return new ExampleFlight(serviceFactory, stateEstimator, finiteTrajectory4d, connectedNode, pidLinearX,
                pidLinearY, pidLinearZ, pidAngularZ);
    }

    public void fly() {
        // task to execute in case of emergency
        final Task emergencyTask = createEmergencyTask();
        final KeyboardEmergency keyboardEmergencyNotifier = createKeyboardEmergencyNotifier(emergencyTask);

        final TaskExecutor taskExecutor = TaskExecutorService.create();
        keyboardEmergencyNotifier.registerTaskExecutor(taskExecutor);

        // normal fly task
        final Task flyTask = createFlyTask();
        taskExecutor.submitTask(flyTask);
    }

    private Task createFlyTask() {
        final Collection<Command> commands = new ArrayList<>();

        final Command takeOff = Takeoff.create(takeOffService);
        commands.add(takeOff);

        final Command hoverFiveSecond = Hover.create(velocityService, stateEstimator, 5);
        commands.add(hoverFiveSecond);

        final Command followTrajectory = FollowTrajectory.builder()
                .stateEstimator(stateEstimator)
                .velocityService(velocityService)
                .trajectory4d(finiteTrajectory4d)
                .durationInSeconds(finiteTrajectory4d.getTrajectoryDuration())
                .pidLinearXParameters(pidLinearX)
                .pidLinearYParameters(pidLinearY)
                .pidLinearZParameters(pidLinearZ)
                .pidAngularZParameters(pidAngularZ)
                .build();
        commands.add(followTrajectory);

        final Command land = Land.create(landService, flyingStateService);
        commands.add(land);

        return Task.create(ImmutableList.copyOf(commands), TaskType.NORMAL_TASK);
    }

    private Task createEmergencyTask() {
        final Command land = Land.create(landService, flyingStateService);
        return Task.create(ImmutableList.of(land), TaskType.FIRST_ORDER_EMERGENCY);
    }

    private KeyboardEmergency createKeyboardEmergencyNotifier(Task emergencyTask) {
        final KeyboardSubscriber keyboardSubscriber = KeyboardSubscriber.createKeyboardSubscriber(
                connectedNode.<Key>newSubscriber("/keyboard/keydown", Key._TYPE));
        final KeyboardEmergency keyboardEmergency = KeyboardEmergency.create(emergencyTask);
        keyboardSubscriber.registerObserver(keyboardEmergency);
        return keyboardEmergency;
    }
}
