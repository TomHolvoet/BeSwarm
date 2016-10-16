package commands;

import org.ros.message.Time;
import org.ros.time.TimeProvider;

import java.util.concurrent.TimeUnit;

/** @author Hoang Tung Dinh */
public final class WaitUntilStartTimeDecorator implements Command {
  private final Command command;
  private final Time startTime;
  private final TimeProvider timeProvider;

  private WaitUntilStartTimeDecorator(Command command, Time startTime, TimeProvider timeProvider) {
    this.command = command;
    this.startTime = startTime;
    this.timeProvider = timeProvider;
  }

  public static WaitUntilStartTimeDecorator create(
      Command command, Time startTime, TimeProvider timeProvider) {
    return new WaitUntilStartTimeDecorator(command, startTime, timeProvider);
  }

  @Override
  public void execute() {
    Time currentTime = timeProvider.getCurrentTime();
    while (currentTime.totalNsecs() < startTime.totalNsecs()) {
      try {
        TimeUnit.MILLISECONDS.sleep(20);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }

      currentTime = timeProvider.getCurrentTime();
    }

    command.execute();
  }
}
