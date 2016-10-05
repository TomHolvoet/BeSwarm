package applications.parrot.tumsim;

import applications.ExampleFlight;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import commands.Command;
import commands.ParrotFollowTrajectoryWithCP;
import commands.WaitForLocalizationDecorator;
import commands.tumsimcommands.TumSimFollowTrajectory;
import commands.tumsimcommands.TumSimHover;
import commands.tumsimcommands.TumSimLand;
import commands.tumsimcommands.TumSimTakeoff;
import control.DroneVelocityController;
import control.FiniteTrajectory4d;
import control.PidCoFilter4d;
import control.PidParameters;
import control.VelocityController4d;
import control.VelocityController4dLogger;
import control.dto.DroneStateStamped;
import gazebo_msgs.ModelStates;
import localization.FakeStateEstimatorDecorator;
import localization.GazeboModelStateEstimator;
import localization.StateEstimator;
import monitors.OutOfTrajectoryMonitor;
import monitors.PoseOutdatedMonitor;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.ros.node.ConnectedNode;
import org.ros.node.parameter.ParameterTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.CascadeBodyFrameVelocityFilter;
import services.FlyingStateService;
import services.LandService;
import services.MaxDiffBodyFrameVelocityFilter;
import services.MinDiffBodyFrameVelocityFilter;
import services.ResetService;
import services.TakeOffService;
import services.Velocity4dService;
import services.parrot.ParrotServiceFactory;
import services.parrot.TumSimServiceFactory;
import services.rossubscribers.MessagesSubscriberService;
import taskexecutor.Task;
import taskexecutor.TaskType;
import time.RosTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/** @author Hoang Tung Dinh */
final class TumExampleFlightFacade {
  private static final Logger logger = LoggerFactory.getLogger(TumExampleFlightFacade.class);
  private static final String MODEL_NAME = "quadrotor";
  private final ExampleFlight exampleFlight;
  private final StateEstimator stateEstimator;

