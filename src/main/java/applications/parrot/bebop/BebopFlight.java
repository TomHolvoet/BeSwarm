package applications.parrot.bebop;

import applications.ExampleFlight;
import com.google.common.collect.ImmutableList;
import commands.Command;
import commands.WaitForLocalizationDecorator;
import commands.bebopcommands.BebopFollowTrajectory;
import commands.bebopcommands.BebopHover;
import commands.bebopcommands.BebopLand;
import commands.bebopcommands.BebopTakeOff;
import control.FiniteTrajectory4d;
import control.DroneVelocityController;
import control.PidParameters;
import control.VelocityController4d;
import control.VelocityController4dLogger;
import localization.BebopStateEstimatorWithPoseStampedAndOdom;
import localization.StateEstimator;
import geometry_msgs.PoseStamped;
import nav_msgs.Odometry;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FlyingStateService;
import services.LandService;
import services.MinDiffBodyFrameVelocityFilter;
import services.ResetService;
import services.TakeOffService;
import services.Velocity4dService;
import services.parrot.BebopServiceFactory;
import services.parrot.ParrotServiceFactory;
import services.rossubscribers.MessagesSubscriberService;
import taskexecutor.Task;
import taskexecutor.TaskType;
import time.RosTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/** @author Hoang Tung Dinh */
final class BebopFlight {
  private static final Logger logger = LoggerFactory.getLogger(BebopFlight.class);
  private final ExampleFlight exampleFlight;

  private BebopFlight(
      String droneName,
      FiniteTrajectory4d trajectory,
      ConnectedNode connectedNode,
      String poseTopic) {
    exampleFlight = constructFlight(connectedNode, droneName, trajectory, poseTopic);
  }

  public static BebopFlight create(
      String droneName,
      FiniteTrajectory4d trajectory,
      ConnectedNode connectedNode,
      String poseTopic) {
    return new BebopFlight(droneName, trajectory, connectedNode, poseTopic);
  }

  private static PidParameters getPidParameters(
      ConnectedNode connectedNode, String argKp, String argKd, String argKi) {
    final double pidLinearXKp = connectedNode.getParameterTree().getDouble(argKp);
    final double pidLinearXKd = connectedNode.getParameterTree().getDouble(argKd);
    final double pidLinearXKi = connectedNode.getParameterTree().getDouble(argKi);
    return PidParameters.builder()
        .setKp(pidLinearXKp)
        .setKd(pidLinearXKd)
        .setKi(pidLinearXKi)
        .build();
  }

  private static MessagesSubscriberService<PoseStamped> getPoseSubscriber(
      ConnectedNode connectedNode, String poseTopic) {
    logger.info("Subscribed to {} for getting pose.", poseTopic);
    return MessagesSubscriberService.create(
        connectedNode.<PoseStamped>newSubscriber(poseTopic, PoseStamped._TYPE),
        RosTime.create(connectedNode));
  }

  private static MessagesSubscriberService<Odometry> getOdometrySubscriber(
      ConnectedNode connectedNode, String droneName) {
    final String odometryTopic = "/" + droneName + "/odom";
    logger.info("Subscribed to {} for getting odometry", odometryTopic);
    return MessagesSubscriberService.create(
        connectedNode.<Odometry>newSubscriber(odometryTopic, Odometry._TYPE),
        RosTime.create(connectedNode));
  }

