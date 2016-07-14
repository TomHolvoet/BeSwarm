package services.crates;

import org.ros.internal.message.Message;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
public final class CratesUtilities {

    private static final Logger logger = LoggerFactory.getLogger(CratesUtilities.class);
    private static final long ROS_SERVICE_WAITING_TIME_IN_MILLISECONDS = 200;

    private CratesUtilities() {}

    public static <T extends Message, U extends Message> void sendRequest(ServiceClient<T, U> serviceClient, T
            request) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ServiceResponseListener<U> serviceResponseListener = CratesServiceResponseListener.create(
                countDownLatch);

        while (true) {
            serviceClient.call(request, serviceResponseListener);

            try {
                countDownLatch.await(ROS_SERVICE_WAITING_TIME_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.info("Waiting for response is interrupted.", e);
            }

            if (countDownLatch.getCount() == 0) {
                return;
            }
        }
    }
}
