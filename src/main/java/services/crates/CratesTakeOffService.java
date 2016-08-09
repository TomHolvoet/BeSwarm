package services.crates;

import com.google.common.annotations.VisibleForTesting;
import hal_quadrotor.TakeoffRequest;
import hal_quadrotor.TakeoffResponse;
import org.ros.node.service.ServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.TakeOffService;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
final class CratesTakeOffService implements TakeOffService {
    private static final Logger logger = LoggerFactory.getLogger(CratesTakeOffService.class);
    private final ServiceClient<TakeoffRequest, TakeoffResponse> srvTakeOff;

    @VisibleForTesting static final double DEFAULT_TAKE_OFF_ALTITUDE = 5;

    private CratesTakeOffService(ServiceClient<TakeoffRequest, TakeoffResponse> srvTakeOff) {
        this.srvTakeOff = srvTakeOff;
    }

    /**
     * Creates a take off service for a drone in the crates simulator.
     *
     * @param srvTakeOff the service client connected to the take of rosservice
     * @return a take off service
     */
    public static CratesTakeOffService create(
            ServiceClient<TakeoffRequest, TakeoffResponse> srvTakeOff) {
        return new CratesTakeOffService(srvTakeOff);
    }

    @Override
    public void sendTakingOffMessage() {
        sendTakingOffMessage(DEFAULT_TAKE_OFF_ALTITUDE);
    }

    @Override
    public void sendTakingOffMessage(double desiredAltitude) {
        checkArgument(desiredAltitude >= 3,
                "Since the take off controller in the crates simulator uses 2.0 meters as the " +
                        "distance consider reached, the desired altitude must be at least " +
                        "3.0 meters, so that the drone can be at 1.0 meter at least.");
        logger.debug("Send taking off messages.");
        final TakeoffRequest takeoffRequest = srvTakeOff.newMessage();
        takeoffRequest.setAltitude(desiredAltitude);
        CratesUtilities.sendRequest(srvTakeOff, takeoffRequest);
    }
}
