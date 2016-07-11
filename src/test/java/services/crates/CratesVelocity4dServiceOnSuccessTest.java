package services.crates;

import hal_quadrotor.VelocityResponse;
import org.ros.node.service.ServiceResponseListener;

import static org.mockito.Mockito.mock;

/**
 * @author Hoang Tung Dinh
 */
public class CratesVelocity4dServiceOnSuccessTest extends CratesVelocity4dServiceTest {
    @Override
    void responseToMessage(ServiceResponseListener<VelocityResponse> serviceResponseListener) {
        serviceResponseListener.onSuccess(mock(VelocityResponse.class));
    }
}
