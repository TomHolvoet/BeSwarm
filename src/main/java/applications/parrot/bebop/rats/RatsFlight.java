package applications.parrot.bebop.rats;

import applications.ExampleFlight;
import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import commands.Command;
import commands.bebopcommands.BebopFollowTrajectory;
import commands.bebopcommands.BebopHover;
import commands.bebopcommands.BebopHoverUntil;
import commands.bebopcommands.BebopLand;
import commands.bebopcommands.BebopTakeOff;
import commands.bebopcommands.BebopWaitUntil;
import control.DroneVelocityController;
import control.FiniteTrajectory4d;
import control.VelocityController4d;
import control.VelocityController4dLogger;
import geometry_msgs.PoseStamped;
import localization.BebopStateEstimatorWithPoseStampedAndOdom;
import localization.StateEstimator;
import nav_msgs.Odometry;
import org.ros.node.ConnectedNode;
import org.ros.time.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FlyingStateService;
import services.LandService;
import services.ResetService;
import services.TakeOffService;
import services.Velocity4dService;
import services.parrot.BebopServiceFactory;
import services.rossubscribers.MessagesSubscriberService;
import std_msgs.Time;
import taskexecutor.Task;
import taskexecutor.TaskType;
import time.RosTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * A single drone flight for a rats show. The drone first waits until it receives the synchronized
 * start time. Then, it will use that start time to construct and execute a flight. The flight is as
 * follows: wait until take off time -> take off -> hover until start flying time -> follow
 * trajectory -> hover 3 seconds -> land.
 *
 * @author Hoang Tung Dinh
 */
public final class RatsFlight {
  private static final Logger logger = LoggerFactory.getLogger(RatsFlight.class);
  private final FiniteTrajectory4d trajectory;
  private final ConnectedNode connectedNode;

  private RatsFlight(FiniteTrajectory4d trajectory, ConnectedNode connectedNode) {
    this.trajectory = trajectory;
    this.connectedNode = connectedNode;
  }

  public static RatsFlight create(FiniteTrajectory4d trajectory, ConnectedNode connectedNode) {
    return new RatsFlight(trajectory, connectedNode);
  }

  public void startRatsShow() {
    final RatsParameter ratsParameter =
        RatsParameter.createFromParameterTree(connectedNode.getParameterTree());
    final double syncStartTimeInSecs =
        waitAndGetSynchronizedSystemTimeInSecs(ratsParameter.timeSyncTopic());
    final ExampleFlight flight = constructFlight(ratsParameter, syncStartTimeInSecs);
    flight.fly();
  }

