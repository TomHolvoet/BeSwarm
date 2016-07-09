package services.crates;

import hal_quadrotor.TakeoffResponse;
import org.ros.node.service.ServiceResponseListener;

import static org.mockito.Mockito.mock;

/**
 * @author Hoang Tung Dinh
 */
public class CratesTakeOffServiceCustomizedAltitudeOnSuccessTest extends CratesTakeOffServiceCustomizedAltitudeTest {
    @Override
    void responseToMessage(ServiceResponseListener<TakeoffResponse> serviceResponseListener) {
        serviceResponseListener.onSuccess(mock(TakeoffResponse.class));
    }
}
