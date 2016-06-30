package applications.cratessim;

import applications.ExampleFlight;
import applications.ExampleTrajectory;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.localization.CratesSimStateEstimator;
import control.localization.StateEstimator;
import hal_quadrotor.State;
import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ServiceFactory;
import services.crates.CratesServiceFactory;
import services.ros_subscribers.MessagesSubscriberService;
import sim.Insert;
import sim.InsertRequest;
import sim.InsertResponse;

import java.util.concurrent.TimeUnit;

/**
 * This class is for running the Crates simulator.
 *
 * @author Hoang Tung Dinh
 * @see <a href="https://bitbucket.org/vicengomez/crates">The simulator</a>
 */
public final class CratesSimulatorExample extends AbstractNodeMain {
    private static final Logger logger = LoggerFactory
            .getLogger(CratesSimulatorExample.class);
    private static final String DRONE_NAME = "uav";
    private static final String MODEL_NAME = "hummingbird";

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("CratesSimulatorExample");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        addDroneModel(connectedNode);
        final double defaultTime = 60;
        final ServiceFactory serviceFactory = CratesServiceFactory
                .create(DRONE_NAME, MODEL_NAME, connectedNode);
        final StateEstimator stateEstimator = getStateEstimator(connectedNode);
        final FiniteTrajectory4d trajectory = Choreography.builder()
                .withTrajectory(ExampleTrajectory.create()).forTime(defaultTime)
                .build();
        final ExampleFlight exampleFlight = ExampleFlight
                .create(serviceFactory, stateEstimator, trajectory,
                        connectedNode);
        exampleFlight.fly();
    }

    private static StateEstimator getStateEstimator(
            ConnectedNode connectedNode) {
        final String srvNamePrefix =
                "/hal/quadrotor/" + MODEL_NAME + "/" + DRONE_NAME + "/";
        final MessagesSubscriberService<State> cratesTruthStateSubscriber =
                MessagesSubscriberService
                        .create(
                                connectedNode.<State>newSubscriber(
                                        srvNamePrefix + "Truth", State._TYPE),
                                2);
        return CratesSimStateEstimator.create(cratesTruthStateSubscriber);
    }

    private static void warmUp() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            logger.info("Warm up is interrupted.", e);
        }
    }

    private static void addDroneModel(ConnectedNode connectedNode) {
        try {
            final String srvName = "/simulator/Insert";
            final ServiceClient<InsertRequest, InsertResponse> insertSrv =
                    connectedNode
                            .newServiceClient(srvName,
                                    Insert._TYPE);
            final InsertRequest insertRequest = insertSrv.newMessage();
            insertRequest.setModelName(DRONE_NAME);
            insertRequest.setModelType("model://" + MODEL_NAME);
            insertSrv.call(insertRequest,
                    InsertServiceResponseListener.create());
            warmUp();
        } catch (ServiceNotFoundException e) {
            logger.info("Cannot connect to insert service.", e);
        }
    }

    private static final class InsertServiceResponseListener
            implements ServiceResponseListener<InsertResponse> {
        private InsertServiceResponseListener() {
        }

        public static InsertServiceResponseListener create() {
            return new InsertServiceResponseListener();
        }

        @Override
        public void onSuccess(InsertResponse insertResponse) {
            logger.info("Successfully inserted the drone model!!!");
            logger.info(insertResponse.getStatusMessage());
        }

        @Override
        public void onFailure(RemoteException e) {
            logger.info("Cannot insert the drone model!!!", e);
        }
    }
}
