package applications.parrot.bebop.rats;

import com.google.auto.value.AutoValue;
import control.PidParameters;
import org.ros.node.parameter.ParameterTree;

/** @author Hoang Tung Dinh */
@AutoValue
abstract class RatsParameter {
  RatsParameter() {}

  static RatsParameter createFromParameterTree(ParameterTree parameterTree) {
    return builder()
        .absoluteTakeOffTimeInSecs(parameterTree.getDouble("~takeoff_time_in_secs"))
        .absoluteStartFlyingTimeInSecs(parameterTree.getDouble("~start_flying_time_in_secs"))
        .timeSyncTopic(parameterTree.getString("~time_sync_topic"))
        .pidLinearX(
            PidParameters.createUsingRosParams(
                parameterTree,
                "~pid_linear_x_kp",
                "~pid_linear_x_kd",
                "~pid_linear_x_ki",
                "~pid_lag_time_in_seconds"))
        .pidLinearY(
            PidParameters.createUsingRosParams(
                parameterTree,
                "~pid_linear_y_kp",
                "~pid_linear_y_kd",
                "~pid_linear_y_ki",
                "~pid_lag_time_in_seconds"))
        .pidLinearZ(
            PidParameters.createUsingRosParams(
                parameterTree,
                "~pid_linear_z_kp",
                "~pid_linear_z_kd",
                "~pid_linear_z_ki",
                "~pid_lag_time_in_seconds"))
        .pidAngularZ(
            PidParameters.createUsingRosParams(
                parameterTree,
                "~pid_angular_z_kp",
                "~pid_angular_z_kd",
                "~pid_angular_z_ki",
                "~pid_lag_time_in_seconds"))
        .droneName(parameterTree.getString("~drone_name"))
        .poseTopic(parameterTree.getString("~pose_topic"))
        .controlFrequencyInHz(parameterTree.getDouble("~control_frequency_in_hz"))
        .build();
  }

  private static Builder builder() {
    return new AutoValue_RatsParameter.Builder();
  }

  abstract double absoluteTakeOffTimeInSecs();

  abstract double absoluteStartFlyingTimeInSecs();

  abstract String timeSyncTopic();

  abstract PidParameters pidLinearX();

  abstract PidParameters pidLinearY();

  abstract PidParameters pidLinearZ();

  abstract PidParameters pidAngularZ();

  abstract String droneName();

  abstract String poseTopic();

  abstract double controlFrequencyInHz();

  @AutoValue.Builder
  public abstract static class Builder {
    abstract Builder absoluteTakeOffTimeInSecs(double value);

    abstract Builder absoluteStartFlyingTimeInSecs(double value);

    abstract Builder timeSyncTopic(String value);

    abstract Builder pidLinearX(PidParameters value);

    abstract Builder pidLinearY(PidParameters value);

    abstract Builder pidLinearZ(PidParameters value);

    abstract Builder pidAngularZ(PidParameters value);

    abstract Builder droneName(String value);

    abstract Builder poseTopic(String value);

    abstract Builder controlFrequencyInHz(double value);

    abstract RatsParameter build();
  }
}
