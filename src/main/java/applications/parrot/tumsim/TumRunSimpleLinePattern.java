package applications.parrot.tumsim;

import applications.LineTrajectory;
import choreo.Choreography;
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
public final class TumRunSimpleLinePattern extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("TumRunSimpleLinePattern");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final double flightDuration = 100;
        final FiniteTrajectory4d trajectory = Choreography.builder()
                .withTrajectory(LineTrajectory.create(flightDuration, 2.0))
                .forTime(flightDuration).build();
        final TumExampleFlightFacade flight = TumExampleFlightFacade
                .create(trajectory, connectedNode);
        flight.fly();
    }
}
