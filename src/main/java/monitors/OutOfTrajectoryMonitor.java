package monitors;

import control.FiniteTrajectory4d;
import control.dto.DroneStateStamped;
import control.dto.Pose;
import localization.StateEstimator;
import org.ros.time.TimeProvider;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** @author Hoang Tung Dinh */
public final class OutOfTrajectoryMonitor {
  private final FiniteTrajectory4d trajectory;
  private final TimeProvider timeProvider;
  private final StateEstimator stateEstimator;
  private final double startTimeInSecs;
  private final double minimumDeviationInMeters;
  private static final long MONITOR_RATE_IN_MILLIS = 50;

  private final AtomicReference<Status> status = new AtomicReference<>(Status.NO_INFORMATION);

  private OutOfTrajectoryMonitor(
      FiniteTrajectory4d trajectory,
      TimeProvider timeProvider,
      StateEstimator stateEstimator,
      double startTimeInSecs,
      double minimumDeviationInMeters) {
    this.trajectory = trajectory;
    this.timeProvider = timeProvider;
    this.stateEstimator = stateEstimator;
    this.startTimeInSecs = startTimeInSecs;
    this.minimumDeviationInMeters = minimumDeviationInMeters;

    Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(new Probe(), 0, MONITOR_RATE_IN_MILLIS, TimeUnit.MILLISECONDS);
  }

  public static OutOfTrajectoryMonitor create(
      FiniteTrajectory4d trajectory,
      TimeProvider timeProvider,
      StateEstimator stateEstimator,
      double startTimeInSecs,
      double minimumDeviationInMeters) {
    return new OutOfTrajectoryMonitor(
        trajectory, timeProvider, stateEstimator, startTimeInSecs, minimumDeviationInMeters);
  }

  public Status getStatus() {
    return status.get();
  }

  public enum Status {
    WITH_IN_MINIMUM_DEVIATION,
    OUT_OF_TRAJECTORY,
    NO_INFORMATION
  }

  private final class Probe implements Runnable {

    @Override
    public void run() {
      final double currentTrajectoryTime =
          timeProvider.getCurrentTime().toSeconds() - startTimeInSecs;
      final Optional<DroneStateStamped> currentState = stateEstimator.getCurrentState();
      if (currentState.isPresent()) {
        final Pose desiredPose = Pose.createFromTrajectory(trajectory, currentTrajectoryTime);
        final double distance =
            Pose.computeEuclideanDistance(desiredPose, currentState.get().pose());
        if (distance > minimumDeviationInMeters) {
          status.set(Status.OUT_OF_TRAJECTORY);
        } else {
          status.set(Status.WITH_IN_MINIMUM_DEVIATION);
        }
      } else {
        status.set(Status.NO_INFORMATION);
      }
    }
  }
}
