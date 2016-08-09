package services.crates;

import org.junit.Test;
import org.ros.node.service.ServiceClient;
import services.TakeOffService;

import static org.mockito.Mockito.mock;

/**
 * @author Hoang Tung Dinh
 */
public class CratesTakeOffServiceInvalidCustomizedAltitudeTest {
    private static final double invalidTakeOffAltitude = 2.9;

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCustomizedAltitude() {
        final TakeOffService cratesTakeOffService = CratesTakeOffService.create(
                mock(ServiceClient.class));
        cratesTakeOffService.sendTakingOffMessage(invalidTakeOffAltitude);
    }
}