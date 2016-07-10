package services.crates;

import hal_quadrotor.TakeoffRequest;
import services.TakeOffService;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.mockito.Mockito.verify;

/**
 * @author Hoang Tung Dinh
 */
public abstract class CratesTakeOffServiceValidCustomizedAltitudeTest extends CratesTakeOffServiceTest {
    private static final double validTakeOffAltitude = 3.0;

    @Override
    Future<?> sendMessage(final TakeOffService cratesTakeOffService) {
        return Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                cratesTakeOffService.sendTakingOffMessage(validTakeOffAltitude);
            }
        });
    }

    @Override
    void checkCorrectSentAltitude(TakeoffRequest takeoffRequest) {
        verify(takeoffRequest).setAltitude(validTakeOffAltitude);
    }
}
