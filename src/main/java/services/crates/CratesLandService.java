package services.crates;

import hal_quadrotor.LandRequest;
import hal_quadrotor.LandResponse;
import org.ros.node.service.ServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.LandService;

/** @author Hoang Tung Dinh */
final class CratesLandService implements LandService {
  private static final Logger logger = LoggerFactory.getLogger(CratesLandService.class);
  private final ServiceClient<LandRequest, LandResponse> srvLand;

  private CratesLandService(ServiceClient<LandRequest, LandResponse> srvLand) {
    this.srvLand = srvLand;
  }

  /**
   * Creates that land service working with the Crates simulator.
   *
   * @param srvLand the ros service client for landing
   * @return the land service working with the crates simualtor
   */
  public static CratesLandService create(ServiceClient<LandRequest, LandResponse> srvLand) {
    return new CratesLandService(srvLand);
  }

  @Override
  public void sendLandingMessage() {
    final LandRequest landRequest = srvLand.newMessage();
    logger.debug("Send landing messages.");
    CratesUtilities.sendRequest(srvLand, landRequest);
  }
}
