package control;

import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;

import javax.annotation.Nullable;

/**
 * The controller output filter for a pid controller. TODO: test this class
 *
 * @author Hoang Tung Dinh
 * @see <a href="http://controlguru.com/pid-with-controller-output-co-filter/">Equation</a>
 */
public final class PidCoFilter4d implements VelocityController4d {
  private final VelocityController4d pidController;
  private final double filterTimeConstance;
  @Nullable private InertialFrameVelocity lastResponse;
  private double lastTimeInSeconds;

  private PidCoFilter4d(VelocityController4d pidController, double filterTimeConstance) {
    this.pidController = pidController;
    this.filterTimeConstance = filterTimeConstance;
  }

  public static PidCoFilter4d create(
      VelocityController4d pidController, double filterTimeConstant) {
    return new PidCoFilter4d(pidController, filterTimeConstant);
  }

  @Override
  public InertialFrameVelocity computeNextResponse(
      Pose currentPose, InertialFrameVelocity currentVelocity, double currentTimeInSeconds) {
    final InertialFrameVelocity rawControllerOutput =
        pidController.computeNextResponse(currentPose, currentVelocity, currentTimeInSeconds);
    if (lastResponse == null) {
      lastResponse = rawControllerOutput;
      lastTimeInSeconds = currentTimeInSeconds;
      return rawControllerOutput;
    } else {
      return getFilteredResponse(rawControllerOutput, currentTimeInSeconds);
    }
  }

  private InertialFrameVelocity getFilteredResponse(
      InertialFrameVelocity rawControllerOutput, double currentTimeInSeconds) {

    final double filteredLinearX =
        getFilteredResponse(
            rawControllerOutput.linearX(), currentTimeInSeconds, lastResponse.linearX());

    final double filteredLinearY =
        getFilteredResponse(
            rawControllerOutput.linearY(), currentTimeInSeconds, lastResponse.linearY());

    final double filteredLinearZ =
        getFilteredResponse(
            rawControllerOutput.linearZ(), currentTimeInSeconds, lastResponse.linearZ());

    final double filteredAngularZ =
        getFilteredResponse(
            rawControllerOutput.angularZ(), currentTimeInSeconds, lastResponse.angularZ());

    return Velocity.builder()
        .setLinearX(filteredLinearX)
        .setLinearY(filteredLinearY)
        .setLinearZ(filteredLinearZ)
        .setAngularZ(filteredAngularZ)
        .build();
  }

  private double getFilteredResponse(
      double rawControllerOutput, double currentTimeInSeconds, double lastRespose) {
    return rawControllerOutput
        - filterTimeConstance
            * ((rawControllerOutput - lastRespose) / (currentTimeInSeconds - lastTimeInSeconds));
  }
}
