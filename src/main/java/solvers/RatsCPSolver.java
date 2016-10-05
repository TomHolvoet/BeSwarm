package solvers;

import com.google.common.base.Optional;
import control.PidParameters;
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

  private final PidParameters pidLinearX;
  private final PidParameters pidLinearY;
  private final PidParameters pidLinearZ;
  private final PidParameters pidAngularZ;

  private static final double MAX_BODY_VEL = 1;
  private static final double MIN_BODY_VEL = -1;

  private final int poseValid;

  private final IloCplex model;

  private RatsCPSolver(Builder builder) throws IloException {
    this.currentPose = builder.currentPose;
    this.desiredPose = builder.desiredPose;
    this.currentRefVelocity = builder.currentRefVelocity;
    this.desiredRefVelocity = builder.desiredRefVelocity;
    this.pidLinearX = builder.pidLinearX;
    this.pidLinearY = builder.pidLinearY;
    this.pidLinearZ = builder.pidLinearZ;
    this.pidAngularZ = builder.pidAngularZ;
    this.poseValid = builder.poseValid;
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
    addL1NormObjectiveFunction();
  }

  private void addHoverWhenPoseOutdatedConstraint() throws IloException {
    velBodyX.setLB(MIN_BODY_VEL * poseValid);
    velBodyX.setUB(MAX_BODY_VEL * poseValid);
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
        pidLinearX.kp() * (desiredPose.x() - currentPose.x())
            + pidLinearX.kd() * (desiredRefVelocity.linearX() - currentRefVelocity.linearX()));
    model.addEq(
        velPidY,
        pidLinearY.kp() * (desiredPose.y() - currentPose.y())
            + pidLinearY.kd() * (desiredRefVelocity.linearY() - currentRefVelocity.linearY()));
    model.addEq(
        velPidZ,
        pidLinearZ.kp() * (desiredPose.z() - currentPose.z())
            + pidLinearZ.kd() * (desiredRefVelocity.linearZ() - currentRefVelocity.linearZ()));
  }

  private void addAngularPidConstraint() throws IloException {
    final double yawDifference =
        EulerAngle.computeAngleDistance(desiredPose.yaw(), currentPose.yaw());
    model.addEq(
        velPidYaw,
        pidAngularZ.kp() * yawDifference
            + pidAngularZ.kd() * (desiredRefVelocity.angularZ() - currentRefVelocity.angularZ()));
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
    private int poseValid;
    private PidParameters pidLinearX;
    private PidParameters pidLinearY;
    private PidParameters pidLinearZ;
    private PidParameters pidAngularZ;

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
     * Sets the {@code poseValid} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param val the {@code poseValid} to set
     * @return a reference to this Builder
     */
    public Builder withPoseValid(int val) {
      poseValid = val;
      return this;
    }

    /**
     * Sets the {@code pidLinearX} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code pidLinearX} to set
     * @return a reference to this Builder
     */
    public Builder withPidLinearX(PidParameters val) {
      pidLinearX = val;
      return this;
    }

    /**
     * Sets the {@code pidLinearY} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code pidLinearY} to set
     * @return a reference to this Builder
     */
    public Builder withPidLinearY(PidParameters val) {
      pidLinearY = val;
      return this;
    }

    /**
     * Sets the {@code pidLinearZ} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code pidLinearZ} to set
     * @return a reference to this Builder
     */
    public Builder withPidLinearZ(PidParameters val) {
      pidLinearZ = val;
      return this;
    }

    /**
     * Sets the {@code pidAngularZ} and returns a reference to this Builder so that the methods can
     * be chained together.
     *
     * @param val the {@code pidAngularZ} to set
     * @return a reference to this Builder
     */
    public Builder withPidAngularZ(PidParameters val) {
      pidAngularZ = val;
      return this;
    }

    /**
     * Returns a {@code RatsCPSolver} built from the parameters previously set.
     *
     * @return a {@code RatsCPSolver} built with parameters of this {@code
     *     RatsCPSolver.Builder}
     */
    public RatsCPSolver build() throws IloException {
      return new RatsCPSolver(this);
    }
  }
}
