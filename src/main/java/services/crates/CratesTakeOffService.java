package services.crates;

import hal_quadrotor.TakeoffRequest;
import hal_quadrotor.TakeoffResponse;
import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.TakeOffService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
public final class CratesTakeOffService implements TakeOffService {
    private static final Logger logger = LoggerFactory.getLogger(CratesTakeOffService.class);
    private static final double DEFAULT_TAKE_OFF_ALTITUDE = 5;
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
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        srvTakeOff.call(takeoffRequest, TakeoffServiceResponseListener.<TakeoffResponse>create(countDownLatch));
        try {
            countDownLatch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.info("Waiting for taking off response is interrupted.", e);
        }
    }

    private static final class TakeoffServiceResponseListener implements ServiceResponseListener<TakeoffResponse> {
        private final CountDownLatch countDownLatch;

        private TakeoffServiceResponseListener(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        public static TakeoffServiceResponseListener create(CountDownLatch countDownLatch) {
            return new TakeoffServiceResponseListener(countDownLatch);
        }

        @Override
        public void onSuccess(TakeoffResponse takeoffResponse) {
            logger.info("Successfully took off!!!");
            logger.info(takeoffResponse.getStatus());
            countDownLatch.countDown();
        }

        @Override
        public void onFailure(RemoteException e) {
            logger.info("Cannot send taking off message!!!", e);
            countDownLatch.countDown();
        }
    }
}
