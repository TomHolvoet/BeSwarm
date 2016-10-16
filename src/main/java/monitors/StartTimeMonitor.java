package monitors;

import org.ros.message.Time;
import org.ros.time.TimeProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** @author Hoang Tung Dinh */
public final class StartTimeMonitor {

  private final AtomicReference<Status> status;

  private StartTimeMonitor(Time startTime, TimeProvider timeProvider) {
    this.status = new AtomicReference<>(Status.NOT_YET_STARTED);

    Executors.newSingleThreadExecutor()
        .submit(
            () -> {
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

              status.set(Status.STARTED);
            });
  }

  public static StartTimeMonitor create(Time startTime, TimeProvider timeProvider) {
    return new StartTimeMonitor(startTime, timeProvider);
  }

  public Status getStatus() {
    return status.get();
  }

  public enum Status {
    STARTED,
    NOT_YET_STARTED
  }
}
