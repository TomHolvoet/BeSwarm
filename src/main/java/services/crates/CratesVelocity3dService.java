package services.crates;

import hal_quadrotor.VelocityRequest;
import hal_quadrotor.VelocityResponse;
import org.ros.node.service.ServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Velocity3dService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
final class CratesVelocity3dService implements Velocity3dService {
    private static final Logger logger = LoggerFactory.getLogger(CratesVelocity3dService.class);
    private final ServiceClient<VelocityRequest, VelocityResponse> srvVelocity;

    private CratesVelocity3dService(ServiceClient<VelocityRequest, VelocityResponse> srvVelocity) {
        this.srvVelocity = srvVelocity;
    }

    public static CratesVelocity3dService create(ServiceClient<VelocityRequest, VelocityResponse> srvVelocity) {
        return new CratesVelocity3dService(srvVelocity);
    }

    @Override
    public void sendVelocity3dMessage(double inertialFrameVelocityX, double inertialFrameVelocityY,
            double inertialFrameVelocityZ, double angularPositionZ) {
        final VelocityRequest velocityRequest = srvVelocity.newMessage();
        velocityRequest.setDx(inertialFrameVelocityX);
        velocityRequest.setDy(inertialFrameVelocityY);
        velocityRequest.setDz(inertialFrameVelocityZ);
        velocityRequest.setYaw(angularPositionZ);
        logger.debug("Sending 3d velocity: [velX={} velY={} velZ={} posYaw={}]", inertialFrameVelocityX,
                inertialFrameVelocityY, inertialFrameVelocityZ, angularPositionZ);
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        while (true) {
            srvVelocity.call(velocityRequest, CratesServiceResponseListener.<VelocityResponse>create(countDownLatch));

            try {
                countDownLatch.await(CratesUtilities.ROS_SERVICE_WAITING_TIME_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.info("Waiting for inertialFrameVelocity response is interrupted.", e);
            }

            if (countDownLatch.getCount() == 0) {
                return;
            }
        }
    }
}
