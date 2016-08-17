package time;

import org.ros.message.Time;
import org.ros.node.ConnectedNode;

/**
 * A {@link TimeProvider}'s instance using ROS timer.
 *
 * @author Hoang Tung Dinh
 */
public final class RosTime implements TimeProvider {
  private final ConnectedNode connectedNode;

  private RosTime(ConnectedNode connectedNode) {
    this.connectedNode = connectedNode;
  }

  public static RosTime create(ConnectedNode connectedNode) {
    return new RosTime(connectedNode);
  }

  @Override
  public long getCurrentTimeNanoSeconds() {
    final Time currentTime = connectedNode.getCurrentTime();
    return currentTime.secs * 1000000000L + currentTime.nsecs;
  }
}
