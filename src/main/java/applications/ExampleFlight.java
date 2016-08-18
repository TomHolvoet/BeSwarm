package applications;

import com.google.common.collect.ImmutableList;
import commands.Command;
import commands.Land;
import keyboard.Key;
import org.ros.node.ConnectedNode;
import sensor_msgs.Joy;
import services.FlyingStateService;
import services.LandService;
import services.rossubscribers.MessagesSubscriberService;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;
import taskexecutor.interruptors.KeyboardEmergency;
import taskexecutor.interruptors.XBox360ControllerEmergency;
import time.RosTime;

/**
 * This class illustrates an example flight. The drone will execute a fly {@link Task} while
 * listening to xbox emergency button and keyboard emergency button.
 *
 * @author Hoang Tung Dinh
 */
public final class ExampleFlight {

  private final LandService landService;
  private final FlyingStateService flyingStateService;
  private final ConnectedNode connectedNode;
  private final Task flyTask;

  private ExampleFlight(
      LandService landService,
      FlyingStateService flyingStateService,
      ConnectedNode connectedNode,
      Task flyTask) {
    this.landService = landService;
    this.flyingStateService = flyingStateService;
    this.connectedNode = connectedNode;
    this.flyTask = flyTask;
  }

  /**
   * Creates an instance of {@link ExampleFlight}.
   *
   * @param landService the flying service
   * @param flyingStateService the flying state service
   * @param connectedNode the connected node
   * @param flyTask the fly task
   * @return an instance of {@link ExampleFlight}
   */
  public static ExampleFlight create(
      LandService landService,
      FlyingStateService flyingStateService,
      ConnectedNode connectedNode,
      Task flyTask) {
    return new ExampleFlight(landService, flyingStateService, connectedNode, flyTask);
  }

  /** Starts flying. */
  public void fly() {
    // task to execute in case of emergency
    final Task emergencyTask = createEmergencyTask();
    final KeyboardEmergency keyboardEmergencyNotifier =
        createKeyboardEmergencyNotifier(emergencyTask);
    final XBox360ControllerEmergency xBox360ControllerEmergency =
        createXBox360ControllerEmergency(emergencyTask);

    final TaskExecutor taskExecutor = TaskExecutorService.create();
    keyboardEmergencyNotifier.registerTaskExecutor(taskExecutor);
    xBox360ControllerEmergency.registerTaskExecutor(taskExecutor);

    // start fly task
    taskExecutor.submitTask(flyTask);
  }

  private Task createEmergencyTask() {
    final Command land = Land.create(landService, flyingStateService);
    return Task.create(ImmutableList.of(land), TaskType.FIRST_ORDER_EMERGENCY);
  }

  private KeyboardEmergency createKeyboardEmergencyNotifier(Task emergencyTask) {
    final MessagesSubscriberService<Key> keyboardSubscriber =
        MessagesSubscriberService.create(
            connectedNode.<Key>newSubscriber("/keyboard/keydown", Key._TYPE),
            RosTime.create(connectedNode));
    final KeyboardEmergency keyboardEmergency = KeyboardEmergency.create(emergencyTask);
    keyboardSubscriber.registerMessageObserver(keyboardEmergency);
    return keyboardEmergency;
  }

  private XBox360ControllerEmergency createXBox360ControllerEmergency(Task emergencyTask) {
    final MessagesSubscriberService<Joy> joystickSubscriber =
        MessagesSubscriberService.create(
            connectedNode.<Joy>newSubscriber("/bebop/joy", Joy._TYPE),
            RosTime.create(connectedNode));
    final XBox360ControllerEmergency xBox360ControllerEmergency =
        XBox360ControllerEmergency.create(emergencyTask);
    joystickSubscriber.registerMessageObserver(xBox360ControllerEmergency);
    return xBox360ControllerEmergency;
  }
}
