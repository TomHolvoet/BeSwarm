package localization;

import com.google.common.base.Optional;
import com.google.common.collect.EvictingQueue;
import control.dto.DroneStateStamped;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A fake {@link StateEstimator} that is a decorator of another {@link StateEstimator}. User can use
 * this fake {@link StateEstimator} to adjust the localization frequency and add noise to the actual
 * state received. This class should be used with a very precise state estimator (e.g., {@link
 * GazeboModelStateEstimator} or {@link CratesSimStateEstimator}) and should be used only for
 * simulation purpose. This decorator assumes that the noise for all pose and velocity dimensions is
 * from the same Gaussian distribution.
 *
 * <p>TODO: test this class
 *
 * @author Hoang Tung Dinh
 */
public final class FakeStateEstimatorDecorator implements StateEstimator {

  private static final Logger logger = LoggerFactory.getLogger(FakeStateEstimatorDecorator.class);

  private final StateEstimator actualStateEstimator;
  private final GaussianRandomGenerator noiseGenerator;
  private final double noiseMean;
  private final double noiseDeviation;
  private final EvictingQueue<DroneStateStamped> stateQueue;

  @Nullable private DroneStateStamped currentState;

  private FakeStateEstimatorDecorator(
      final StateEstimator actualStateEstimator,
      double localizationFrequency,
      GaussianRandomGenerator noiseGenerator,
      double noiseMean,
      double noiseDeviation,
      int numberOfAveragingPoses) {
    this.actualStateEstimator = actualStateEstimator;
    this.noiseGenerator = noiseGenerator;
    this.noiseMean = noiseMean;
    this.noiseDeviation = noiseDeviation;

    checkArgument(numberOfAveragingPoses >= 1, "numberOfAveragingPoses must be at least one.");
    this.stateQueue = EvictingQueue.create(numberOfAveragingPoses);

    final long localizationRateInNanoSeconds = (long) (1000000000L / localizationFrequency);
    Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(
            new StateGetter(), 0, localizationRateInNanoSeconds, TimeUnit.NANOSECONDS);
  }

  public static FakeStateEstimatorDecorator create(
      StateEstimator actualStateEstimator,
      double localizationFrequency,
      GaussianRandomGenerator noiseGenerator,
      double noiseMean,
      double noiseDeviation,
      int numberOfAveragingPoses) {
    return new FakeStateEstimatorDecorator(
        actualStateEstimator,
        localizationFrequency,
        noiseGenerator,
        noiseMean,
        noiseDeviation,
        numberOfAveragingPoses);
  }

  @Override
  public Optional<DroneStateStamped> getCurrentState() {
    if (currentState == null) {
      return Optional.absent();
    } else {
      return Optional.of(currentState);
    }
  }

  private final class StateGetter implements Runnable {

    private StateGetter() {}

    @Override
    public void run() {
      final Optional<DroneStateStamped> actualCurrentState = actualStateEstimator.getCurrentState();
      if (actualCurrentState.isPresent()) {
        final DroneStateStamped state = actualCurrentState.get();
        logGroundTruthPose(state);
        final DroneStateStamped newState = addNoiseToState(state);
        currentState = getAveragedState(newState);
      } else {
        currentState = null;
      }
    }

    private DroneStateStamped getAveragedState(DroneStateStamped newState) {
      final double timeStamp = newState.getTimeStampInSeconds();
      stateQueue.add(newState);
      final Pose averagedPose = getAveragedPose(stateQueue);
      final InertialFrameVelocity averagedVelocity = getAveragedVelocity(stateQueue);
      return DroneStateStamped.create(averagedPose, averagedVelocity, timeStamp);
    }

    private InertialFrameVelocity getAveragedVelocity(EvictingQueue<DroneStateStamped> stateQueue) {
      double linearX = 0;
      double linearY = 0;
      double linearZ = 0;
      double angularZ = 0;

      for (final DroneStateStamped state : stateQueue) {
        final InertialFrameVelocity velocity = state.inertialFrameVelocity();
        linearX += velocity.linearX();
        linearY += velocity.linearY();
        linearZ += velocity.linearZ();
        angularZ += velocity.angularZ();
      }

      final int queueSize = stateQueue.size();
      return Velocity.builder()
          .setLinearX(linearX / queueSize)
          .setLinearY(linearY / queueSize)
          .setLinearZ(linearZ / queueSize)
          .setAngularZ(angularZ / queueSize)
          .build();
    }

    private Pose getAveragedPose(Collection<DroneStateStamped> stateQueue) {
      double x = 0;
      double y = 0;
      double z = 0;
      double yaw = 0;

      for (final DroneStateStamped state : stateQueue) {
        final Pose pose = state.pose();
        x += pose.x();
        y += pose.y();
        z += pose.z();
        yaw += pose.yaw();
      }

      final int queueSize = stateQueue.size();
      return Pose.builder()
          .setX(x / queueSize)
          .setY(y / queueSize)
          .setZ(z / queueSize)
          .setYaw(yaw / queueSize)
          .build();
    }

    private void logGroundTruthPose(DroneStateStamped state) {
      logger.trace(
          "{} {} {} {} {}",
          state.getTimeStampInSeconds(),
          state.pose().x(),
          state.pose().y(),
          state.pose().z(),
          state.pose().yaw());
    }

    private DroneStateStamped addNoiseToState(DroneStateStamped state) {
      final Pose noisyPose = addNoiseToPose(state.pose());
      final InertialFrameVelocity noisyVelocity = addNoiseToVelocity(state.inertialFrameVelocity());
      return DroneStateStamped.create(noisyPose, noisyVelocity, state.getTimeStampInSeconds());
    }

    private InertialFrameVelocity addNoiseToVelocity(InertialFrameVelocity inertialFrameVelocity) {
      return Velocity.builder()
          .setLinearX(inertialFrameVelocity.linearX() + generateNoise())
          .setLinearY(inertialFrameVelocity.linearY() + generateNoise())
          .setLinearZ(inertialFrameVelocity.linearZ() + generateNoise())
          .setAngularZ(inertialFrameVelocity.angularZ() + generateNoise())
          .build();
    }

    private Pose addNoiseToPose(Pose pose) {
      return Pose.builder()
          .setX(pose.x() + generateNoise())
          .setY(pose.y() + generateNoise())
          .setZ(pose.z() + generateNoise())
          .setYaw(pose.yaw() + generateNoise())
          .build();
    }

    private double generateNoise() {
      return noiseGenerator.nextNormalizedDouble() * noiseDeviation + noiseMean;
    }
  }
}
