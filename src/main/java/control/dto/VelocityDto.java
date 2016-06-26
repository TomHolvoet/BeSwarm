package control.dto;

import com.google.common.annotations.VisibleForTesting;

/**
 * @author Hoang Tung Dinh
 */
@VisibleForTesting
public interface VelocityDto {
    double linearX();

    double linearY();

    double linearZ();

    double angularZ();
}
