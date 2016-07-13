package services.crates;

import hal_quadrotor.Land;
import hal_quadrotor.LandRequest;
import hal_quadrotor.LandResponse;
import hal_quadrotor.State;
import hal_quadrotor.Takeoff;
import hal_quadrotor.TakeoffRequest;
import hal_quadrotor.TakeoffResponse;
import hal_quadrotor.Velocity;
import hal_quadrotor.VelocityHeightRequest;
import hal_quadrotor.VelocityHeightResponse;
import hal_quadrotor.VelocityRequest;
import hal_quadrotor.VelocityResponse;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.CommonServiceFactory;
import services.FlyingStateService;
import services.LandService;
import services.TakeOffService;
import services.Velocity2dService;
import services.Velocity3dService;
import services.ros_subscribers.MessagesSubscriberService;

/**
 * @author Hoang Tung Dinh
 */
public final class CratesServiceFactory implements CommonServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(CratesServiceFactory.class);

    private final String droneName;
    private final String modelName;
    private final ConnectedNode connectedNode;
    private final String namePrefix;

    private CratesServiceFactory(String droneName, String modelName, ConnectedNode connectedNode) {
        this.droneName = droneName;
        this.modelName = modelName;
        this.connectedNode = connectedNode;
        this.namePrefix = "/hal/quadrotor/" + modelName + "/" + droneName + "/";
    }

    public static CratesServiceFactory create(String droneName, String modelName, ConnectedNode connectedNode) {
        return new CratesServiceFactory(droneName, modelName, connectedNode);
    }

    @Override
    public TakeOffService createTakeOffService() {
        try {
            return CratesTakeOffService.create(
                    connectedNode.<TakeoffRequest, TakeoffResponse>newServiceClient(namePrefix + "controller/Takeoff",
                            Takeoff._TYPE));
        } catch (ServiceNotFoundException e) {
            logger.info("Take off service not found. Drone: {}. Model: {}. Exception: {}", droneName, modelName, e);
            throw new RuntimeException(
                    String.format("Take off service not found. Drone: %s. Model: %s", droneName, modelName));
        }
    }

    @Override
    public LandService createLandService() {
        try {
            return CratesLandService.create(
                    connectedNode.<LandRequest, LandResponse>newServiceClient(namePrefix + "controller/Land",
                            Land._TYPE));
        } catch (ServiceNotFoundException e) {
            throw new RuntimeException(
                    String.format("Land service not found. Drone: %s. Model: %s", droneName, modelName));
        }
    }

    @Override
    public FlyingStateService createFlyingStateService() {
        final String topicName = namePrefix + "Truth";
        final MessagesSubscriberService<State> flyingStateSubscriber = MessagesSubscriberService.create(
                connectedNode.<State>newSubscriber(topicName, State._TYPE));
        return CratesFlyingStateService.create(flyingStateSubscriber);
    }

    public Velocity3dService createVelocity3dService() {
        try {
            return CratesVelocity3dService.create(connectedNode.<VelocityRequest, VelocityResponse>newServiceClient(
                    namePrefix + "controller/Velocity", Velocity._TYPE));
        } catch (ServiceNotFoundException e) {
            throw new RuntimeException(
                    String.format("Velocity service not found. Drone: %s. Model: %s", droneName, modelName));
        }
    }

    public Velocity2dService createVelocity2dService() {
        try {
            return CratesVelocity2dService.create(
                    connectedNode.<VelocityHeightRequest, VelocityHeightResponse>newServiceClient(
                            namePrefix + "controller/VelocityHeight", Velocity._TYPE));
        } catch (ServiceNotFoundException e) {
            throw new RuntimeException(
                    String.format("Velocity service not found. Drone: %s. Model: %s", droneName, modelName));
        }
    }
}
