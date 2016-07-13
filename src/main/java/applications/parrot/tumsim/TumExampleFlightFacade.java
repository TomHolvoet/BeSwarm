package applications.parrot.tumsim;

import applications.ExampleFlight;
import control.FiniteTrajectory4d;
import control.localization.GazeboModelStateEstimator;
import control.localization.StateEstimator;
import gazebo_msgs.ModelStates;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.parrot.ParrotServiceFactory;
import services.parrot.TumSimServiceFactory;
import services.ros_subscribers.MessagesSubscriberService;

import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
final class TumExampleFlightFacade {
    private static final Logger logger = LoggerFactory.getLogger(TumExampleFlightFacade.class);
    private static final String MODEL_NAME = "quadrotor";
    private final ExampleFlight exampleFlight;

    private TumExampleFlightFacade(FiniteTrajectory4d trajectory4d, ConnectedNode connectedNode) {
        final ParrotServiceFactory parrotServiceFactory = TumSimServiceFactory.create(connectedNode);
        final StateEstimator stateEstimator = getStateEstimator(connectedNode);
        exampleFlight = ExampleFlight.builder()
                .withConnectedNode(connectedNode)
                .withFiniteTrajectory4d(trajectory4d)
                .withFlyingStateService(parrotServiceFactory.createFlyingStateService())
                .withLandService(parrotServiceFactory.createLandService())
                .withStateEstimator(stateEstimator)
                .withTakeOffService(parrotServiceFactory.createTakeOffService())
                .withVelocityService(parrotServiceFactory.createVelocity4dService())
                .build();
    }

    public static TumExampleFlightFacade create(FiniteTrajectory4d trajectory4d, ConnectedNode connectedNode) {
        return new TumExampleFlightFacade(trajectory4d, connectedNode);
    }

    public void fly() {
        // without this code, the take off message cannot be sent properly (I
        // don't understand why).
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            logger.info("Warm up time is interrupted.", e);
        }

        exampleFlight.fly();
    }

    private static StateEstimator getStateEstimator(ConnectedNode connectedNode) {
        final MessagesSubscriberService<ModelStates> modelStateSubscriber = MessagesSubscriberService.create(
                connectedNode.<ModelStates>newSubscriber("/gazebo/model_states", ModelStates._TYPE));
        return GazeboModelStateEstimator.create(modelStateSubscriber, MODEL_NAME);
    }
}
