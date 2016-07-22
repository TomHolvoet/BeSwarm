package applications.parrot.tumsim;

import applications.trajectory.Trajectories;
import applications.trajectory.points.Point4D;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.Trajectory4d;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class TumSimulatorCorkscrewExample extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("TumRunExampleTrajectory2");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final FiniteTrajectory4d trajectory = getConcreteTrajectory();
        final TumExampleFlightFacade flight = TumExampleFlightFacade.create(trajectory, connectedNode);
        flight.fly();
    }

    private FiniteTrajectory4d getConcreteTrajectory() {
        Point4D start = Point4D.create(0, 0, 10, 0);
        Point4D end = Point4D.create(0, 15, 25, 0);
        FiniteTrajectory4d target1 = Trajectories.newCorkscrewTrajectory(start, end, 1, 0.5, 0.3, 0);
        Trajectory4d hold1 = Trajectories.newHoldPositionTrajectory(start);
        return Choreography.builder().withTrajectory(hold1).forTime(30).withTrajectory(target1).build();
    }
}