  private TumExampleFlightFacade(
      FiniteTrajectory4d trajectory4d, final ConnectedNode connectedNode) {
    final String nodeName = connectedNode.getName().toString();
    final ParameterTree parameterTree = connectedNode.getParameterTree();
    final PidParameters pidLinearX =
        PidParameters.createUsingRosParams(
            parameterTree,
            nodeName + "/pid_linear_x_kp",
            nodeName + "/pid_linear_x_kd",
            nodeName + "/pid_linear_x_ki",
            nodeName + "/pid_lag_time_in_seconds");
    final PidParameters pidLinearY =
        PidParameters.createUsingRosParams(
            parameterTree,
            nodeName + "/pid_linear_y_kp",
            nodeName + "/pid_linear_y_kd",
            nodeName + "/pid_linear_y_ki",
            nodeName + "/pid_lag_time_in_seconds");
    final PidParameters pidLinearZ =
        PidParameters.createUsingRosParams(
            parameterTree,
            nodeName + "/pid_linear_z_kp",
            nodeName + "/pid_linear_z_kd",
            nodeName + "/pid_linear_z_ki",
            nodeName + "/pid_lag_time_in_seconds");
    final PidParameters pidAngularZ =
        PidParameters.createUsingRosParams(
            parameterTree,
            nodeName + "/pid_angular_z_kp",
            nodeName + "/pid_angular_z_kd",
            nodeName + "/pid_angular_z_ki",
            nodeName + "/pid_lag_time_in_seconds");

    final String trajectoryDurationParameterName = nodeName + "/trajectory_duration_in_seconds";
    final double trajectoryDurationInSeconds;
    if (parameterTree.has(trajectoryDurationParameterName)) {
      trajectoryDurationInSeconds = parameterTree.getDouble(trajectoryDurationParameterName);
    } else {
      trajectoryDurationInSeconds = trajectory4d.getTrajectoryDuration();
    }

    final ParrotServiceFactory parrotServiceFactory = TumSimServiceFactory.create(connectedNode);
    stateEstimator = getFakeStateEstimator(getGazeboStateEstimator(connectedNode), connectedNode);
    final LandService landService = parrotServiceFactory.createLandService();
    final FlyingStateService flyingStateService = parrotServiceFactory.createFlyingStateService();
    final TakeOffService takeOffService = parrotServiceFactory.createTakeOffService();
    final ResetService resetService = parrotServiceFactory.createResetService();
    final Velocity4dService velocity4dService =
        getVelocity4dService(nodeName, parameterTree, parrotServiceFactory);

    final Collection<Command> commands = new ArrayList<>();

    final Command takeOff = TumSimTakeoff.create(takeOffService, flyingStateService, resetService);
    commands.add(takeOff);

    final Command hoverFiveSecond =
        TumSimHover.create(5, RosTime.create(connectedNode), velocity4dService, stateEstimator);
    commands.add(hoverFiveSecond);

    if (parameterTree.getBoolean(nodeName + "/cp_mode")) {
      final PoseOutdatedMonitor poseOutdatedMonitor =
          PoseOutdatedMonitor.create(stateEstimator, RosTime.create(connectedNode), 0.2);
      final Command followTrajectoryWithCP =
          ParrotFollowTrajectoryWithCP.builder()
              .withStateEstimator(stateEstimator)
              .withPoseOutdatedMonitor(poseOutdatedMonitor)
              .withTrajectory(trajectory4d)
              .withControlRateInSeconds(getControlRateInSeconds(nodeName, parameterTree))
              .withTimeProvider(RosTime.create(connectedNode))
              .withPidLinearX(pidLinearX)
              .withPidLinearY(pidLinearY)
              .withPidLinearZ(pidLinearZ)
              .withPidAngularZ(pidAngularZ)
              .withVelocity4dService(velocity4dService)
              .build();
      commands.add(followTrajectoryWithCP);
    } else {
      VelocityController4d velocityController4d =
          DroneVelocityController.pidBuilder()
              .withTrajectory4d(trajectory4d)
              .withLinearXParameters(pidLinearX)
              .withLinearYParameters(pidLinearY)
              .withLinearZParameters(pidLinearZ)
              .withAngularZParameters(pidAngularZ)
              .build();

      if (parameterTree.getBoolean(nodeName + "/pid_co_filter")) {
        logger.info("Run with pid filter.");
        final double filterTimeConstant =
            parameterTree.getDouble(nodeName + "/pid_co_filter_time_constant");
        velocityController4d = PidCoFilter4d.create(velocityController4d, filterTimeConstant);
      }

      velocityController4d =
          VelocityController4dLogger.create(
              velocityController4d, trajectory4d, RosTime.create(connectedNode), "drone");

      final Command followTrajectory =
          TumSimFollowTrajectory.builder()
              .withVelocity4dService(velocity4dService)
              .withStateEstimator(stateEstimator)
              .withTimeProvider(RosTime.create(connectedNode))
              .withDurationInSeconds(trajectoryDurationInSeconds)
              .withVelocityController4d(velocityController4d)
              .withControlRateInSeconds(getControlRateInSeconds(nodeName, parameterTree))
              .build();

      final Command waitForLocalizationThenFollowTrajectory =
          WaitForLocalizationDecorator.create(stateEstimator, followTrajectory);
      commands.add(waitForLocalizationThenFollowTrajectory);
    }

    final Command land = TumSimLand.create(landService, flyingStateService);
    commands.add(land);

    final Command shutdownNode =
        new Command() {
          @Override
          public void execute() {
            connectedNode.shutdown();
          }
        };
    commands.add(shutdownNode);

    final Task flyTask = Task.create(ImmutableList.copyOf(commands), TaskType.NORMAL_TASK);
    final Task emergencyTask = createEmergencyTask(landService, flyingStateService);

    exampleFlight = ExampleFlight.create(connectedNode, flyTask, emergencyTask);
  }

  private static double getControlRateInSeconds(String nodeName, ParameterTree parameterTree) {
    return 1.0 / parameterTree.getDouble(nodeName + "/pid_control_frequency");
  }

