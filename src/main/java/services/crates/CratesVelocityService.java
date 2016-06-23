package services.crates;

import control.dto.Velocity;
import hal_quadrotor.VelocityRequest;
import hal_quadrotor.VelocityResponse;
import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.VelocityService;

/**
 * @author Hoang Tung Dinh
 */
public final class CratesVelocityService implements VelocityService {
    private static final Logger logger = LoggerFactory.getLogger(CratesVelocityService.class);
    private final ServiceClient<VelocityRequest, VelocityResponse> srvVelocity;

    private CratesVelocityService(ServiceClient<VelocityRequest, VelocityResponse> srvVelocity) {
        this.srvVelocity = srvVelocity;
    }

    public static CratesVelocityService create(ServiceClient<VelocityRequest, VelocityResponse> srvVelocity) {
        return new CratesVelocityService(srvVelocity);
    }

    @Override
    public void sendVelocityMessage(Velocity velocity) {
        final VelocityRequest velocityRequest = srvVelocity.newMessage();
        velocityRequest.setDx(velocity.linearX());
        velocityRequest.setDy(velocity.linearY());
        velocityRequest.setDz(velocity.linearZ());
        velocityRequest.setYaw(velocity.angularZ());
        logger.debug("Sending velocity: [x={} y={} z={} yaw={}]", velocity.linearX(), velocity.linearY(),
                velocity.linearZ(), velocity.angularZ());
        srvVelocity.call(velocityRequest, VelocityServiceResponseListener.create());
    }

    private static final class VelocityServiceResponseListener implements ServiceResponseListener<VelocityResponse> {
        private VelocityServiceResponseListener() {}

        public static VelocityServiceResponseListener create() {
            return new VelocityServiceResponseListener();
        }

        @Override
        public void onSuccess(VelocityResponse velocityResponse) {
            logger.info("Successfully sent velocity message!!!");
            logger.info(velocityResponse.getStatus());
        }

        @Override
        public void onFailure(RemoteException e) {
            logger.info("Cannot send velocity message!!!", e);
        }
    }
}
