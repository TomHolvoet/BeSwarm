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
  private final Velocity4dCp<IloNumVar> velBody;
  private final IloNumExpr l1Norm;

  private static final double MAX_BODY_VEL = 1;
  private static final double MIN_BODY_VEL = -1;

  private final int poseValidSC;
  private final int onTrajectorySC;

  private final IloCplex model;

  private RatsCPSolver(Builder builder) throws IloException {
    final Pose currentPose = builder.currentPose;
    final Pose desiredPose = builder.desiredPose;
    final InertialFrameVelocity currentRefVelocity = builder.currentRefVelocity;
    final InertialFrameVelocity desiredRefVelocity = builder.desiredRefVelocity;
    final Pid4dParameters pid4dParameters = builder.pid4dParameters;
    this.poseValidSC = builder.poseValid;
    this.onTrajectorySC = builder.onTrajectory;
    this.model = new IloCplex();
    model.setOut(null);

    // bound: [-1, 1]
    velBody = Velocity4dCp.createVariables(model, MIN_BODY_VEL, MAX_BODY_VEL);
    final Velocity4dCp<IloNumExpr> velRef =
        initializeReferenceVelocityExpressions(model, currentPose, velBody);
    final Velocity velPid =
        initializePidVelocity(
            desiredPose, currentPose, currentRefVelocity, desiredRefVelocity, pid4dParameters);

    l1Norm = initializeL1NormExpression(model, velRef, velPid);
  }

  public static Builder builder() {
    return new Builder();
  }

  public Optional<BodyFrameVelocity> solve() throws IloException {
    buildModel();

    final boolean solvable = model.solve();
    if (solvable) {
      final double velX = model.getValue(velBody.x());
      final double velY = model.getValue(velBody.y());
      final double velZ = model.getValue(velBody.z());
      final double velYaw = model.getValue(velBody.yaw());

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
    addHoverWhenPoseOutdatedConstraint();
    addHoverWhenOutOfTrajectoryConstraint();
    addL1NormObjectiveFunction();
  }

  private void addHoverWhenOutOfTrajectoryConstraint() throws IloException {
    Velocity4dCp.setBoundary(
        velBody, MIN_BODY_VEL * onTrajectorySC, MAX_BODY_VEL * onTrajectorySC);
  }

  private void addHoverWhenPoseOutdatedConstraint() throws IloException {
    Velocity4dCp.setBoundary(velBody, MIN_BODY_VEL * poseValidSC, MAX_BODY_VEL * poseValidSC);
  }

  private static Velocity4dCp<IloNumExpr> initializeReferenceVelocityExpressions(
      IloModeler model, Pose currentPose, Velocity4dCp<IloNumVar> velBody) throws IloException {
    final double sin = StrictMath.sin(currentPose.yaw());
    final double cos = StrictMath.cos(currentPose.yaw());

    // Vrx = Vbx*cos(theta) - Vby*sin(theta)
    final IloNumExpr velRefX =
        model.diff(model.prod(velBody.x(), cos), model.prod(velBody.y(), sin));
    // Vrx = Vbx*sin(theta) + Vby*cos(theta)
    final IloNumExpr velRefY =
        model.sum(model.prod(velBody.x(), sin), model.prod(velBody.y(), cos));
    //Vrz = Vbz
    final IloNumExpr velRefZ = velBody.z();
    // Vryaw = Vbyaw
    final IloNumExpr velRefYaw = velBody.yaw();

    return Velocity4dCp.create(velRefX, velRefY, velRefZ, velRefYaw);
  }

  private static IloNumExpr initializeL1NormExpression(
      IloModeler model, Velocity4dCp<IloNumExpr> velRef, InertialFrameVelocity velPid)
      throws IloException {
    final IloNumVar deltaVelX = createAbsoluteVarConstraint(model, velRef.x(), velPid.linearX());
    final IloNumVar deltaVelY = createAbsoluteVarConstraint(model, velRef.y(), velPid.linearY());
    final IloNumVar deltaVelZ = createAbsoluteVarConstraint(model, velRef.z(), velPid.linearZ());
    final IloNumVar deltaVelYaw =
        createAbsoluteVarConstraint(model, velRef.yaw(), velPid.angularZ());
    return model.sum(deltaVelX, deltaVelY, deltaVelZ, deltaVelYaw);
  }

  private static IloNumVar createAbsoluteVarConstraint(
      IloModeler model, IloNumExpr firstVar, double secondVar) throws IloException {
    // t
    final IloNumVar absVal = model.numVar(-Double.MAX_VALUE, Double.MAX_VALUE);
    // t >= v1 - v2
    model.addGe(absVal, model.diff(firstVar, secondVar));
    // t >= v2 - v1
    model.addGe(absVal, model.diff(secondVar, firstVar));
    return absVal;
  }

  private static Velocity initializePidVelocity(
      Pose desiredPose,
      Pose currentPose,
      InertialFrameVelocity currentRefVelocity,
      InertialFrameVelocity desiredRefVelocity,
      Pid4dParameters pid4dParameters) {
    final double velX =
        pid4dParameters.linearX().kp() * (desiredPose.x() - currentPose.x())
            + pid4dParameters.linearX().kd()
                * (desiredRefVelocity.linearX() - currentRefVelocity.linearX());
    final double velY =
        pid4dParameters.linearY().kp() * (desiredPose.y() - currentPose.y())
            + pid4dParameters.linearY().kd()
                * (desiredRefVelocity.linearY() - currentRefVelocity.linearY());
    final double velZ =
        pid4dParameters.linearZ().kp() * (desiredPose.z() - currentPose.z())
            + pid4dParameters.linearZ().kd()
                * (desiredRefVelocity.linearZ() - currentRefVelocity.linearZ());

    final double yawDifference =
        EulerAngle.computeAngleDistance(desiredPose.yaw(), currentPose.yaw());
    final double velYaw =
        pid4dParameters.angularZ().kp() * yawDifference
            + pid4dParameters.angularZ().kd()
                * (desiredRefVelocity.angularZ() - currentRefVelocity.angularZ());

    return Velocity.builder()
        .setLinearX(velX)
        .setLinearY(velY)
        .setLinearZ(velZ)
        .setAngularZ(velYaw)
        .build();
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
