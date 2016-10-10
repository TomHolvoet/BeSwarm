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
public final class KeepDistanceCGP implements ConstraintGeneratorPheromone {

  private final double lifeTimeInSecs;
  private final Pose poseOfPheromoneDropper;
  private final double minimumDistance;

  private KeepDistanceCGP(
      double lifeTimeInSecs, Pose poseOfPheromoneDropper, double minimumDistance) {
    checkArgument(minimumDistance > 0, "Minimum distance must be positive.");
    this.lifeTimeInSecs = lifeTimeInSecs;
    this.poseOfPheromoneDropper = poseOfPheromoneDropper;
    this.minimumDistance = minimumDistance;
  }

  public static KeepDistanceCGP create(
      double lifeTimeInSecs, Pose poseOfPheromoneDropper, double minimumDistance) {
    return new KeepDistanceCGP(lifeTimeInSecs, poseOfPheromoneDropper, minimumDistance);
  }

  @Override
  public double lifeTimeInSecs() {
    return lifeTimeInSecs;
  }

  @Override
  public void addConstraints(CPInfo cpInfo) throws IloException {
    final int cDistance = computeCDistance(cpInfo);
    addHoverWhenTooCloseConstraints(cpInfo, cDistance);
    addKeepDistanceConstraints(cpInfo, cDistance);
  }

  private void addKeepDistanceConstraints(CPInfo cpInfo, int cDistance) throws IloException {
    final PoseCpExpr futurePose = computeFuturePose(cpInfo);
    final PlaneScalarCoeffs planeCoeffs = computePlaneCoeffs(cpInfo);
    final IloNumExpr distanceToPlane = computeDistanceExpr(cpInfo.model(), planeCoeffs, futurePose);
    cpInfo.model().addGe(distanceToPlane, cDistance * minimumDistance);
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

  private PlaneScalarCoeffs computePlaneCoeffs(CPInfo cpInfo) {
    final double coeffA = cpInfo.currentPose().x() - poseOfPheromoneDropper.x();
    final double coeffB = cpInfo.currentPose().y() - poseOfPheromoneDropper.y();
    final double coeffC = cpInfo.currentPose().z() - poseOfPheromoneDropper.z();
    final double coeffD =
        -coeffA * poseOfPheromoneDropper.x()
            - coeffB * poseOfPheromoneDropper.y()
            - coeffC * poseOfPheromoneDropper.z();
    return PlaneScalarCoeffs.create(coeffA, coeffB, coeffC, coeffD);
  }

  private static PoseCpExpr computeFuturePose(CPInfo cpInfo) throws IloException {
    final Pose currentPose = cpInfo.currentPose();
    final IloCplex model = cpInfo.model();
    final Velocity4dCp velRef = cpInfo.velRef();
    final double timeDelta = cpInfo.controlRateInSecs();

    final IloNumExpr futureX = model.sum(currentPose.x(), model.prod(velRef.x(), timeDelta));
    final IloNumExpr futureY = model.sum(currentPose.y(), model.prod(velRef.y(), timeDelta));
    final IloNumExpr futureZ = model.sum(currentPose.z(), model.prod(velRef.z(), timeDelta));
    final IloNumExpr futureYaw = model.sum(currentPose.yaw(), model.prod(velRef.yaw(), timeDelta));

    return PoseCpExpr.create(futureX, futureY, futureZ, futureYaw);
  }

  private static void addHoverWhenTooCloseConstraints(CPInfo cpInfo, int cDistance)
      throws IloException {
    Velocity4dCp.setBoundary(
        cpInfo.velBody(), cDistance * cpInfo.minVelocity(), cDistance * cpInfo.maxVelocity());
  }

  private int computeCDistance(CPInfo cpInfo) {
    return Pose.computeEuclideanDistance(poseOfPheromoneDropper, cpInfo.currentPose())
            >= minimumDistance
        ? 1
        : 0;
  }

  @AutoValue
  abstract static class PoseCpExpr {

    PoseCpExpr() {}

    public static PoseCpExpr create(IloNumExpr x, IloNumExpr y, IloNumExpr z, IloNumExpr yaw) {
      return new AutoValue_KeepDistanceCGP_PoseCpExpr(x, y, z, yaw);
    }

    abstract IloNumExpr x();

    abstract IloNumExpr y();

    abstract IloNumExpr z();

    abstract IloNumExpr yaw();
  }
}
