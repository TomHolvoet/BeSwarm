package applications.cratessim;

import applications.ExampleFlight;
import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;
import control.PidParameters;
import control.localization.CratesSimStateEstimator;
import control.localization.StateEstimator;
import hal_quadrotor.State;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.crates.CratesServiceFactory;
import services.crates.CratesUtilities;
import services.rossubscribers.MessagesSubscriberService;
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
    private static final Logger logger = LoggerFactory.getLogger(CratesSimulatorExample.class);
    private static final String DRONE_NAME = "uav";
    private static final String MODEL_NAME = "hummingbird";

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("CratesSimulatorExample");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        addDroneModel(connectedNode);
        final CratesServiceFactory cratesServiceFactory = CratesServiceFactory.create(DRONE_NAME, MODEL_NAME,
                connectedNode);
        final StateEstimator stateEstimator = getStateEstimator(connectedNode);
        final FiniteTrajectory4d trajectory = TrajectoriesForTesting.getFastCircle();
        final ExampleFlight exampleFlight = ExampleFlight.builder()
                .withConnectedNode(connectedNode)
                .withFiniteTrajectory4d(trajectory)
                .withFlyingStateService(cratesServiceFactory.createFlyingStateService())
                .withLandService(cratesServiceFactory.createLandService())
                .withStateEstimator(stateEstimator)
                .withTakeOffService(cratesServiceFactory.createTakeOffService())
                .withVelocityService(cratesServiceFactory.createVelocity2dService())
                .withPidLinearX(PidParameters.builder().setKp(2).setKd(1).setKi(0).setLagTimeInSeconds(0.2).build())
                .withPidLinearY(PidParameters.builder().setKp(2).setKd(1).setKi(0).setLagTimeInSeconds(0.2).build())
                .withPidLinearZ(PidParameters.builder().setKp(2).setKd(1).setKi(0).setLagTimeInSeconds(0.2).build())
                .withPidAngularZ(
                        PidParameters.builder().setKp(1.5).setKd(0.75).setKi(0).setLagTimeInSeconds(0.2).build())
                .build();
        exampleFlight.fly();
    }

    private static StateEstimator getStateEstimator(ConnectedNode connectedNode) {
        final String srvNamePrefix = "/hal/quadrotor/" + MODEL_NAME + "/" + DRONE_NAME + "/";
        final MessagesSubscriberService<State> cratesTruthStateSubscriber = MessagesSubscriberService.create(
                connectedNode.<State>newSubscriber(srvNamePrefix + "Truth", State._TYPE), 2);
        return CratesSimStateEstimator.create(cratesTruthStateSubscriber);
    }

    private static void warmUp() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            logger.info("Warm up is interrupted.", e);
            Thread.currentThread().interrupt();
        }
    }

    private static void addDroneModel(ConnectedNode connectedNode) {
        try {
            final String srvName = "/simulator/Insert";
            final ServiceClient<InsertRequest, InsertResponse> insertSrv = connectedNode.newServiceClient(srvName,
                    Insert._TYPE);
            final InsertRequest insertRequest = insertSrv.newMessage();
            insertRequest.setModelName(DRONE_NAME);
            insertRequest.setModelType("model://" + MODEL_NAME);
            CratesUtilities.sendRequest(insertSrv, insertRequest);
            warmUp();
        } catch (ServiceNotFoundException e) {
            logger.info("Cannot connect to insert service.", e);
        }
    }
}
