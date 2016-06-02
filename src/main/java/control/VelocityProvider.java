package control;

import bebopbehavior.Velocity;
import com.google.common.base.Optional;

/**
 * @author Hoang Tung Dinh
 */
public interface VelocityProvider {
    Optional<Velocity> getCurrentVelocity();
}
