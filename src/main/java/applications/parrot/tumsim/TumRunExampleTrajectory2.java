package applications.parrot.tumsim;

import applications.ExampleTrajectory;
import control.Trajectory4d;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

/**
 * This class is for running the simulation with the AR drone in the Tum simulator.
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
        final Trajectory4d trajectory = ExampleTrajectory.create();
        final TumExampleFlightFacade flight = TumExampleFlightFacade.create(trajectory, connectedNode);
        flight.fly();
    }
}
