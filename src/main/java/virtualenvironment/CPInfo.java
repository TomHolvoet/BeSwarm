package virtualenvironment;

import com.google.auto.value.AutoValue;
import control.dto.Pose;
import ilog.cplex.IloCplex;
import solvers.Velocity4dCPVars;

/** @author Hoang Tung Dinh */
@AutoValue
public abstract class CPInfo {

  CPInfo() {}

  public static CPInfo create(
      IloCplex model,
      Pose currentPose,
      double controlRateInSecs,
      Velocity4dCPVars velRef,
      Velocity4dCPVars velBody,
      double maxAcceleration,
      double minVelocity,
      double maxVelocity) {
    return new AutoValue_CPInfo(
        model,
        currentPose,
        controlRateInSecs,
        velRef,
        velBody,
        maxAcceleration,
        minVelocity,
        maxVelocity);
  }

  public abstract IloCplex model();

  public abstract Pose currentPose();

  public abstract double controlRateInSecs();

  public abstract Velocity4dCPVars velRef();

  public abstract Velocity4dCPVars velBody();

  public abstract double maxAcceleration();

  public abstract double minVelocity();

  public abstract double maxVelocity();
}
