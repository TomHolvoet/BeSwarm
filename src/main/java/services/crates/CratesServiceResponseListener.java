package services.crates;

import org.ros.exception.RemoteException;
import org.ros.internal.message.Message;
import org.ros.node.service.ServiceResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author Hoang Tung Dinh
 */
final class CratesServiceResponseListener<T extends Message> implements ServiceResponseListener<T> {

    private static final Logger logger = LoggerFactory.getLogger(CratesServiceResponseListener.class);
    private final CountDownLatch countDownLatch;

    private CratesServiceResponseListener(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public static <T extends Message> CratesServiceResponseListener<T> create(CountDownLatch countDownLatch) {
        return new CratesServiceResponseListener<T>(countDownLatch);
    }

    @Override
    public void onSuccess(T t) {
        logger.info("Successfully sent message!!!");
        countDownLatch.countDown();
    }

    @Override
    public void onFailure(RemoteException e) {
        logger.info("Cannot send message!!!");
        countDownLatch.countDown();
    }
}