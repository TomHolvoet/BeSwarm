package virtualenvironment;

import control.dto.Pose;
import ilog.concert.IloException;
import solvers.Velocity4dCPVars;

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
  }

  private static void addHoverWhenTooCloseConstraints(CPInfo cpInfo, int cDistance)
      throws IloException {
    Velocity4dCPVars.setBoundary(
        cpInfo.velBody(), cDistance * cpInfo.minVelocity(), cDistance * cpInfo.maxVelocity());
  }

  private int computeCDistance(CPInfo cpInfo) {
    return Pose.computeEuclideanDistance(poseOfPheromoneDropper, cpInfo.currentPose())
            >= minimumDistance
        ? 1
        : 0;
  }
}
