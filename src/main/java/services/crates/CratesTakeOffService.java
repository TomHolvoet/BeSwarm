package services.crates;

import hal_quadrotor.TakeoffRequest;
import hal_quadrotor.TakeoffResponse;
import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.TakeOffService;

/**
 * @author Hoang Tung Dinh
 */
public final class CratesTakeOffService implements TakeOffService {
    private static final Logger logger = LoggerFactory.getLogger(CratesTakeOffService.class);
    private static final double DEFAULT_TAKE_OFF_ALTITUDE = 1;
    private final ServiceClient<TakeoffRequest, TakeoffResponse> srvTakeOff;

    private CratesTakeOffService(ServiceClient<TakeoffRequest, TakeoffResponse> srvTakeOff) {
        this.srvTakeOff = srvTakeOff;
    }

    public static CratesTakeOffService create(ServiceClient<TakeoffRequest, TakeoffResponse> srvTakeOff) {
        return new CratesTakeOffService(srvTakeOff);
    }

    @Override
    public void sendTakingOffMessage() {
        sendTakingOffMessage(DEFAULT_TAKE_OFF_ALTITUDE);
    }

    @Override
    public void sendTakingOffMessage(double desiredAltitude) {
        final TakeoffRequest takeoffRequest = srvTakeOff.newMessage();
        takeoffRequest.setAltitude(desiredAltitude);
        srvTakeOff.call(takeoffRequest, TakeoffServiceResponseListener.create());
    }

    private static final class TakeoffServiceResponseListener implements ServiceResponseListener<TakeoffResponse> {
        private TakeoffServiceResponseListener() {}

        public static TakeoffServiceResponseListener create() {
            return new TakeoffServiceResponseListener();
        }

        @Override
        public void onSuccess(TakeoffResponse takeoffResponse) {
            logger.info("Successfully took off!!!");
            logger.info(takeoffResponse.getStatus());
        }

        @Override
        public void onFailure(RemoteException e) {
            logger.info("Cannot send taking off message!!!", e);
        }
    }
}