  private static ExampleFlight constructFlight(
      ConnectedNode connectedNode,
      String droneName,
      FiniteTrajectory4d trajectory,
      String poseTopic) {
    final PidParameters pidLinearX =
        getPidParameters(
            connectedNode,
            "beswarm/pid_linear_x_kp",
            "beswarm/pid_linear_x_kd",
            "beswarm/pid_linear_x_ki");
    final PidParameters pidLinearY =
        getPidParameters(
            connectedNode,
            "beswarm/pid_linear_y_kp",
            "beswarm/pid_linear_y_kd",
            "beswarm/pid_linear_y_ki");
    final PidParameters pidLinearZ =
        getPidParameters(
            connectedNode,
            "beswarm/pid_linear_z_kp",
            "beswarm/pid_linear_z_kd",
            "beswarm/pid_linear_z_ki");
    final PidParameters pidAngularZ =
        getPidParameters(
            connectedNode,
            "beswarm/pid_angular_z_kp",
            "beswarm/pid_angular_z_kd",
            "beswarm/pid_angular_z_ki");

    final ParrotServiceFactory parrotServiceFactory =
        BebopServiceFactory.create(connectedNode, droneName);
    final LandService landService = parrotServiceFactory.createLandService();
    final FlyingStateService flyingStateService = parrotServiceFactory.createFlyingStateService();
    final Velocity4dService velocity4dService =
        MinDiffBodyFrameVelocityFilter.create(
            parrotServiceFactory.createVelocity4dService(), 0.000015);
    final TakeOffService takeOffService = parrotServiceFactory.createTakeOffService();
    final ResetService resetService = parrotServiceFactory.createResetService();
    final StateEstimator stateEstimator =
        BebopStateEstimatorWithPoseStampedAndOdom.create(
            getPoseSubscriber(connectedNode, poseTopic),
            getOdometrySubscriber(connectedNode, droneName));
    final Task flyTask =
        createFlyTask(
            connectedNode,
            pidLinearX,
            pidLinearY,
            pidLinearZ,
            pidAngularZ,
            landService,
            flyingStateService,
            velocity4dService,
            takeOffService,
            resetService,
            stateEstimator,
            trajectory,
            droneName);

    final Task emergencyTask = createEmergencyTask(landService, flyingStateService);

    final ExampleFlight exampleFlight = ExampleFlight.create(connectedNode, flyTask, emergencyTask);

    // without this code, the take off message cannot be sent properly (I don't understand why).
    try {
      TimeUnit.SECONDS.sleep(3);
    } catch (InterruptedException e) {
      logger.info("Warm up time is interrupted.", e);
      Thread.currentThread().interrupt();
    }

    return exampleFlight;
  }

  public void startFlying() {
    exampleFlight.fly();
  }

  private static Task createEmergencyTask(
      LandService landService, FlyingStateService flyingStateService) {
    final Command land = BebopLand.create(landService, flyingStateService);
    return Task.create(ImmutableList.of(land), TaskType.FIRST_ORDER_EMERGENCY);
  }

  private static Task createFlyTask(
      ConnectedNode connectedNode,
      PidParameters pidLinearX,
      PidParameters pidLinearY,
      PidParameters pidLinearZ,
      PidParameters pidAngularZ,
      LandService landService,
      FlyingStateService flyingStateService,
      Velocity4dService velocity4dService,
      TakeOffService takeOffService,
      ResetService resetService,
      StateEstimator stateEstimator,
      FiniteTrajectory4d trajectory,
      String droneName) {

    final Collection<Command> commands = new ArrayList<>();

    final Command takeOff = BebopTakeOff.create(takeOffService, flyingStateService, resetService);
    commands.add(takeOff);

    final Command hoverFiveSecond =
        BebopHover.create(5, RosTime.create(connectedNode), velocity4dService, stateEstimator);
    commands.add(hoverFiveSecond);

    VelocityController4d velocityController4d =
        DroneVelocityController.pidBuilder()
            .withTrajectory4d(trajectory)
            .withLinearXParameters(pidLinearX)
            .withLinearYParameters(pidLinearY)
            .withLinearZParameters(pidLinearZ)
            .withAngularZParameters(pidAngularZ)
            .build();

    velocityController4d =
        VelocityController4dLogger.create(
            velocityController4d, trajectory, RosTime.create(connectedNode), droneName);

    final Command followTrajectory =
        BebopFollowTrajectory.builder()
            .withVelocity4dService(velocity4dService)
            .withStateEstimator(stateEstimator)
            .withTimeProvider(RosTime.create(connectedNode))
            .withDurationInSeconds(trajectory.getTrajectoryDuration())
            .withVelocityController4d(velocityController4d)
            .withControlRateInSeconds(0.01)
            .build();

    final Command waitForLocalizationThenFollowTrajectory =
        WaitForLocalizationDecorator.create(stateEstimator, followTrajectory);

    commands.add(waitForLocalizationThenFollowTrajectory);

    final Command hoverThreeSeconds =
        BebopHover.create(3, RosTime.create(connectedNode), velocity4dService, stateEstimator);
    commands.add(hoverThreeSeconds);

    final Command land = BebopLand.create(landService, flyingStateService);
    commands.add(land);

    return Task.create(ImmutableList.copyOf(commands), TaskType.NORMAL_TASK);
  }
}
