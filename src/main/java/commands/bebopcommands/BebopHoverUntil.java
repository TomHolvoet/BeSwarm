package commands.bebopcommands;

import commands.Command;
import control.dto.BodyFrameVelocity;
import control.dto.Velocity;
import org.ros.time.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Velocity4dService;

import java.util.concurrent.TimeUnit;

/**
 * Hover until a given time.
 *
 * @author Hoang Tung Dinh
 */
public final class BebopHoverUntil implements Command {

  private static final Logger logger = LoggerFactory.getLogger(BebopHoverUntil.class);

  private final TimeProvider timeProvider;
  private final double endTimeInSeconds;
  private final Velocity4dService velocity4dService;

  private BebopHoverUntil(
      TimeProvider timeProvider, double endTimeInSeconds, Velocity4dService velocity4dService) {
    this.timeProvider = timeProvider;
    this.endTimeInSeconds = endTimeInSeconds;
    this.velocity4dService = velocity4dService;
  }

  public static BebopHoverUntil create(
      TimeProvider timeProvider, double endTimeInSeconds, Velocity4dService velocity4dService) {
    return new BebopHoverUntil(timeProvider, endTimeInSeconds, velocity4dService);
  }

  @Override
  public void execute() {
    logger.info("Start hover command until second {}", endTimeInSeconds);
    final BodyFrameVelocity bodyFrameVelocity = Velocity.createZeroVelocity();
    velocity4dService.sendBodyFrameVelocity(bodyFrameVelocity);

    while (timeProvider.getCurrentTime().toSeconds() < endTimeInSeconds) {
      try {
        TimeUnit.MILLISECONDS.sleep(20);
      } catch (InterruptedException e) {
        logger.info("Sleep in BebopHoverUntil is interrupted.", e);
      }
    }
  }
}
