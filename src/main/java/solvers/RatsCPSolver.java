package solvers;

import com.google.common.base.Optional;
import control.Pid4dParameters;
import control.dto.BodyFrameVelocity;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import ilog.concert.IloException;
import ilog.concert.IloModeler;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import utils.math.EulerAngle;

import static com.google.common.base.Preconditions.checkNotNull;

/** @author Hoang Tung Dinh */
public final class RatsCPSolver {
  private final IloNumVar velBodyX;
  private final IloNumVar velBodyY;
  private final IloNumVar velBodyZ;
  private final IloNumVar velBodyYaw;

  private final IloNumVar velRefX;
  private final IloNumVar velRefY;
  private final IloNumVar velRefZ;
  private final IloNumVar velRefYaw;

  private final IloNumVar velPidX;
  private final IloNumVar velPidY;
  private final IloNumVar velPidZ;
  private final IloNumVar velPidYaw;

  private final IloNumVar l1Norm;

  private final Pose currentPose;
  private final Pose desiredPose;
  private final InertialFrameVelocity currentRefVelocity;
  private final InertialFrameVelocity desiredRefVelocity;

  private final Pid4dParameters pid4dParameters;

  private static final double MAX_BODY_VEL = 1;
  private static final double MIN_BODY_VEL = -1;

  private final int poseValidSC;
  private final int onTrajectorySC;

  private final IloCplex model;

  private RatsCPSolver(Builder builder) throws IloException {
    this.currentPose = builder.currentPose;
    this.desiredPose = builder.desiredPose;
    this.currentRefVelocity = builder.currentRefVelocity;
    this.desiredRefVelocity = builder.desiredRefVelocity;
    this.pid4dParameters = builder.pid4dParameters;
    this.poseValidSC = builder.poseValid;
    this.onTrajectorySC = builder.onTrajectory;
    this.model = new IloCplex();
    model.setOut(null);

    // bound: [-1, 1]
    final IloNumVar[] bodyVelVars = initBodyVelocityVars(model, MIN_BODY_VEL, MAX_BODY_VEL);
    velBodyX = bodyVelVars[0];
    velBodyY = bodyVelVars[1];
    velBodyZ = bodyVelVars[2];
    velBodyYaw = bodyVelVars[3];

    // no bound
    final IloNumVar[] refVelVars = initBodyVelocityVars(model, -Double.MAX_VALUE, Double.MAX_VALUE);
    velRefX = refVelVars[0];
    velRefY = refVelVars[1];
    velRefZ = refVelVars[2];
    velRefYaw = refVelVars[3];

    // no bound
    final IloNumVar[] pidVelVars = initBodyVelocityVars(model, -Double.MAX_VALUE, Double.MAX_VALUE);
    velPidX = pidVelVars[0];
    velPidY = pidVelVars[1];
    velPidZ = pidVelVars[2];
    velPidYaw = pidVelVars[3];

    l1Norm = model.numVar(-Double.MAX_VALUE, Double.MAX_VALUE);
  }

  public static Builder builder() {
    return new Builder();
  }

  private static IloNumVar[] initBodyVelocityVars(
      IloModeler model, double lowerBound, double upperBound) throws IloException {
    return model.numVarArray(4, lowerBound, upperBound);
  }

  public Optional<BodyFrameVelocity> solve() throws IloException {
    buildModel();

    final boolean solvable = model.solve();
    if (solvable) {
      final double velX = model.getValue(velBodyX);
      final double velY = model.getValue(velBodyY);
      final double velZ = model.getValue(velBodyZ);
      final double velYaw = model.getValue(velBodyYaw);

      final BodyFrameVelocity resultingVelocity =
          Velocity.builder()
              .setLinearX(velX)
              .setLinearY(velY)
              .setLinearZ(velZ)
              .setAngularZ(velYaw)
              .build();
      model.end();
      return Optional.of(resultingVelocity);
    } else {
      model.end();
      return Optional.absent();
    }
  }

  private void buildModel() throws IloException {
    addReferenceVelocityConstraints();
    addL1NormConstraint();
    addLinearPidConstraints();
    addAngularPidConstraint();
    addHoverWhenPoseOutdatedConstraint();
    addHoverWhenOutOfTrajectoryConstraint();
    addL1NormObjectiveFunction();
  }

  private void addHoverWhenOutOfTrajectoryConstraint() throws IloException {
    setVelBodyBound(MIN_BODY_VEL * onTrajectorySC, MAX_BODY_VEL * onTrajectorySC);
  }

  private void addHoverWhenPoseOutdatedConstraint() throws IloException {
    setVelBodyBound(MIN_BODY_VEL * poseValidSC, MAX_BODY_VEL * poseValidSC);
  }

  private void setVelBodyBound(double lowerBound, double upperBound) throws IloException {
    velBodyX.setLB(lowerBound);
    velBodyX.setUB(upperBound);
    velBodyY.setLB(lowerBound);
    velBodyY.setUB(upperBound);
    velBodyZ.setLB(lowerBound);
    velBodyZ.setUB(upperBound);
    velBodyYaw.setLB(lowerBound);
    velBodyYaw.setUB(upperBound);
  }

  private void addReferenceVelocityConstraints() throws IloException {
    final double sin = StrictMath.sin(currentPose.yaw());
    final double cos = StrictMath.cos(currentPose.yaw());

    // Vrx = Vbx*cos(theta) - Vby*sin(theta)
    model.addEq(velRefX, model.diff(model.prod(velBodyX, cos), model.prod(velBodyY, sin)));
    // Vrx = Vbx*sin(theta) + Vby*cos(theta)
    model.addEq(velRefY, model.sum(model.prod(velBodyX, sin), model.prod(velBodyY, cos)));
    //Vrz = Vbz
    model.addEq(velRefZ, velBodyZ);
    // Vryaw = Vbyaw
    model.addEq(velRefYaw, velBodyYaw);
  }

