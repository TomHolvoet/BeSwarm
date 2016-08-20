package services;

import control.dto.BodyFrameVelocity;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A decorator filter for the {@link BodyFrameVelocity} of a {@link Velocity4dService}. The filter
 * is to remove small noises when sending the velocity to the drone. If the difference between two
 * consecutive velocity values is insignificant, the second velocity value will be set to the first
 * velocity value.
 *
 * @author Hoang Tung Dinh
 */
public final class BodyFrameVelocityFilter implements Velocity4dService {

  private final Velocity4dService velocity4dService;
  private final double minimumDifference;
  @Nullable private BodyFrameVelocity lastBodyFrameVelocity;

  private BodyFrameVelocityFilter(Velocity4dService velocity4dService, double minimumDifference) {
    checkArgument(minimumDifference > 0, "minimumDifference must be positive.");
    this.velocity4dService = velocity4dService;
    this.minimumDifference = minimumDifference;
  }

  /**
   * Creates an instance of {@link BodyFrameVelocityFilter}.
   *
   * @param velocity4dService the velocity service to be filtered
   * @param minimumDifference the minimum difference between two consecutive velocity values
   * @return an instance of {@link BodyFrameVelocityFilter}
   */
  public static BodyFrameVelocityFilter create(
      Velocity4dService velocity4dService, double minimumDifference) {
    return new BodyFrameVelocityFilter(velocity4dService, minimumDifference);
  }

  @Override
  public void sendInertialFrameVelocity(InertialFrameVelocity inertialFrameVelocity, Pose pose) {
    velocity4dService.sendInertialFrameVelocity(inertialFrameVelocity, pose);
  }

  @Override
  public void sendBodyFrameVelocity(BodyFrameVelocity bodyFrameVelocity) {
    if (lastBodyFrameVelocity == null) {
      lastBodyFrameVelocity = bodyFrameVelocity;
      velocity4dService.sendBodyFrameVelocity(bodyFrameVelocity);
    } else {
      final BodyFrameVelocity filteredVelocity = filter(lastBodyFrameVelocity, bodyFrameVelocity);
      velocity4dService.sendBodyFrameVelocity(filteredVelocity);
      lastBodyFrameVelocity = filteredVelocity;
    }
  }

  private BodyFrameVelocity filter(
      BodyFrameVelocity lastVelocity, BodyFrameVelocity currentVelocity) {
    return Velocity.builder()
        .setLinearX(filter(lastVelocity.linearX(), currentVelocity.linearX()))
        .setLinearY(filter(lastVelocity.linearY(), currentVelocity.linearY()))
        .setLinearZ(filter(lastVelocity.linearZ(), currentVelocity.linearZ()))
        .setAngularZ(filter(lastVelocity.angularZ(), currentVelocity.angularZ()))
        .build();
  }

  private double filter(double lastVelocity, double currentVelocity) {
    if (currentVelocity > lastVelocity - minimumDifference
        && currentVelocity < lastVelocity + minimumDifference) {
      return lastVelocity;
    } else {
      return currentVelocity;
    }
  }
}
