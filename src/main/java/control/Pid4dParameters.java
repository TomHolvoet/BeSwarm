package control;

import com.google.auto.value.AutoValue;

/** @author Hoang Tung Dinh */
@AutoValue
public abstract class Pid4dParameters {

  Pid4dParameters() {}

  public static Pid4dParameters create(
      PidParameters linearX, PidParameters linearY, PidParameters linearZ, PidParameters angularZ) {
    return new AutoValue_Pid4dParameters(linearX, linearY, linearZ, angularZ);
  }

  public static Pid4dParameters createWithDefaultValues() {
    return create(
        DefaultPidParameters.LINEAR_X.getParameters(),
        DefaultPidParameters.LINEAR_Y.getParameters(),
        DefaultPidParameters.LINEAR_Z.getParameters(),
        DefaultPidParameters.ANGULAR_Z.getParameters());
  }

  public abstract PidParameters linearX();

  public abstract PidParameters linearY();

  public abstract PidParameters linearZ();

  public abstract PidParameters angularZ();
}
