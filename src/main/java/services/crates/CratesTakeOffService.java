package services.crates;

import com.google.common.annotations.VisibleForTesting;
import hal_quadrotor.TakeoffRequest;
import hal_quadrotor.TakeoffResponse;
import org.ros.node.service.ServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.TakeOffService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
final class CratesTakeOffService implements TakeOffService {
    private static final Logger logger = LoggerFactory.getLogger(CratesTakeOffService.class);
    private final ServiceClient<TakeoffRequest, TakeoffResponse> srvTakeOff;

    @VisibleForTesting static final double DEFAULT_TAKE_OFF_ALTITUDE = 5;

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
        logger.debug("Send taking off messages.");
        final TakeoffRequest takeoffRequest = srvTakeOff.newMessage();
        takeoffRequest.setAltitude(desiredAltitude);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CratesServiceResponseListener<TakeoffResponse> cratesServiceResponseListener =
                CratesServiceResponseListener
                .create(countDownLatch);

        while (true) {
            srvTakeOff.call(takeoffRequest, cratesServiceResponseListener);
            try {
                countDownLatch.await(CratesUtilities.ROS_SERVICE_WAITING_TIME_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.info("Waiting for taking off response is interrupted.", e);
            }

            if (countDownLatch.getCount() == 0) {
                return;
            }
        }
    }
}
