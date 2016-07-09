package services.crates;

import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import hal_quadrotor.VelocityRequest;
import hal_quadrotor.VelocityResponse;
import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.VelocityService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
final class CratesVelocityService implements VelocityService {
    private static final Logger logger = LoggerFactory.getLogger(CratesVelocityService.class);
    private final ServiceClient<VelocityRequest, VelocityResponse> srvVelocity;

    private CratesVelocityService(ServiceClient<VelocityRequest, VelocityResponse> srvVelocity) {
        this.srvVelocity = srvVelocity;
    }

    public static CratesVelocityService create(ServiceClient<VelocityRequest, VelocityResponse> srvVelocity) {
        return new CratesVelocityService(srvVelocity);
    }

    @Override
    public void sendVelocityMessage(InertialFrameVelocity inertialFrameVelocity, Pose pose) {
        final VelocityRequest velocityRequest = srvVelocity.newMessage();
        velocityRequest.setDx(inertialFrameVelocity.linearX());
        velocityRequest.setDy(inertialFrameVelocity.linearY());
        velocityRequest.setDz(inertialFrameVelocity.linearZ());
        velocityRequest.setYaw(inertialFrameVelocity.angularZ());
        logger.debug("Sending inertialFrameVelocity: [x={} y={} z={} yaw={}]", inertialFrameVelocity.linearX(),
                inertialFrameVelocity.linearY(), inertialFrameVelocity.linearZ(), inertialFrameVelocity.angularZ());
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        srvVelocity.call(velocityRequest, VelocityServiceResponseListener.create(countDownLatch));
        try {
            countDownLatch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.info("Waiting for inertialFrameVelocity response is interrupted.", e);
        }
    }

    private static final class VelocityServiceResponseListener implements ServiceResponseListener<VelocityResponse> {
        private final CountDownLatch countDownLatch;

        private VelocityServiceResponseListener(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        public static VelocityServiceResponseListener create(CountDownLatch countDownLatch) {
            return new VelocityServiceResponseListener(countDownLatch);
        }

        @Override
        public void onSuccess(VelocityResponse velocityResponse) {
            logger.info("Successfully sent velocity message!!!");
            logger.info(velocityResponse.getStatus());
            countDownLatch.countDown();
        }

        @Override
        public void onFailure(RemoteException e) {
            logger.info("Cannot send velocity message!!!", e);
            countDownLatch.countDown();
        }
    }
}
