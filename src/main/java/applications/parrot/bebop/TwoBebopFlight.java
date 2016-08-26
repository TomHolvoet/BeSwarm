package applications.parrot.bebop;

import control.FiniteTrajectory4d;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import java.util.concurrent.Executors;

/** @author Hoang Tung Dinh */
public abstract class TwoBebopFlight extends AbstractNodeMain {
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("TwoBebopFlight");
  }

  @Override
  public void onStart(final ConnectedNode connectedNode) {
    final String firstBebopName =
        connectedNode.getParameterTree().getString("beswarm/first_bebop_name");
    final String secondBebopName =
        connectedNode.getParameterTree().getString("beswarm/second_bebop_name");
    final String firstBebopPoseTopic =
        connectedNode.getParameterTree().getString("beswarm/first_bebop_pose_topic");
    final String secondBebopPoseTopic =
        connectedNode.getParameterTree().getString("beswarm/second_bebop_pose_topic");

    final SingleBebopFlight firstBebopFlight =
        SingleBebopFlight.create(
            firstBebopName,
            getConcreteTrajectoryForFirstBebop(),
            connectedNode,
            firstBebopPoseTopic);
    final SingleBebopFlight secondBebopFlight =
        SingleBebopFlight.create(
            secondBebopName,
            getConcreteTrajectoryForSecondBebop(),
            connectedNode,
            secondBebopPoseTopic);

    startFlying(firstBebopFlight);
    startFlying(secondBebopFlight);
  }

  private static void startFlying(final SingleBebopFlight singleBebopFlight) {
    Executors.newSingleThreadExecutor()
        .submit(
            new Runnable() {
              @Override
              public void run() {
                singleBebopFlight.startFlying();
              }
            });
  }

  protected abstract FiniteTrajectory4d getConcreteTrajectoryForFirstBebop();

  protected abstract FiniteTrajectory4d getConcreteTrajectoryForSecondBebop();
}
