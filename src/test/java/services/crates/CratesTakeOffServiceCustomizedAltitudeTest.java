package services.crates;

import hal_quadrotor.TakeoffRequest;
import services.TakeOffService;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.mockito.Mockito.verify;

/**
 * @author Hoang Tung Dinh
 */
public abstract class CratesTakeOffServiceCustomizedAltitudeTest extends CratesTakeOffServiceTest {
    private static final double takeOffAltitude = 1.0;

    @Override
    Future<?> sendMessage(final TakeOffService cratesTakeOffService) {
        return Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                cratesTakeOffService.sendTakingOffMessage(takeOffAltitude);
            }
        });
    }

    @Override
    void checkCorrectSentAltitude(TakeoffRequest takeoffRequest) {
        verify(takeoffRequest).setAltitude(takeOffAltitude);
    }
}
