package commands.bebopcommands;

import commands.Command;
import org.ros.time.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Simply wait (and do nothing) until the end time.
 *
 * @author Hoang Tung Dinh
 */
public final class BebopWaitUntil implements Command {

  private static final Logger logger = LoggerFactory.getLogger(BebopWaitUntil.class);

  private final TimeProvider timeProvider;
  private final double endTimeInSecs;

  private BebopWaitUntil(TimeProvider timeProvider, double endTimeInSecs) {
    this.timeProvider = timeProvider;
    this.endTimeInSecs = endTimeInSecs;
  }

  public static BebopWaitUntil create(TimeProvider timeProvider, double endTimeInSecs) {
    return new BebopWaitUntil(timeProvider, endTimeInSecs);
  }

  @Override
  public void execute() {
    while (timeProvider.getCurrentTime().toSeconds() < endTimeInSecs) {
      try {
        TimeUnit.MILLISECONDS.sleep(20);
      } catch (InterruptedException e) {
        logger.info("Sleep inside BebopWaitUntil is interrupted.", e);
      }
    }
  }
}
