package applications.parrot.tumsim;

import applications.ExampleTrajectory;
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
public final class TumRunExampleTrajectory2 extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("TumRunExampleTrajectory2");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final double defaultTime = 60;
        final FiniteTrajectory4d trajectory = Choreography.builder()
                .withTrajectory(ExampleTrajectory.create())
                .forTime(defaultTime)
                .build();
        final TumExampleFlightFacade flight = TumExampleFlightFacade.create(trajectory, connectedNode);
        flight.fly();
    }
}
