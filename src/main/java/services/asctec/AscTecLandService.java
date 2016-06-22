package services.asctec;

import hal_quadrotor.LandRequest;
import hal_quadrotor.LandResponse;
import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.LandService;

/**
 * @author Hoang Tung Dinh
 */
public final class AscTecLandService implements LandService {
    private static final Logger logger = LoggerFactory.getLogger(AscTecLandService.class);
    private final ServiceClient<LandRequest, LandResponse> srvLand;

    private AscTecLandService(ServiceClient<LandRequest, LandResponse> srvLand) {
        this.srvLand = srvLand;
    }

    public static AscTecLandService create(ServiceClient<LandRequest, LandResponse> srvLand) {
        return new AscTecLandService(srvLand);
    }

    @Override
    public void sendLandingMessage() {
        final LandRequest landRequest = srvLand.newMessage();
        srvLand.call(landRequest, LandServiceResponseListener.create());
    }

    private static final class LandServiceResponseListener implements ServiceResponseListener<LandResponse> {
        private LandServiceResponseListener() {}

        public static LandServiceResponseListener create() {
            return new LandServiceResponseListener();
        }

        @Override
        public void onSuccess(LandResponse landResponse) {
            logger.info("Successfully landed!!!");
            logger.info(landResponse.getStatus());
        }

        @Override
        public void onFailure(RemoteException e) {
            logger.info("Cannot send landing message!!!", e);
        }
    }
}
