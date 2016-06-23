package applications.simulations;

import com.google.common.collect.ImmutableList;
import commands.Command;
import commands.FollowTrajectory;
import commands.Hover;
import commands.Land;
import commands.MoveToPose;
import commands.Takeoff;
import control.Trajectory4d;
import control.dto.Pose;
import control.dto.Velocity;
import control.localization.CratesStatePoseEstimator;
import control.localization.CratesStateVelocityEstimator;
import control.localization.PoseEstimator;
import control.localization.VelocityEstimator;
import hal_quadrotor.LandRequest;
import hal_quadrotor.LandResponse;
import hal_quadrotor.State;
import hal_quadrotor.TakeoffRequest;
import hal_quadrotor.TakeoffResponse;
import hal_quadrotor.VelocityRequest;
import hal_quadrotor.VelocityResponse;
import keyboard.Key;
import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.LandService;
import services.TakeOffService;
import services.VelocityService;
import services.crates.CratesLandService;
import services.crates.CratesTakeOffService;
import services.crates.CratesVelocityService;
import services.ros_subscribers.KeyboardSubscriber;
import services.ros_subscribers.MessagesSubscriberService;
import sim.Insert;
import sim.InsertRequest;
import sim.InsertResponse;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;
import taskexecutor.interruptors.KeyboardEmergency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * This class is for running the simulation with the AR drone in the Tum simulator.
 *
 * @author Hoang Tung Dinh
 * @see <a href="https://github.com/dougvk/tum_simulator">The simulator</a>
 */
public final class CratesSimulatorExample extends AbstractNodeMain {
    private static final Logger logger = LoggerFactory.getLogger(CratesSimulatorExample.class);
    private TakeOffService takeOffService;
    private LandService landService;
    private VelocityService velocityService;
    private MessagesSubscriberService<State> cratesTruthStateSubscriber;
    private KeyboardSubscriber keyboardSubscriber;
    private static final String DRONE_NAME = "uav";

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("CratesSimulatorExample");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        addDroneModel(connectedNode);
        warmUp();
        initializeComm(connectedNode);

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
        final PoseEstimator poseEstimator = CratesStatePoseEstimator.create(cratesTruthStateSubscriber);
        final VelocityEstimator velocityEstimator = CratesStateVelocityEstimator.create(cratesTruthStateSubscriber);
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
        final PoseEstimator poseEstimator = CratesStatePoseEstimator.create(cratesTruthStateSubscriber);
        final VelocityEstimator velocityEstimator = CratesStateVelocityEstimator.create(cratesTruthStateSubscriber);
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
        final String srvNamePrefix = "/hal/quadrotor/hummingbird/" + DRONE_NAME + "/";
        try {
            takeOffService = CratesTakeOffService.create(
                    connectedNode.<TakeoffRequest, TakeoffResponse>newServiceClient(
                            srvNamePrefix + "controller/Takeoff", hal_quadrotor.Takeoff._TYPE));
            landService = CratesLandService.create(
                    connectedNode.<LandRequest, LandResponse>newServiceClient(srvNamePrefix + "controller/Land",
                            hal_quadrotor.Land._TYPE));
            velocityService = CratesVelocityService.create(
                    connectedNode.<VelocityRequest, VelocityResponse>newServiceClient(
                            srvNamePrefix + "controller/Velocity", hal_quadrotor.Velocity._TYPE));
        } catch (ServiceNotFoundException e) {
            logger.info("Cannot connect to some services.", e);
        }
        cratesTruthStateSubscriber = MessagesSubscriberService.create(
                connectedNode.<State>newSubscriber(srvNamePrefix + "Truth", State._TYPE), 2);
        cratesTruthStateSubscriber.startListeningToMessages();
        keyboardSubscriber = KeyboardSubscriber.createKeyboardSubscriber(
                connectedNode.<Key>newSubscriber("/keyboard/keydown", Key._TYPE));
    }

    private void addDroneModel(ConnectedNode connectedNode) {
        final String srvName = "/simulator/Insert";
        try {
            final ServiceClient<InsertRequest, InsertResponse> insertSrv = connectedNode.newServiceClient(srvName,
                    Insert._TYPE);
            final InsertRequest insertRequest = insertSrv.newMessage();
            insertRequest.setModelName(DRONE_NAME);
            insertRequest.setModelType("model://hummingbird");
            insertSrv.call(insertRequest, InsertServiceResponseListener.create());
            warmUp();
        } catch (ServiceNotFoundException e) {
            logger.info("Cannot connect to insert service.", e);
        }
    }

    private static final class InsertServiceResponseListener implements ServiceResponseListener<InsertResponse> {
        private InsertServiceResponseListener() {}

        public static InsertServiceResponseListener create() {
            return new InsertServiceResponseListener();
        }

        @Override
        public void onSuccess(InsertResponse insertResponse) {
            logger.info("Successfully inserted the drone model!!!");
            logger.info(insertResponse.getStatusMessage());
        }

        @Override
        public void onFailure(RemoteException e) {
            logger.info("Cannot insert the drone model!!!", e);
        }
    }
}
