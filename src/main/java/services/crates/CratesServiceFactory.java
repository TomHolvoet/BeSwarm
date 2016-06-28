package services.crates;

import com.google.common.base.Optional;
import hal_quadrotor.Land;
import hal_quadrotor.LandRequest;
import hal_quadrotor.LandResponse;
import hal_quadrotor.Takeoff;
import hal_quadrotor.TakeoffRequest;
import hal_quadrotor.TakeoffResponse;
import hal_quadrotor.Velocity;
import hal_quadrotor.VelocityRequest;
import hal_quadrotor.VelocityResponse;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.FlipService;
import services.LandService;
import services.ServiceFactory;
import services.TakeOffService;
import services.VelocityService;

/**
 * @author Hoang Tung Dinh
 */
public final class CratesServiceFactory implements ServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(CratesServiceFactory.class);

    private final String droneName;
    private final String modelName;
    private final ConnectedNode connectedNode;
    private final String srvNamePrefix;

    private CratesServiceFactory(String droneName, String modelName, ConnectedNode connectedNode) {
        this.droneName = droneName;
        this.modelName = modelName;
        this.connectedNode = connectedNode;
        this.srvNamePrefix = "/hal/quadrotor/" + modelName + "/" + droneName + "/";
    }

    public static CratesServiceFactory create(String droneName, String modelName, ConnectedNode connectedNode) {
        return new CratesServiceFactory(droneName, modelName, connectedNode);
    }

    @Override
    public TakeOffService createTakeOffService() {
        try {
            final TakeOffService takeOffService = CratesTakeOffService.create(
                    connectedNode.<TakeoffRequest, TakeoffResponse>newServiceClient(
                            srvNamePrefix + "controller/Takeoff", Takeoff._TYPE));
            return takeOffService;
        } catch (ServiceNotFoundException e) {
            logger.info("Take off service not found. Drone: {}. Model: {}", droneName, modelName);
            throw new RuntimeException(
                    String.format("Take off service not found. Drone: %s. Model: %s", droneName, modelName));
        }
    }

    @Override
    public LandService createLandService() {
        try {
            final LandService landService = CratesLandService.create(
                    connectedNode.<LandRequest, LandResponse>newServiceClient(srvNamePrefix + "controller/Land",
                            Land._TYPE));
            return landService;
        } catch (ServiceNotFoundException e) {
            throw new RuntimeException(
                    String.format("Land service not found. Drone: %s. Model: %s", droneName, modelName));
        }
    }

    @Override
    public VelocityService createVelocityService() {
        try {
            final VelocityService velocityService = CratesVelocityService.create(
                    connectedNode.<VelocityRequest, VelocityResponse>newServiceClient(
                            srvNamePrefix + "controller/Velocity", Velocity._TYPE));
            return velocityService;
        } catch (ServiceNotFoundException e) {
            throw new RuntimeException(
                    String.format("Velocity service not found. Drone: %s. Model: %s", droneName, modelName));
        }
    }

    /**
     * This service is not supported by the crates simulator.
     */
    @Override
    public FlipService createFlipService() {
        throw new UnsupportedOperationException("This service is not supported by the crates simulator.");
    }
}
