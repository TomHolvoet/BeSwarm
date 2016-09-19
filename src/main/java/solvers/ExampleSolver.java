package solvers;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloModeler;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.List;

/** @author Hoang Tung Dinh */
public final class ExampleSolver {

  public static void main(String[] args) {
    System.load(
        "/Users/Tung/Applications/IBM/ILOG/CPLEX_Studio1263/cplex/bin/x86-64_osx"
            + "/libcplex1263.jnilib");
    try {
      final IloCplex model = new IloCplex();
      final IloNumVar[][] decisionVars = new IloNumVar[3][4];

      decisionVars[0] = createNumVarArray(new String[] {"ax", "bx", "cx", "dx"}, model);
      decisionVars[1] = createNumVarArray(new String[] {"ay", "by", "cy", "dy"}, model);
      decisionVars[2] = createNumVarArray(new String[] {"az", "bz", "cz", "dz"}, model);

      final List<Double> discreteTimeInSecs = createDiscreteTime();
      addVelocityConstraints(decisionVars, model, discreteTimeInSecs);
      addAccelerationConstraints(decisionVars, model, discreteTimeInSecs);
      addInitialPositionConstraints(decisionVars, model);
      addGoalPositionConstraints(decisionVars, model);
      addObjectiveFunction(decisionVars, model, discreteTimeInSecs);

      model.solve();

      model.output().println("Solution status = " + model.getStatus());
      model.output().println("Solution value  = " + model.getObjValue());

      printSolution(model.getValues(decisionVars[0]));
      printSolution(model.getValues(decisionVars[1]));
      printSolution(model.getValues(decisionVars[2]));

      model.end();

    } catch (IloException e) {
      System.err.println("Concert exception '" + e + "' caught");
    }
  }

  private static void printSolution(double[] values) {
    for (final double val : values) {
      System.out.print(val + " ");
    }
    System.out.println();
  }

  private static void addObjectiveFunction(
      IloNumVar[][] decisionVars, IloModeler model, Iterable<Double> discreteTimeInSecs)
      throws IloException {
    final IloLinearNumExpr objectiveExpression = model.linearNumExpr();
    for (final IloNumVar[] vars : decisionVars) {
      final IloNumVar a = vars[0];
      final IloNumVar b = vars[1];

      for (final double t : discreteTimeInSecs) {
        objectiveExpression.addTerm(6 * t, a);
        objectiveExpression.addTerm(2, b);
      }
    }
  }

  private static void addGoalPositionConstraints(IloNumVar[][] decisionVars, IloModeler model)
      throws IloException {
    final IloNumExpr posX = createPositionExpression(decisionVars[0], model);
    final IloNumExpr posY = createPositionExpression(decisionVars[1], model);
    final IloNumExpr posZ = createPositionExpression(decisionVars[2], model);

    model.addEq(posX, 2);
    model.addEq(posY, 2);
    model.addEq(posZ, 2);
  }

  private static IloNumExpr createPositionExpression(IloNumVar[] decisionVar, IloModeler model)
      throws IloException {
    final IloNumVar a = decisionVar[0];
    final IloNumVar b = decisionVar[1];
    final IloNumVar c = decisionVar[2];
    final IloNumVar d = decisionVar[3];

    return model.sum(a, b, c, d);
  }

  private static void addInitialPositionConstraints(IloNumVar[][] decisionVars, IloModeler model)
      throws IloException {
    final IloNumVar dx = decisionVars[0][3];
    final IloNumVar dy = decisionVars[1][3];
    final IloNumVar dz = decisionVars[2][3];

    model.addEq(dx, 0);
    model.addEq(dy, 0);
    model.addEq(dz, 0);
  }

  private static void addAccelerationConstraints(
      IloNumVar[][] decisionVars, IloCplex model, Iterable<Double> discreteTimeInSecs)
      throws IloException {
    for (final IloNumVar[] vars : decisionVars) {
      addAccConstraints(vars, model, discreteTimeInSecs);
    }
  }

  private static void addAccConstraints(
      IloNumVar[] vars, IloCplex model, Iterable<Double> discreteTimeInSecs) throws IloException {
    final IloNumVar a = vars[0];
    final IloNumVar b = vars[1];

    for (final double t : discreteTimeInSecs) {
      final IloNumExpr acceleration = model.sum(model.prod(6 * t, a), model.prod(2, b));
      model.addLe(acceleration, 3);
      model.addGe(acceleration, -3);
    }
  }

  private static void addVelocityConstraints(
      IloNumVar[][] decisionVars, IloModeler model, Iterable<Double> discreteTimeInSecs)
      throws IloException {
    for (final IloNumVar[] vars : decisionVars) {
      addVelConstraints(vars, model, discreteTimeInSecs);
    }
  }

  private static void addVelConstraints(
      IloNumVar[] vars, IloModeler model, Iterable<Double> discreteTimeInSecs) throws IloException {

    final IloNumVar a = vars[0];
    final IloNumVar b = vars[1];
    final IloNumVar c = vars[2];

    for (final double t : discreteTimeInSecs) {
      // 3at^2 + 2bt + c
      final IloNumExpr velocity = model.sum(model.prod(3 * t * t, a), model.prod(2 * t, b), c);
      model.addLe(velocity, 3);
      model.addGe(velocity, -3);
    }
  }

  private static List<Double> createDiscreteTime() {
    final List<Double> times = new ArrayList<>();
    for (int i = 0; i < 1001; i++) {
      times.add(i * 0.001);
    }

    return times;
  }

  private static IloNumVar[] createNumVarArray(String[] varNames, IloModeler model)
      throws IloException {
    return model.numVarArray(4, -Double.MAX_VALUE, Double.MAX_VALUE, varNames);
  }
}
