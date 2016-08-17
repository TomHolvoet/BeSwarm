package commands;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FlyingStateService;
import services.ResetService;
import services.TakeOffService;
import services.rossubscribers.FlyingState;

import java.util.concurrent.TimeUnit;

/**
 * Command for taking off.
 * // TODO test me
 *
 * @author Hoang Tung Dinh
 */
public final class Takeoff implements Command {

  private static final Logger logger = LoggerFactory.getLogger(Takeoff.class);

  private final TakeOffService takeOffService;
  private final FlyingStateService flyingStateService;
  private final ResetService resetService;

  private Takeoff(
      TakeOffService takeOffService,
      FlyingStateService flyingStateService,
      ResetService resetService) {
    this.takeOffService = takeOffService;
    this.flyingStateService = flyingStateService;
    this.resetService = resetService;
  }

  /**
   * Creates an instance of the {@link Takeoff} command.
   *
   * @param takeOffService the take off service
   * @param flyingStateService the flying state service
   * @param resetService the reset service
   * @return an instance of the {@link Takeoff} command
   */
  public static Takeoff create(
      TakeOffService takeOffService,
      FlyingStateService flyingStateService,
      ResetService resetService) {
    return new Takeoff(takeOffService, flyingStateService, resetService);
  }

  @Override
  public void execute() {
    logger.debug("Execute take off command.");
    sendResetMessageAndWaitForFlyingState();
    logger.debug("The drone is in LANDED state. Start sending taking off message.");
    sendTakeOffMessages();
    waitUntilInHoveringState();
  }

  /** Waits until the drone is in HOVERING state. */
  private void waitUntilInHoveringState() {
    while (true) {
      final Optional<FlyingState> currentFlyingState = flyingStateService.getCurrentFlyingState();
      if (currentFlyingState.isPresent() && currentFlyingState.get() == FlyingState.HOVERING) {
        return;
      }
      try {
        TimeUnit.MILLISECONDS.sleep(50);
      } catch (InterruptedException e) {
        logger.debug(
            "Sleep while waiting for the drone in hovering state take off execution is interrupted.",
            e);
      }
    }
  }

  /** Sends take off messages until the drone state is TAKING_OFF. */
  private void sendTakeOffMessages() {
    while (true) {
      takeOffService.sendTakingOffMessage();

      try {
        TimeUnit.MILLISECONDS.sleep(50);
      } catch (InterruptedException e) {
        logger.debug(
            "Sleep while waiting for flying state in take off execution is interrupted.", e);
      }

      final Optional<FlyingState> currentFlyingState = flyingStateService.getCurrentFlyingState();
      if (currentFlyingState.isPresent() && currentFlyingState.get() == FlyingState.TAKING_OFF) {
        break;
      }
    }
  }

  /**
   * Sends the reset message to the drone until the state of the drone is {@link
   * FlyingState#LANDED}.
   */
  private void sendResetMessageAndWaitForFlyingState() {
    while (true) {
      // if there is no flying state received yet, then send the reset message
      if (!flyingStateService.getCurrentFlyingState().isPresent()) {
        resetService.sendResetMessage();
      }

      try {
        TimeUnit.MILLISECONDS.sleep(50);
      } catch (InterruptedException e) {
        logger.debug(
            "Sleep while waiting for flying state in  sendResetMessageAndWaitForFlyingState in "
                + "take off execution is interrupted.",
            e);
        Thread.currentThread().interrupt();
      }

      final Optional<FlyingState> currentFlyingState = flyingStateService.getCurrentFlyingState();
      if (currentFlyingState.isPresent() && currentFlyingState.get() == FlyingState.LANDED) {
        // if the drone is in LANDED state then return
        return;
      }
    }
  }
}
