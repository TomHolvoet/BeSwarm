package services.crates;

import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import hal_quadrotor.VelocityRequest;
import hal_quadrotor.VelocityResponse;
import org.ros.node.service.ServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Velocity4dService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
final class CratesVelocity4dService implements Velocity4dService {
    private static final Logger logger = LoggerFactory.getLogger(CratesVelocity4dService.class);
    private final ServiceClient<VelocityRequest, VelocityResponse> srvVelocity;

    private CratesVelocity4dService(ServiceClient<VelocityRequest, VelocityResponse> srvVelocity) {
        this.srvVelocity = srvVelocity;
    }

    public static CratesVelocity4dService create(ServiceClient<VelocityRequest, VelocityResponse> srvVelocity) {
        return new CratesVelocity4dService(srvVelocity);
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
