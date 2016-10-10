package solvers;

import com.google.auto.value.AutoValue;
import ilog.concert.IloException;
import ilog.concert.IloModeler;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;

/** @author Hoang Tung Dinh */
@AutoValue
public abstract class Velocity4dCPVars<T extends IloNumExpr> {

  Velocity4dCPVars() {}

  public static <U extends IloNumExpr> Velocity4dCPVars<U> create(
      U velX, U velY, U velZ, U velYaw) {
    return new AutoValue_Velocity4dCPVars<>(velX, velY, velZ, velYaw);
  }

  public static Velocity4dCPVars<IloNumVar> createVariables(
      IloModeler model, double lowerBound, double upperBound) throws IloException {
    final IloNumVar[] vars = model.numVarArray(4, lowerBound, upperBound);
    return create(vars[0], vars[1], vars[2], vars[3]);
  }

  public static Velocity4dCPVars<IloNumExpr> createEmptyExpressions(IloModeler model)
      throws IloException {
    return create(model.numExpr(), model.numExpr(), model.numExpr(), model.numExpr());
  }

  public static Velocity4dCPVars<IloNumExpr> createFromExpressions(
      IloNumExpr velX, IloNumExpr velY, IloNumExpr velZ, IloNumExpr velYaw) {
    return create(velX, velY, velZ, velYaw);
  }

  public static void setBoundary(
      Velocity4dCPVars<IloNumVar> velocity, double lowerBound, double upperBound)
      throws IloException {
    velocity.x().setLB(lowerBound);
    velocity.x().setUB(upperBound);
    velocity.y().setLB(lowerBound);
    velocity.y().setUB(upperBound);
    velocity.z().setLB(lowerBound);
    velocity.z().setUB(upperBound);
    velocity.yaw().setLB(lowerBound);
    velocity.yaw().setUB(upperBound);
  }

  public abstract T x();

  public abstract T y();

  public abstract T z();

  public abstract T yaw();
}
