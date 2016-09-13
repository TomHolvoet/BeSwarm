package applications;

import control.PidParameters;
import org.ros.node.parameter.ParameterTree;

/**
 * Utility class for creating appropriate java instances using parameters from ROS parameter server.
 *
 * @author Hoang Tung Dinh
 */
public final class RosParameters {

  private RosParameters() {}

  public static PidParameters createPidParameters(
      ParameterTree parameterTree,
      String argKp,
      String argKd,
      String argKi,
      String argLagTimeInSeconds) {
    final double pidLinearXKp = parameterTree.getDouble(argKp);
    final double pidLinearXKd = parameterTree.getDouble(argKd);
    final double pidLinearXKi = parameterTree.getDouble(argKi);
    final double pidLagTimeInSeconds = parameterTree.getDouble(argLagTimeInSeconds);
    return PidParameters.builder()
        .setKp(pidLinearXKp)
        .setKd(pidLinearXKd)
        .setKi(pidLinearXKi)
        .setLagTimeInSeconds(pidLagTimeInSeconds)
        .build();
  }
}
