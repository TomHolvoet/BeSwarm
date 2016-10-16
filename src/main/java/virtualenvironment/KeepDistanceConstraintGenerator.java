package virtualenvironment;

import com.google.auto.value.AutoValue;
import control.dto.Pose;
import geom.PlaneScalarCoeffs;
import ilog.concert.IloException;
import ilog.concert.IloModeler;
import ilog.concert.IloNumExpr;
import ilog.cplex.IloCplex;
import solvers.Velocity4dCp;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A constraint generator who responsible for generating "hover when distance is too close" and
 * "keep distance" between two drones constraints.
 *
 * @author Hoang Tung Dinh
 */
public final class KeepDistanceConstraintGenerator implements ConstraintGenerator {

  private final Pose poseOfPheromoneDropper;
  private final double minimumDistance;

  private KeepDistanceConstraintGenerator(
      double lifeTimeInSecs, Pose poseOfPheromoneDropper, double minimumDistance) {
    checkArgument(minimumDistance > 0, "Minimum distance must be positive.");
    this.poseOfPheromoneDropper = poseOfPheromoneDropper;
    this.minimumDistance = minimumDistance;
  }

  public static KeepDistanceConstraintGenerator create(
      double lifeTimeInSecs, Pose poseOfPheromoneDropper, double minimumDistance) {
    return new KeepDistanceConstraintGenerator(
        lifeTimeInSecs, poseOfPheromoneDropper, minimumDistance);
  }

  @Override
  public void getConstraints(CpState cpState) throws IloException {
    final int cDistance = computeCDistance(cpState);
    addHoverWhenTooCloseConstraints(cpState, cDistance);
    addKeepDistanceConstraints(cpState, cDistance);
  }

  private void addKeepDistanceConstraints(CpState cpState, int cDistance) throws IloException {
    final PoseCpExpr futurePose = computeFuturePose(cpState);
    final PlaneScalarCoeffs planeCoeffs = computePlaneCoeffs(cpState);
    final IloNumExpr distanceToPlane =
        computeDistanceExpr(cpState.model(), planeCoeffs, futurePose);
    cpState.model().addGe(distanceToPlane, cDistance * minimumDistance);
  }

  private static IloNumExpr computeDistanceExpr(
      IloModeler model, PlaneScalarCoeffs planeCoeffs, PoseCpExpr futurePose) throws IloException {
    final IloNumExpr numerator =
        model.sum(
            model.sum(
                model.prod(planeCoeffs.a(), futurePose.x()),
                model.prod(planeCoeffs.b(), futurePose.y()),
                model.prod(planeCoeffs.c(), futurePose.z())),
            planeCoeffs.d());

    final double denominator =
        StrictMath.sqrt(
            planeCoeffs.a() * planeCoeffs.a()
                + planeCoeffs.b() * planeCoeffs.b()
                + planeCoeffs.c() * planeCoeffs.c());
    return model.prod(numerator, 1 / denominator);
  }

  private PlaneScalarCoeffs computePlaneCoeffs(CpState cpState) {
    final double coeffA = cpState.currentPose().x() - poseOfPheromoneDropper.x();
    final double coeffB = cpState.currentPose().y() - poseOfPheromoneDropper.y();
    final double coeffC = cpState.currentPose().z() - poseOfPheromoneDropper.z();
    final double coeffD =
        -coeffA * poseOfPheromoneDropper.x()
            - coeffB * poseOfPheromoneDropper.y()
            - coeffC * poseOfPheromoneDropper.z();
    return PlaneScalarCoeffs.create(coeffA, coeffB, coeffC, coeffD);
  }

  private static PoseCpExpr computeFuturePose(CpState cpState) throws IloException {
    final Pose currentPose = cpState.currentPose();
    final IloCplex model = cpState.model();
    final Velocity4dCp velRef = cpState.velRef();
    final double timeDelta = cpState.controlRateInSecs();

    final IloNumExpr futureX = model.sum(currentPose.x(), model.prod(velRef.x(), timeDelta));
    final IloNumExpr futureY = model.sum(currentPose.y(), model.prod(velRef.y(), timeDelta));
    final IloNumExpr futureZ = model.sum(currentPose.z(), model.prod(velRef.z(), timeDelta));
    final IloNumExpr futureYaw = model.sum(currentPose.yaw(), model.prod(velRef.yaw(), timeDelta));

    return PoseCpExpr.create(futureX, futureY, futureZ, futureYaw);
  }

  private static void addHoverWhenTooCloseConstraints(CpState cpState, int cDistance)
      throws IloException {
    Velocity4dCp.imposeBoundaryConstraint(
        cpState.model(),
        cpState.velBody(),
        cDistance * cpState.minVelocity(),
        cDistance * cpState.maxVelocity());
  }

  private int computeCDistance(CpState cpState) {
    return Pose.computeEuclideanDistance(poseOfPheromoneDropper, cpState.currentPose())
            >= minimumDistance
        ? 1
        : 0;
  }

  @AutoValue
  abstract static class PoseCpExpr {

    PoseCpExpr() {}

    public static PoseCpExpr create(IloNumExpr x, IloNumExpr y, IloNumExpr z, IloNumExpr yaw) {
      return new AutoValue_KeepDistanceConstraintGenerator_PoseCpExpr(x, y, z, yaw);
    }

    abstract IloNumExpr x();

    abstract IloNumExpr y();

    abstract IloNumExpr z();

    abstract IloNumExpr yaw();
  }
}
