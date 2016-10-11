package virtualenvironment;

import com.google.auto.value.AutoValue;
import control.dto.Pose;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import solvers.Velocity4dCp;

/** @author Hoang Tung Dinh */
@AutoValue
public abstract class CpState {

  CpState() {}

  public static CpState create(
      IloCplex model,
      Pose currentPose,
      double controlRateInSecs,
      Velocity4dCp<IloNumExpr> velRef,
      Velocity4dCp<IloNumVar> velBody,
      double maxAcceleration,
      double minVelocity,
      double maxVelocity) {
    return new AutoValue_CpState(
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

  public abstract Velocity4dCp<IloNumExpr> velRef();

  public abstract Velocity4dCp<IloNumVar> velBody();

  public abstract double maxAcceleration();

  public abstract double minVelocity();

  public abstract double maxVelocity();
}