  private ExampleFlight constructFlight(RatsParameter ratsParameter, double syncStartTimeInSecs) {
    final BebopServices bebopServices = BebopServices.create(connectedNode, ratsParameter);
    final Task flyTask = createFlyTask(bebopServices, ratsParameter, syncStartTimeInSecs);
    final Task emergencyTask = createEmergencyTask(bebopServices);
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

  private static Task createEmergencyTask(BebopServices bebopServices) {
    final Command land =
        BebopLand.create(bebopServices.landService(), bebopServices.flyingStateService());
    return Task.create(ImmutableList.of(land), TaskType.FIRST_ORDER_EMERGENCY);
  }

  private Task createFlyTask(
      BebopServices bebopServices, RatsParameter ratsParameter, double syncStartTimeInSecs) {
    final TimeProvider timeProvider = RosTime.create(connectedNode);

    final Collection<Command> commands = new ArrayList<>();

    commands.add(createWaitUntilCommand(ratsParameter, syncStartTimeInSecs, timeProvider));
    commands.add(createTakeOffCommand(bebopServices));
    commands.add(
        createHoverUntilCommand(bebopServices, ratsParameter, syncStartTimeInSecs, timeProvider));
    commands.add(createFollowTrajectoryCommand(bebopServices, ratsParameter));
    commands.add(createHoverThreeSecondsCommand(bebopServices));
    commands.add(BebopLand.create(bebopServices.landService(), bebopServices.flyingStateService()));

    return Task.create(ImmutableList.copyOf(commands), TaskType.NORMAL_TASK);
  }

  private Command createHoverThreeSecondsCommand(BebopServices bebopServices) {
    return BebopHover.create(
        3,
        RosTime.create(connectedNode),
        bebopServices.velocity4dService(),
        bebopServices.stateEstimator());
  }

  private Command createFollowTrajectoryCommand(
      BebopServices bebopServices, RatsParameter ratsParameter) {
    VelocityController4d velocityController4d =
        DroneVelocityController.pidBuilder()
            .withTrajectory4d(trajectory)
            .withLinearXParameters(ratsParameter.pidLinearX())
            .withLinearYParameters(ratsParameter.pidLinearY())
            .withLinearZParameters(ratsParameter.pidLinearZ())
            .withAngularZParameters(ratsParameter.pidAngularZ())
            .build();

    velocityController4d =
        VelocityController4dLogger.create(
            velocityController4d,
            trajectory,
            RosTime.create(connectedNode),
            ratsParameter.loggerName());

    return BebopFollowTrajectory.builder()
        .withVelocity4dService(bebopServices.velocity4dService())
        .withStateEstimator(bebopServices.stateEstimator())
        .withTimeProvider(RosTime.create(connectedNode))
        .withDurationInSeconds(trajectory.getTrajectoryDuration())
        .withVelocityController4d(velocityController4d)
        .withControlRateInSeconds(1.0 / ratsParameter.controlFrequencyInHz())
        .build();
  }

  private static Command createHoverUntilCommand(
      BebopServices bebopServices,
      RatsParameter ratsParameter,
      double syncStartTimeInSecs,
      TimeProvider timeProvider) {
    final double realStartFlyingTime =
        syncStartTimeInSecs + ratsParameter.absoluteStartFlyingTimeInSecs();
    return BebopHoverUntil.create(
        timeProvider, realStartFlyingTime, bebopServices.velocity4dService());
  }

  private static Command createTakeOffCommand(BebopServices bebopServices) {
    return BebopTakeOff.create(
        bebopServices.takeOffService(),
        bebopServices.flyingStateService(),
        bebopServices.resetService());
  }

  private static Command createWaitUntilCommand(
      RatsParameter ratsParameter, double syncStartTimeInSecs, TimeProvider timeProvider) {
    final double realTakeOffTime = syncStartTimeInSecs + ratsParameter.absoluteTakeOffTimeInSecs();
    return BebopWaitUntil.create(timeProvider, realTakeOffTime);
  }

  private double waitAndGetSynchronizedSystemTimeInSecs(String timeSyncTopic) {
    final MessagesSubscriberService<Time> startTimeSubscriber =
        createStartTimeSubscriber(timeSyncTopic);

    Optional<Time> timeMsgs = startTimeSubscriber.getMostRecentMessage();

    while (!timeMsgs.isPresent()) {
      try {
        TimeUnit.MILLISECONDS.sleep(20);
      } catch (InterruptedException e) {
        logger.info("Sleep during waiting for starting time.", e);
      }

      timeMsgs = startTimeSubscriber.getMostRecentMessage();
    }

    return timeMsgs.get().getData().toSeconds();
  }

  private MessagesSubscriberService<Time> createStartTimeSubscriber(String timeSyncTopic) {
    logger.info("Subscribe to {} for getting start time", timeSyncTopic);
    return MessagesSubscriberService.create(
        connectedNode.<Time>newSubscriber(timeSyncTopic, Time._TYPE),
        RosTime.create(connectedNode));
  }

  @AutoValue
  abstract static class BebopServices {
    BebopServices() {}

    static BebopServices create(ConnectedNode connectedNode, RatsParameter ratsParameter) {
      final BebopServiceFactory bebopServiceFactory =
          BebopServiceFactory.create(connectedNode, ratsParameter.droneName());
      final LandService landService = bebopServiceFactory.createLandService();
      final FlyingStateService flyingStateService = bebopServiceFactory.createFlyingStateService();
      final Velocity4dService velocity4dService = bebopServiceFactory.createVelocity4dService();
      final TakeOffService takeOffService = bebopServiceFactory.createTakeOffService();
      final ResetService resetService = bebopServiceFactory.createResetService();
      final StateEstimator stateEstimator =
          BebopStateEstimatorWithPoseStampedAndOdom.create(
              createPoseSubscriber(connectedNode, ratsParameter.poseTopic()),
              createOdometrySubscriber(connectedNode, ratsParameter.droneName()));
      return new AutoValue_RatsFlight_BebopServices(
          landService,
          flyingStateService,
          velocity4dService,
          takeOffService,
          resetService,
          stateEstimator);
    }

    private static MessagesSubscriberService<PoseStamped> createPoseSubscriber(
        ConnectedNode connectedNode, String poseTopic) {
      logger.info("Subscribed to {} for getting pose.", poseTopic);
      return MessagesSubscriberService.create(
          connectedNode.<PoseStamped>newSubscriber(poseTopic, PoseStamped._TYPE),
          RosTime.create(connectedNode));
    }

    private static MessagesSubscriberService<Odometry> createOdometrySubscriber(
        ConnectedNode connectedNode, String droneName) {
      final String odometryTopic = "/" + droneName + "/odom";
      logger.info("Subscribed to {} for getting odometry", odometryTopic);
      return MessagesSubscriberService.create(
          connectedNode.<Odometry>newSubscriber(odometryTopic, Odometry._TYPE),
          RosTime.create(connectedNode));
    }

    abstract LandService landService();

    abstract FlyingStateService flyingStateService();

    abstract Velocity4dService velocity4dService();

    abstract TakeOffService takeOffService();

    abstract ResetService resetService();

    abstract StateEstimator stateEstimator();
  }
}
