package services.crates;

import hal_quadrotor.VelocityResponse;
import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceResponseListener;

import static org.mockito.Mockito.mock;

/**
 * @author Hoang Tung Dinh
 */
public class CratesVelocityServiceOnFailureTest extends CratesVelocityServiceTest {
    @Override
    void responseToMessage(ServiceResponseListener<VelocityResponse> serviceResponseListener) {
        serviceResponseListener.onFailure(mock(RemoteException.class));
    }
}