  private static Velocity4dService getVelocity4dService(
      String nodeName, ParameterTree parameterTree, ParrotServiceFactory parrotServiceFactory) {
    final Velocity4dService velocity4dService;
    final String filterType = parameterTree.getString(nodeName + "/velocity_filter");
    if ("min".equals(filterType)) {
      velocity4dService =
          MinDiffBodyFrameVelocityFilter.create(
              parrotServiceFactory.createVelocity4dService(),
              parameterTree.getDouble(nodeName + "/min_diff"));
    } else if ("max".equals(filterType)) {
      velocity4dService =
          MaxDiffBodyFrameVelocityFilter.create(
              parrotServiceFactory.createVelocity4dService(),
              parameterTree.getDouble(nodeName + "/max_diff"));
    } else if ("cascade".equals(filterType)) {
      velocity4dService =
          CascadeBodyFrameVelocityFilter.create(
              parrotServiceFactory.createVelocity4dService(),
              parameterTree.getDouble(nodeName + "/cascade_delta"));
    } else {
      velocity4dService = parrotServiceFactory.createVelocity4dService();
    }
    return velocity4dService;
  }

  private static Task createEmergencyTask(
      LandService landService, FlyingStateService flyingStateService) {
    final Command land = TumSimLand.create(landService, flyingStateService);
    return Task.create(ImmutableList.of(land), TaskType.FIRST_ORDER_EMERGENCY);
  }

  /**
   * Creates a facade to run the drone in the Tum simulator. The drone will take off, follow a
   * provided trajectory and then land.
   *
   * @param trajectory4d the trajectory which the drone will follow
   * @param connectedNode the connected node
   * @return a facade for flying with the drone in the Tum simulator
   */
  public static TumExampleFlightFacade create(
      FiniteTrajectory4d trajectory4d, ConnectedNode connectedNode) {
    return new TumExampleFlightFacade(trajectory4d, connectedNode);
  }

  private static StateEstimator getGazeboStateEstimator(ConnectedNode connectedNode) {
    final MessagesSubscriberService<ModelStates> modelStateSubscriber =
        MessagesSubscriberService.create(
            connectedNode.<ModelStates>newSubscriber("/gazebo/model_states", ModelStates._TYPE),
            RosTime.create(connectedNode));
    return GazeboModelStateEstimator.create(
        modelStateSubscriber, MODEL_NAME, RosTime.create(connectedNode));
  }

  private static StateEstimator getFakeStateEstimator(
      StateEstimator stateEstimator, ConnectedNode connectedNode) {
    final ParameterTree parameterTree = connectedNode.getParameterTree();
    final String nodeName = connectedNode.getName().toString();

    final double localizationFrequency =
        parameterTree.getDouble(nodeName + "/localization_frequency");
    final double localizationNoiseMean =
        parameterTree.getDouble(nodeName + "/localization_noise_mean");
    final double localizationNoiseDeviation =
        parameterTree.getDouble(nodeName + "/localization_noise_deviation");
    final int localizationNoiseSeed =
        parameterTree.getInteger(nodeName + "/localization_noise_seed");
    final int numberOfAveragedPoses =
        parameterTree.getInteger(nodeName + "/localization_average_poses");

    final GaussianRandomGenerator noiseGenerator =
        new GaussianRandomGenerator(new MersenneTwister(localizationNoiseSeed));

    return FakeStateEstimatorDecorator.create(
        stateEstimator,
        localizationFrequency,
        noiseGenerator,
        localizationNoiseMean,
        localizationNoiseDeviation,
        numberOfAveragedPoses);
  }

  /** Starts flying. */
  void fly() {
    waitUntilRecevingDroneState();
    exampleFlight.fly();
  }

  private void waitUntilRecevingDroneState() {
    Optional<DroneStateStamped> droneState = stateEstimator.getCurrentState();

    // wait until we receive at lease a state of the drone before flying. It is to guarantee that
    // the drone model is properly initialized before running this code.
    while (!droneState.isPresent()) {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        logger.info("Sleep while waiting for drone state is interrupted.", e);
        Thread.currentThread().interrupt();
      }

      droneState = stateEstimator.getCurrentState();
    }
  }
}
