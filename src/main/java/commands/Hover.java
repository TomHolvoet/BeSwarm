package commands;

import time.TimeProvider;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Hover command.
 *
 * @author Hoang Tung Dinh
 */
// TODO: Refactor this class to use the optical flow sensor only
public abstract class Hover implements Command {
  private final double durationInSeconds;
  private final TimeProvider timeProvider;

  protected Hover(double durationInSeconds, TimeProvider timeProvider) {
    checkArgument(
        durationInSeconds > 0,
        String.format("Duration must be a positive value, but it is %f", durationInSeconds));
    this.durationInSeconds = durationInSeconds;
    this.timeProvider = timeProvider;
  }

  protected final double getDurationInSeconds() {
    return durationInSeconds;
  }

  protected final TimeProvider getTimeProvider() {
    return timeProvider;
  }
}
