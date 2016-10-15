package solvers;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloModeler;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * An example cplex solver.
 *
 * @author Hoang Tung Dinh
 */
public final class ExampleCplexSolver {

  private static final Logger logger = LoggerFactory.getLogger(ExampleCplexSolver.class);

  private ExampleCplexSolver() {}

  public static void main(String[] args) {
    // this one will not be used in the future. We will use LD_LIBRARY_PATH instead.
    System.load(
        "/Users/Tung/Applications/IBM/ILOG/CPLEX_Studio1263/cplex/bin/x86-64_osx"
            + "/libcplex1263.jnilib");
    try {
      long runningTime = 0;
      int counter = 0;
      for (int i = 0; i < 1000; i++) {
        final long startTime = System.nanoTime();
        final IloCplex model = new IloCplex();
        final IloNumVar[][] decisionVars = new IloNumVar[3][4];

        decisionVars[0] = createNumVarArray(new String[] {"ax", "bx", "cx", "dx"}, model);
        decisionVars[1] = createNumVarArray(new String[] {"ay", "by", "cy", "dy"}, model);
        decisionVars[2] = createNumVarArray(new String[] {"az", "bz", "cz", "dz"}, model);

        final List<Double> discreteTimeInSecs = createDiscreteTime();
        addVelocityConstraints(decisionVars, model, discreteTimeInSecs);
        addAccelerationConstraints(decisionVars, model, discreteTimeInSecs);
        addAvoidObstacleConstraints(decisionVars, model, discreteTimeInSecs);
        addInitialPositionConstraints(decisionVars, model);
        addGoalPositionConstraints(decisionVars, model);
        addObjectiveFunction(decisionVars, model, discreteTimeInSecs);

        model.setOut(null);
        model.solve();

        if (i > 10) {
          runningTime += System.nanoTime() - startTime;
          counter++;
        }

        model.output().println("Solution status = " + model.getStatus());
        model.output().println("Solution value  = " + model.getObjValue());

        printSolution(model.getValues(decisionVars[0]));
        printSolution(model.getValues(decisionVars[1]));
        printSolution(model.getValues(decisionVars[2]));

        model.end();
      }

      logger.info("Running time is: {}", (runningTime / (double) counter) / 1.0E9);

    } catch (IloException e) {
      logger.info("Concert exception caught", e);
    }
  }

  private static void addAvoidObstacleConstraints(
      IloNumVar[][] decisionVars, IloCplex model, Iterable<Double> discreteTimeInSecs)
      throws IloException {
    for (final double time : discreteTimeInSecs) {
      final IloNumExpr posX = createPositionExpression(decisionVars[0], model, time);
      final IloNumExpr posY = createPositionExpression(decisionVars[1], model, time);
      final IloNumExpr posZ = createPositionExpression(decisionVars[2], model, time);

      final IloConstraint c1 = model.ge(model.sum(0.5, model.prod(-1, posZ)), 0);
      final IloConstraint c2 = model.le(model.sum(1.5, model.prod(-1, posZ)), 0);
      final IloConstraint c3 = model.ge(model.sum(0.5, model.prod(-1, posX)), 0);
      final IloConstraint c4 = model.le(model.sum(1.5, model.prod(-1, posX)), 0);
      final IloConstraint c5 = model.ge(model.sum(0.5, model.prod(-1, posY)), 0);
      final IloConstraint c6 = model.le(model.sum(1.5, model.prod(-1, posY)), 0);
      model.add(model.or(new IloConstraint[] {c1, c2, c3, c4, c5, c6}));
    }
  }

  private static void printSolution(double[] values) {
    for (final double val : values) {
      logger.info(String.valueOf(val));
    }
  }

  private static void addObjectiveFunction(
      IloNumVar[][] decisionVars, IloCplex model, Iterable<Double> discreteTimeInSecs)
      throws IloException {
    final IloLinearNumExpr objectiveExpression = model.linearNumExpr();
    for (final IloNumVar[] vars : decisionVars) {
      final IloNumVar a = vars[0];
      final IloNumVar b = vars[1];

      for (final double t : discreteTimeInSecs) {
        // for the absolute value
        final IloNumVar u = model.numVar(-Double.MAX_VALUE, Double.MAX_VALUE);
        // expr: 6at + 2b
        final IloNumExpr expr = model.sum(model.prod(6 * t, a), model.prod(2, b));
        // constraint: u >= 6at + 2b
        model.addGe(u, expr);
        // constraint: u >= -(6at + 2b)
        model.addGe(u, model.prod(-1, expr));
        objectiveExpression.addTerm(1, u);
      }
    }

    model.addMinimize(objectiveExpression);
  }

  private static void addGoalPositionConstraints(IloNumVar[][] decisionVars, IloModeler model)
      throws IloException {
    final IloNumExpr posX = createPositionExpression(decisionVars[0], model, 1);
    final IloNumExpr posY = createPositionExpression(decisionVars[1], model, 1);
    final IloNumExpr posZ = createPositionExpression(decisionVars[2], model, 1);

    model.addEq(posX, 2);
    model.addEq(posY, 2);
    model.addEq(posZ, 2);
  }

  private static IloNumExpr createPositionExpression(
      IloNumVar[] decisionVar, IloModeler model, double time) throws IloException {
    final IloNumVar a = decisionVar[0];
    final IloNumVar b = decisionVar[1];
    final IloNumVar c = decisionVar[2];
    final IloNumVar d = decisionVar[3];

    // at^3 + bt^2 + ct + d
    return model.sum(
        model.prod(StrictMath.pow(time, 3), a),
        model.prod(StrictMath.pow(time, 2), b),
        model.prod(time, c),
        d);
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
      model.addLe(acceleration, 10);
      model.addGe(acceleration, -10);
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
      model.addLe(velocity, 10);
      model.addGe(velocity, -10);
    }
  }

  private static List<Double> createDiscreteTime() {
    final List<Double> times = new ArrayList<>();
    for (int i = 0; i < 101; i++) {
      times.add(i * 0.01);
    }

    return times;
  }

  private static IloNumVar[] createNumVarArray(String[] varNames, IloModeler model)
      throws IloException {
    return model.numVarArray(4, -Double.MAX_VALUE, Double.MAX_VALUE, varNames);
  }
}
