package applications.parrot.bebop.rats;

import applications.trajectory.Trajectories;
import applications.trajectory.geom.point.Point3D;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.Trajectory4d;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

/**
 * Bebop starts at x=1.5, y=-1, yaw = -pi/2.
 *
 * @author Hoang Tung Dinh
 */
public final class BebopOne extends AbstractNodeMain {
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("BebopOne");
  }

  @Override
  public void onStart(ConnectedNode connectedNode) {
    final Trajectory4d rawTrajectory =
        Trajectories.circleTrajectoryBuilder()
            .setLocation(Point3D.create(1.5, -2.5, 1.5))
            .setRadius(1.5)
            .setFrequency(0.1)
            .fixYawAt(-Math.PI / 2)
            .setPhase(0)
            .build();

    final FiniteTrajectory4d trajectory =
        Choreography.builder().withTrajectory(rawTrajectory).forTime(120).build();

    final RatsFlight ratsFlight = RatsFlight.create(trajectory, connectedNode);
    ratsFlight.startRatsShow();
  }
}
