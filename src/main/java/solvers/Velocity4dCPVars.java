package solvers;

import com.google.auto.value.AutoValue;
import ilog.concert.IloException;
import ilog.concert.IloModeler;
import ilog.concert.IloNumVar;

/** @author Hoang Tung Dinh */
@AutoValue
public abstract class Velocity4dCPVars {
  Velocity4dCPVars() {}

  public static Velocity4dCPVars create(
      IloNumVar velX, IloNumVar velY, IloNumVar velZ, IloNumVar velYaw) {
    return new AutoValue_Velocity4dCPVars(velX, velY, velZ, velYaw);
  }

  public static Velocity4dCPVars createFromModel(
      IloModeler model, double lowerBound, double upperBound) throws IloException {
    final IloNumVar[] vars = model.numVarArray(4, lowerBound, upperBound);
    return create(vars[0], vars[1], vars[2], vars[3]);
  }

  public abstract IloNumVar x();

  public abstract IloNumVar y();

  public abstract IloNumVar z();

  public abstract IloNumVar yaw();
}
