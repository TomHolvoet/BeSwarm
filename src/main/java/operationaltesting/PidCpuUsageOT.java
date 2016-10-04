package operationaltesting;

import applications.trajectory.Trajectories;
import applications.trajectory.geom.point.Point3D;
import com.google.common.base.Optional;
import commands.Command;
import commands.bebopcommands.BebopFollowTrajectory;
import control.DefaultPidParameters;
import control.DroneVelocityController;
import control.Trajectory4d;
import control.VelocityController4d;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import localization.StateEstimator;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.time.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Velocity4dService;
import services.parrot.BebopServiceFactory;
import time.RosTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Operational test for cpu usage of multiple pid controllers.
 *
 * @author Hoang Tung Dinh
 */
public final class PidCpuUsageOT extends AbstractNodeMain {

  private static final Logger logger = LoggerFactory.getLogger(PidCpuUsageOT.class);

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("PidCpuUsageOT");
  }

  @Override
  public void onStart(ConnectedNode connectedNode) {
    final int numberOfControllers =
        connectedNode
            .getParameterTree()
            .getInteger(connectedNode.getName().toString() + "/number_of_controllers");
    final double controlFrequency =
        connectedNode
            .getParameterTree()
            .getDouble(connectedNode.getName().toString() + "/control_frequency");
    final double controlRateInSeconds = 1.0 / controlFrequency;
    final double durationInSeconds =
        connectedNode
            .getParameterTree()
            .getDouble(connectedNode.getName().toString() + "/duration_in_seconds");

    final Collection<Command> followTrajectoryCommandList = new ArrayList<>();
    final TimeProvider timeProvider = RosTime.create(connectedNode);

    for (int i = 0; i < numberOfControllers; i++) {
      followTrajectoryCommandList.add(
          createFollowTrajectoryCommand(
              timeProvider,
              "controller_" + i,
              connectedNode,
              durationInSeconds,
              controlRateInSeconds));
    }

    for (final Command command : followTrajectoryCommandList) {
      Executors.newSingleThreadExecutor()
          .submit(
              new Runnable() {
                @Override
                public void run() {
                  command.execute();
                }
              });
    }

    try {
      TimeUnit.SECONDS.sleep((long) (durationInSeconds + 5));
    } catch (InterruptedException e) {
      logger.info("OT is interrupted.", e);
    }
  }

  private static Command createFollowTrajectoryCommand(
      TimeProvider timeProvider,
      String controllerName,
      ConnectedNode connectedNode,
      double duration,
      double controlRateInSeconds) {
    final Trajectory4d trajectory =
        Trajectories.circleTrajectoryBuilder()
            .setLocation(Point3D.create(1.5, -2.5, 1.5))
            .setRadius(1.5)
            .setFrequency(0.1)
            .fixYawAt(-Math.PI / 2)
            .build();

    VelocityController4d velocityController4d =
        DroneVelocityController.pidBuilder()
            .withTrajectory4d(trajectory)
            .withLinearXParameters(DefaultPidParameters.LINEAR_X.getParameters())
            .withLinearYParameters(DefaultPidParameters.LINEAR_Y.getParameters())
            .withLinearZParameters(DefaultPidParameters.LINEAR_Z.getParameters())
            .withAngularZParameters(DefaultPidParameters.ANGULAR_Z.getParameters())
            .build();

    velocityController4d =
        ControllerTimeLogger.create(timeProvider, velocityController4d, controllerName);

    final BebopServiceFactory bebopServiceFactory =
        BebopServiceFactory.create(connectedNode, controllerName);
    final Velocity4dService velocity4dService = bebopServiceFactory.createVelocity4dService();

    return BebopFollowTrajectory.builder()
        .withVelocity4dService(velocity4dService)
        .withStateEstimator(FakeStateEstimator.create(timeProvider))
        .withTimeProvider(RosTime.create(connectedNode))
        .withDurationInSeconds(duration)
        .withVelocityController4d(velocityController4d)
        .withControlRateInSeconds(controlRateInSeconds)
        .build();
  }

  private static final class ControllerTimeLogger implements VelocityController4d {

    private final TimeProvider timeProvider;
    private final VelocityController4d velocityController;
    private final String controllerName;
    private long lastTimeInNanoSecs = -1;

    private ControllerTimeLogger(
        TimeProvider timeProvider, VelocityController4d velocityController, String controllerName) {
      this.timeProvider = timeProvider;
      this.velocityController = velocityController;
      this.controllerName = controllerName;
    }

    public static ControllerTimeLogger create(
        TimeProvider timeProvider, VelocityController4d velocityController, String controllerName) {
      return new ControllerTimeLogger(timeProvider, velocityController, controllerName);
    }

    @Override
    public InertialFrameVelocity computeNextResponse(
        Pose currentPose, InertialFrameVelocity currentVelocity, double currentTimeInSeconds) {
      final InertialFrameVelocity nextVelocity =
          velocityController.computeNextResponse(
              currentPose, currentVelocity, currentTimeInSeconds);
      final long currentTimeInNanoSecs = timeProvider.getCurrentTime().totalNsecs();
      if (lastTimeInNanoSecs == -1) {
        lastTimeInNanoSecs = currentTimeInNanoSecs;
      } else {
        final long durationInNanoSecs = currentTimeInNanoSecs - lastTimeInNanoSecs;
        logger.trace("{} {}", controllerName, durationInNanoSecs);
        lastTimeInNanoSecs = currentTimeInNanoSecs;
      }
      return nextVelocity;
    }
  }

  private static final class FakeStateEstimator implements StateEstimator {

    private final TimeProvider timeProvider;

    private FakeStateEstimator(TimeProvider timeProvider) {
      this.timeProvider = timeProvider;
    }

    public static FakeStateEstimator create(TimeProvider timeProvider) {
      return new FakeStateEstimator(timeProvider);
    }

    @Override
    public Optional<DroneStateStamped> getCurrentState() {
      return Optional.of(
          DroneStateStamped.create(
              Pose.createZeroPose(),
              Velocity.createZeroVelocity(),
              timeProvider.getCurrentTime().toSeconds()));
    }
  }
}