  private void addL1NormConstraint() throws IloException {
    final IloNumVar deltaVelX = createAbsoluteVarConstraint(velRefX, velPidX);
    final IloNumVar deltaVelY = createAbsoluteVarConstraint(velRefY, velPidY);
    final IloNumVar deltaVelZ = createAbsoluteVarConstraint(velRefZ, velPidZ);
    final IloNumVar deltaVelYaw = createAbsoluteVarConstraint(velRefYaw, velPidYaw);
    model.addEq(l1Norm, model.sum(deltaVelX, deltaVelY, deltaVelZ, deltaVelYaw));
  }

  private IloNumVar createAbsoluteVarConstraint(IloNumExpr firstVar, IloNumExpr secondVar)
      throws IloException {
    // t
    final IloNumVar absVal = model.numVar(-Double.MAX_VALUE, Double.MAX_VALUE);
    // t >= v1 - v2
    model.addGe(absVal, model.diff(firstVar, secondVar));
    // t >= v2 - v1
    model.addGe(absVal, model.diff(secondVar, firstVar));
    return absVal;
  }

  private void addLinearPidConstraints() throws IloException {
    model.addEq(
        velPidX,
        pid4dParameters.linearX().kp() * (desiredPose.x() - currentPose.x())
            + pid4dParameters.linearX().kd()
                * (desiredRefVelocity.linearX() - currentRefVelocity.linearX()));
    model.addEq(
        velPidY,
        pid4dParameters.linearY().kp() * (desiredPose.y() - currentPose.y())
            + pid4dParameters.linearY().kd()
                * (desiredRefVelocity.linearY() - currentRefVelocity.linearY()));
    model.addEq(
        velPidZ,
        pid4dParameters.linearZ().kp() * (desiredPose.z() - currentPose.z())
            + pid4dParameters.linearZ().kd()
                * (desiredRefVelocity.linearZ() - currentRefVelocity.linearZ()));
  }

  private void addAngularPidConstraint() throws IloException {
    final double yawDifference =
        EulerAngle.computeAngleDistance(desiredPose.yaw(), currentPose.yaw());
    model.addEq(
        velPidYaw,
        pid4dParameters.angularZ().kp() * yawDifference
            + pid4dParameters.angularZ().kd()
                * (desiredRefVelocity.angularZ() - currentRefVelocity.angularZ()));
  }

  private void addL1NormObjectiveFunction() throws IloException {
    model.addMinimize(l1Norm);
  }

  /** {@code RatsCPSolver} builder static inner class. */
  public static final class Builder {
    private Pose currentPose;
    private Pose desiredPose;
    private InertialFrameVelocity currentRefVelocity;
    private InertialFrameVelocity desiredRefVelocity;
    private Integer poseValid;
    private Integer onTrajectory;
    private Pid4dParameters pid4dParameters;

    private Builder() {}

    /**
     * Sets the {@code currentPose} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code currentPose} to set
     * @return a reference to this Builder
     */
    public Builder withCurrentPose(Pose val) {
      currentPose = val;
      return this;
    }

    /**
     * Sets the {@code desiredPose} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code desiredPose} to set
     * @return a reference to this Builder
     */
    public Builder withDesiredPose(Pose val) {
      desiredPose = val;
      return this;
    }

    /**
     * Sets the {@code currentRefVelocity} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code currentRefVelocity} to set
     * @return a reference to this Builder
     */
    public Builder withCurrentRefVelocity(InertialFrameVelocity val) {
      currentRefVelocity = val;
      return this;
    }

    /**
     * Sets the {@code desiredRefVelocity} and returns a reference to this Builder so that the
     * methods can be chained together.
     *
     * @param val the {@code desiredRefVelocity} to set
     * @return a reference to this Builder
     */
    public Builder withDesiredRefVelocity(InertialFrameVelocity val) {
      desiredRefVelocity = val;
      return this;
    }

    /**
     * Sets the {@code poseValidSC} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code poseValidSC} to set
     * @return a reference to this Builder
     */
    public Builder withPoseValid(int val) {
      poseValid = val;
      return this;
    }

    /**
     * Sets the {@code onTrajectorySC} and returns a reference to this Builder so that the methods
     * can be chained together.
     *
     * @param val the {@code onTrajectorySC} to set
     * @return a reference to this Builder
     */
    public Builder withOnTrajectory(int val) {
      onTrajectory = val;
      return this;
    }

    /**
     * Sets the {@code pid4dParameters} and returns a reference to this Builder so that the methods
     * can be chained together.
     *
     * @param val the {@code pid4dParameters} to set
     * @return a reference to this Builder
     */
    public Builder withPid4dParameters(Pid4dParameters val) {
      pid4dParameters = val;
      return this;
    }

    /**
     * Returns a {@code RatsCPSolver} built from the parameters previously set.
     *
     * @return a {@code RatsCPSolver} built with parameters of this {@code RatsCPSolver.Builder}
     */
    public RatsCPSolver build() throws IloException {
      checkNotNull(currentPose);
      checkNotNull(desiredPose);
      checkNotNull(currentRefVelocity);
      checkNotNull(desiredRefVelocity);
      checkNotNull(poseValid);
      checkNotNull(onTrajectory);
      checkNotNull(pid4dParameters);
      return new RatsCPSolver(this);
    }
  }
}
