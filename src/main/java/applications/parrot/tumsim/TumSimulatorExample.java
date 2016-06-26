package applications.parrot.tumsim;

import applications.ExampleFlight;
import applications.ExampleTrajectory2;
import control.Trajectory4d;
import control.localization.GazeboModelStateEstimator;
import control.localization.StateEstimator;
import gazebo_msgs.ModelStates;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import services.ServiceFactory;
import services.parrot.TumSimServiceFactory;
import services.ros_subscribers.MessagesSubscriberService;

/**
 * This class is for running the simulation with the AR drone in the Tum simulator.
 *
 * @author Hoang Tung Dinh
 * @see <a href="https://github.com/dougvk/tum_simulator">The simulator</a>
 */
public final class TumSimulatorExample extends AbstractNodeMain {

    private static final String MODEL_NAME = "quadrotor";

    private TumSimulatorExample() {
        super();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("TumSimulatorExample");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final ServiceFactory serviceFactory = TumSimServiceFactory.create(connectedNode);
        final StateEstimator stateEstimator = getStateEstimator(connectedNode);
        final Trajectory4d trajectory = ExampleTrajectory2.create();
        final ExampleFlight exampleFlight = ExampleFlight.create(serviceFactory, stateEstimator, trajectory,
                connectedNode);
        exampleFlight.fly();
    }

    private static StateEstimator getStateEstimator(ConnectedNode connectedNode) {
        final MessagesSubscriberService<ModelStates> modelStateSubscriber = MessagesSubscriberService.create(
                connectedNode.<ModelStates>newSubscriber("/gazebo/model_states", ModelStates._TYPE));
        return GazeboModelStateEstimator.create(modelStateSubscriber, MODEL_NAME);
    }
}
