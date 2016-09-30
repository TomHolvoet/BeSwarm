package operationaltesting.ratsflight;

import applications.parrot.bebop.rats.RatsFlight;
import applications.trajectory.Trajectories;
import applications.trajectory.geom.point.Point4D;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.Trajectory4d;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

/**
 * Bebop starts at x=2, y=-5, yaw = -pi/2.
 *
 * @author Hoang Tung Dinh
 */
public final class BebopOneHover extends AbstractNodeMain {
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("BebopOne");
  }

  @Override
  public void onStart(ConnectedNode connectedNode) {
    final Trajectory4d rawTrajectory =
        Trajectories.newHoldPositionTrajectory(Point4D.create(2, -5, 1, -StrictMath.PI / 2));

    final FiniteTrajectory4d trajectory =
        Choreography.builder().withTrajectory(rawTrajectory).forTime(3).build();

    final RatsFlight ratsFlight = RatsFlight.create(trajectory, connectedNode);
    ratsFlight.startRatsShow();
  }
}
