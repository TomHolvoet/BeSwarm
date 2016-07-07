package applications.parrot.tumsim;

import applications.trajectory.points.Point4D;
import applications.trajectory.Trajectories;
import control.FiniteTrajectory4d;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

/**
 * This class is for running the simulation with the AR drone in the Tum
 * simulator.
 *
 * @author Hoang Tung Dinh
 * @see <a href="https://github.com/dougvk/tum_simulator">The simulator</a>
 */
public final class TumRunStraightLinePattern extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("TumRunExampleTrajectory2");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final FiniteTrajectory4d trajectory = Trajectories.newStraightLineTrajectory(Point4D.create(0, 0, 1, 0),
                Point4D.create(2, 0, 1, 0), 0.2);
        final TumExampleFlightFacade flight = TumExampleFlightFacade.create(trajectory, connectedNode);
        flight.fly();
    }
}
