package operationaltesting;

import com.google.common.collect.ImmutableList;
import commands.Command;
import commands.Land;
import commands.Takeoff;
import commands.schedulers.PeriodicTaskRunner;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sensor_msgs.Joy;
import services.FlyingStateService;
import services.LandService;
import services.TakeOffService;
import services.Velocity4dService;
import services.parrot.BebopServiceFactory;
import services.parrot.ParrotServiceFactory;
import services.rossubscribers.MessagesSubscriberService;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;
import taskexecutor.interruptors.XBox360ControllerEmergency;

import java.util.concurrent.TimeUnit;

/**
 * A runner for testing the xbox emergency button when flying with bebop. The runner commands the
 * drone to take off, then waits for five seconds, then starts sending zero velocity for each 50ms.
 * After five seconds, the user presses the emergency land button. The drone is expected to land
 * after that.
 *
 * @author Hoang Tung Dinh
 */
public final class EmergencyButtonOT extends AbstractNodeMain {

  private static final Logger logger = LoggerFactory.getLogger(EmergencyButtonOT.class);
  private static final String DRONE_NAME = "bebop";

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("EmergencyButtonOT");
  }

  @Override
  public void onStart(final ConnectedNode connectedNode) {
    final String controllerStatus =
        connectedNode.getParameterTree().getString("/beswarm/ot/xboxcontroller");

    final TaskExecutor taskExecutor = TaskExecutorService.create();

    final ParrotServiceFactory parrotServiceFactory =
        BebopServiceFactory.create(connectedNode, DRONE_NAME);

    if ("enable".equals(controllerStatus)) {
      final XBox360ControllerEmergency xBox360ControllerEmergency =
          createXBox360ControllerEmergencyNotifier(connectedNode, parrotServiceFactory);
      xBox360ControllerEmergency.registerTaskExecutor(taskExecutor);
    }

    try {
      TimeUnit.SECONDS.sleep(3);
    } catch (InterruptedException e) {
      logger.info("Warm up time is interrupted.", e);
      Thread.currentThread().interrupt();
    }

    // start flying
    final Task flyTask = createFlyTask(parrotServiceFactory);
    taskExecutor.submitTask(flyTask);
  }

  private static Task createFlyTask(ParrotServiceFactory parrotServiceFactory) {
    final TakeOffService takeOffService = parrotServiceFactory.createTakeOffService();
    final Command takeOff = Takeoff.create(takeOffService);

    final Velocity4dService velocity4dService = parrotServiceFactory.createVelocity4dService();
    final Command sendZeroVelocity =
        new Command() {
          @Override
          public void execute() {
            logger.debug("Executing SendZeroVelocity command.");
            final Runnable sendZeroVelocityTask =
                new Runnable() {
                  @Override
                  public void run() {
                    final InertialFrameVelocity inertialFrameVelocity =
                        Velocity.createZeroVelocity();
                    final Pose pose = Pose.createZeroPose();
                    velocity4dService.sendVelocity4dMessage(inertialFrameVelocity, pose);
                  }
                };

            PeriodicTaskRunner.run(sendZeroVelocityTask, 0.05, 120);
          }
        };

    return Task.create(ImmutableList.of(takeOff, sendZeroVelocity), TaskType.NORMAL_TASK);
  }

  private static XBox360ControllerEmergency createXBox360ControllerEmergencyNotifier(
      ConnectedNode connectedNode, ParrotServiceFactory bebopServiceFactory) {
    final Task emergencyLandingTask = createEmergencyLandingTask(bebopServiceFactory);

    final MessagesSubscriberService<Joy> joystickSubscriber =
        MessagesSubscriberService.create(connectedNode.<Joy>newSubscriber("/bebop/joy", Joy._TYPE));
    final XBox360ControllerEmergency xBox360ControllerEmergency =
        XBox360ControllerEmergency.create(emergencyLandingTask);
    joystickSubscriber.registerMessageObserver(xBox360ControllerEmergency);
    return xBox360ControllerEmergency;
  }

  private static Task createEmergencyLandingTask(ParrotServiceFactory parrotServiceFactory) {
    final LandService landService = parrotServiceFactory.createLandService();
    final FlyingStateService flyingStateService = parrotServiceFactory.createFlyingStateService();
    final Command land = Land.create(landService, flyingStateService);
    return Task.create(ImmutableList.of(land), TaskType.FIRST_ORDER_EMERGENCY);
  }
}
