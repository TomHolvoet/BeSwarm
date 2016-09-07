package applications;

import control.PidParameters;
import org.ros.node.ConnectedNode;

/**
 * Utility class for creating appropriate java instances using parameters from ROS parameter server.
 *
 * @author Hoang Tung Dinh
 */
public final class RosParameters {

  private RosParameters() {}

  public static PidParameters createPidParameters(
      ConnectedNode connectedNode,
      String argKp,
      String argKd,
      String argKi,
      String argLagTimeInSeconds) {
    final double pidLinearXKp = connectedNode.getParameterTree().getDouble(argKp);
    final double pidLinearXKd = connectedNode.getParameterTree().getDouble(argKd);
    final double pidLinearXKi = connectedNode.getParameterTree().getDouble(argKi);
    final double pidLagTimeInSeconds =
        connectedNode.getParameterTree().getDouble(argLagTimeInSeconds);
    return PidParameters.builder()
        .setKp(pidLinearXKp)
        .setKd(pidLinearXKd)
        .setKi(pidLinearXKi)
        .setLagTimeInSeconds(pidLagTimeInSeconds)
        .build();
  }
}
