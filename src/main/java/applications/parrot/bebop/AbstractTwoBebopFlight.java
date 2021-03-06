package applications.parrot.bebop;

import control.FiniteTrajectory4d;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import java.util.concurrent.Executors;

/** @author Hoang Tung Dinh */
public abstract class AbstractTwoBebopFlight extends AbstractNodeMain {
  private final String nodeName;

  protected AbstractTwoBebopFlight(String nodeName) {
    this.nodeName = nodeName;
  }

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of(nodeName);
  }

  @Override
  public void onStart(final ConnectedNode connectedNode) {
    final String firstBebopName =
        connectedNode.getParameterTree().getString("beswarm/first_drone_name");
    final String secondBebopName =
        connectedNode.getParameterTree().getString("beswarm/second_drone_name");
    final String firstBebopPoseTopic =
        connectedNode.getParameterTree().getString("beswarm/first_drone_pose_topic");
    final String secondBebopPoseTopic =
        connectedNode.getParameterTree().getString("beswarm/second_drone_pose_topic");

    // TODO: check drone name and pose topic (need to be different)

    final BebopFlight firstBebopFlight =
        BebopFlight.create(
            firstBebopName,
            getConcreteTrajectoryForFirstBebop(),
            connectedNode,
            firstBebopPoseTopic);
    final BebopFlight secondBebopFlight =
        BebopFlight.create(
            secondBebopName,
            getConcreteTrajectoryForSecondBebop(),
            connectedNode,
            secondBebopPoseTopic);

    startFlying(firstBebopFlight);
    startFlying(secondBebopFlight);
  }

  private static void startFlying(final BebopFlight bebopFlight) {
    Executors.newSingleThreadExecutor()
        .submit(
            new Runnable() {
              @Override
              public void run() {
                bebopFlight.startFlying();
              }
            });
  }

  abstract FiniteTrajectory4d getConcreteTrajectoryForFirstBebop();

  abstract FiniteTrajectory4d getConcreteTrajectoryForSecondBebop();
}
