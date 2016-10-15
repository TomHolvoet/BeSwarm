package control;

import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import org.ros.time.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * A decorator for logging the state of a {@link VelocityController4d}. This class is assumed to be
 * used with Logback's <a
 * href="http://logback.qos.ch/manual/appenders.html#SiftingAppender">SiftingAppender</a> and <a
 * href="http://logback.qos.ch/manual/mdc.html">Mapped Diagnostic Contexts</a>.
 *
 * @author Hoang Tung Dinh
 */
public final class VelocityController4dLogger implements VelocityController4d {

  private static final Logger logger = LoggerFactory.getLogger(VelocityController4dLogger.class);

  private final VelocityController4d velocityController4d;
  private final Trajectory4d desiredTrajectory;
  private final TimeProvider systemTimeProvider;
  private final String loggerName;

  private VelocityController4dLogger(
      VelocityController4d velocityController4d,
      Trajectory4d desiredTrajectory,
      TimeProvider systemTimeProvider,
      String loggerName) {
    this.velocityController4d = velocityController4d;
    this.desiredTrajectory = desiredTrajectory;
    this.systemTimeProvider = systemTimeProvider;
    this.loggerName = loggerName;
  }

  public static VelocityController4dLogger create(
      VelocityController4d velocityController4d,
      Trajectory4d desiredTrajectory,
      TimeProvider systemTimeProvider,
      String loggerName) {
    return new VelocityController4dLogger(
        velocityController4d, desiredTrajectory, systemTimeProvider, loggerName);
  }

  @Override
  public InertialFrameVelocity computeNextResponse(
      Pose currentPose, InertialFrameVelocity currentVelocity, double currentTimeInSeconds) {
    final double currentSystemTimeInSeconds = systemTimeProvider.getCurrentTime().toSeconds();
    logPose(currentPose, currentTimeInSeconds, currentSystemTimeInSeconds);
    logVelocity(currentVelocity, currentTimeInSeconds, currentSystemTimeInSeconds);
    return velocityController4d.computeNextResponse(
        currentPose, currentVelocity, currentTimeInSeconds);
  }

  private void logVelocity(
      InertialFrameVelocity currentVelocity,
      double currentTimeInSeconds,
      double currentSystemTimeInSeconds) {
    final double deltaTimeInSeconds = 0.1;
    final double desiredVelocityX =
        (desiredTrajectory.getDesiredPositionX(currentTimeInSeconds + deltaTimeInSeconds)
                - desiredTrajectory.getDesiredPositionX(currentTimeInSeconds))
            / deltaTimeInSeconds;
    final double desiredVelocityY =
        (desiredTrajectory.getDesiredPositionY(currentTimeInSeconds + deltaTimeInSeconds)
                - desiredTrajectory.getDesiredPositionY(currentTimeInSeconds))
            / deltaTimeInSeconds;
    final double desiredVelocityZ =
        (desiredTrajectory.getDesiredPositionZ(currentTimeInSeconds + deltaTimeInSeconds)
                - desiredTrajectory.getDesiredPositionZ(currentTimeInSeconds))
            / deltaTimeInSeconds;
    final double desiredVelocityYaw =
        (desiredTrajectory.getDesiredAngleZ(currentTimeInSeconds + deltaTimeInSeconds)
                - desiredTrajectory.getDesiredAngleZ(currentTimeInSeconds))
            / deltaTimeInSeconds;

    MDC.put("loggerName", loggerName + "_velocity");
    logger.trace(
        "{} {} {} {} {} {} {} {} {}",
        currentSystemTimeInSeconds,
        currentVelocity.linearX(),
        currentVelocity.linearY(),
        currentVelocity.linearZ(),
        currentVelocity.angularZ(),
        desiredVelocityX,
        desiredVelocityY,
        desiredVelocityZ,
        desiredVelocityYaw);
  }

  private void logPose(
      Pose currentPose, double currentTimeInSeconds, double currentSystemTimeInSeconds) {
    MDC.put("loggerName", loggerName + "_pose");
    logger.trace(
        "{} {} {} {} {} {} {} {} {}",
        currentSystemTimeInSeconds,
        currentPose.x(),
        currentPose.y(),
        currentPose.z(),
        currentPose.yaw(),
        desiredTrajectory.getDesiredPositionX(currentTimeInSeconds),
        desiredTrajectory.getDesiredPositionY(currentTimeInSeconds),
        desiredTrajectory.getDesiredPositionZ(currentTimeInSeconds),
        desiredTrajectory.getDesiredAngleZ(currentTimeInSeconds));
  }
}
