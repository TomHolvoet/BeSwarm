package solvers;

import com.google.auto.value.AutoValue;
import ilog.concert.IloException;
import ilog.concert.IloModeler;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;

/** @author Hoang Tung Dinh */
@AutoValue
public abstract class Velocity4dCp<T extends IloNumExpr> {

  Velocity4dCp() {}

  public static <U extends IloNumExpr> Velocity4dCp<U> create(U velX, U velY, U velZ, U velYaw) {
    return new AutoValue_Velocity4dCp<>(velX, velY, velZ, velYaw);
  }

  public static Velocity4dCp<IloNumVar> createVariables(
      IloModeler model, double lowerBound, double upperBound) throws IloException {
    final IloNumVar[] vars = model.numVarArray(4, lowerBound, upperBound);
    return create(vars[0], vars[1], vars[2], vars[3]);
  }

  public static Velocity4dCp<IloNumExpr> createEmptyExpressions(IloModeler model)
      throws IloException {
    return create(model.numExpr(), model.numExpr(), model.numExpr(), model.numExpr());
  }

  public static Velocity4dCp<IloNumExpr> createFromExpressions(
      IloNumExpr velX, IloNumExpr velY, IloNumExpr velZ, IloNumExpr velYaw) {
    return create(velX, velY, velZ, velYaw);
  }

  public static void imposeBoundaryConstraint(
      IloModeler model,
      Velocity4dCp<? extends IloNumExpr> velocity,
      double lowerBound,
      double upperBound)
      throws IloException {
    addBoundaryConstraint(model, velocity.x(), lowerBound, upperBound);
    addBoundaryConstraint(model, velocity.y(), lowerBound, upperBound);
    addBoundaryConstraint(model, velocity.z(), lowerBound, upperBound);
    addBoundaryConstraint(model, velocity.yaw(), lowerBound, upperBound);
  }

  private static void addBoundaryConstraint(
      IloModeler model, IloNumExpr velocity, double lowerBound, double upperBound)
      throws IloException {
    model.addGe(velocity, lowerBound);
    model.addLe(velocity, upperBound);
  }

  public abstract T x();

  public abstract T y();

  public abstract T z();

  public abstract T yaw();
}
