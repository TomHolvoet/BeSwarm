package monitors;

import com.google.common.base.Optional;
import control.dto.DroneStateStamped;
import localization.StateEstimator;
import org.ros.time.TimeProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** @author Hoang Tung Dinh */
public final class PoseOutdatedMonitor {
  private final StateEstimator stateEstimator;
  private final TimeProvider timeProvider;
  private final double expiredTimeInSecs;

  private final AtomicReference<PoseStatus> poseStatus = new AtomicReference<>(PoseStatus.OUTDATED);

  private PoseOutdatedMonitor(
      final StateEstimator stateEstimator,
      final TimeProvider timeProvider,
      final double expiredTimeInSecs) {
    this.stateEstimator = stateEstimator;
    this.timeProvider = timeProvider;
    this.expiredTimeInSecs = expiredTimeInSecs;

    Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(new Probe(), 0, 50, TimeUnit.MILLISECONDS);
  }

  public static PoseOutdatedMonitor create(
      StateEstimator stateEstimator, TimeProvider timeProvider, double expiredTimeInSecs) {
    return new PoseOutdatedMonitor(stateEstimator, timeProvider, expiredTimeInSecs);
  }

  public PoseStatus getPoseStatus() {
    return poseStatus.get();
  }

  public enum PoseStatus {
    VALID,
    OUTDATED
  }

  private final class Probe implements Runnable {

    private Probe() {}

    @Override
    public void run() {
      final Optional<DroneStateStamped> currentState = stateEstimator.getCurrentState();

      if (currentState.isPresent()) {
        final double currentTime = timeProvider.getCurrentTime().toSeconds();
        if (currentTime - currentState.get().getTimeStampInSeconds() >= expiredTimeInSecs) {
          poseStatus.set(PoseStatus.OUTDATED);
        } else {
          poseStatus.set(PoseStatus.VALID);
        }
      } else {
        poseStatus.set(PoseStatus.OUTDATED);
      }
    }
  }
}
