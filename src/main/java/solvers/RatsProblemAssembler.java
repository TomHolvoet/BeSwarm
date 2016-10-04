package solvers;

import com.google.common.base.Optional;
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
public final class RatsProblemAssembler {
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

  private final double kp;
  private final double kd;

  private final IloCplex model;

  private RatsProblemAssembler(Builder builder) throws IloException {

    this.currentPose = builder.currentPose;
    this.desiredPose = builder.desiredPose;
    this.currentRefVelocity = builder.currentRefVelocity;
    this.desiredRefVelocity = builder.desiredRefVelocity;
    this.kp = builder.kp;
    this.kd = builder.kd;
    this.model = new IloCplex();
    model.setOut(null);

    // bound: [-1, 1]
    final IloNumVar[] bodyVelVars =
        initBodyVelocityVars(model, -1 * builder.poseValid, 1 * builder.poseValid);
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

  public void buildModel() throws IloException {
    addReferenceVelocityConstraints();
    addL1NormConstraint();
    addLinearPidConstraints();
    addAngularPidConstraint();
    addL1NormObjectiveFunction();
  }

  public Optional<BodyFrameVelocity> solve() throws IloException {
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
        kp * (desiredPose.x() - currentPose.x())
            + kd * (desiredRefVelocity.linearX() - currentRefVelocity.linearX()));
    model.addEq(
        velPidY,
        kp * (desiredPose.y() - currentPose.y())
            + kd * (desiredRefVelocity.linearY() - currentRefVelocity.linearY()));
    model.addEq(
        velPidZ,
        kp * (desiredPose.z() - currentPose.z())
            + kd * (desiredRefVelocity.linearZ() - currentRefVelocity.linearZ()));
  }

  private void addAngularPidConstraint() throws IloException {
    final double yawDifference =
        EulerAngle.computeAngleDistance(desiredPose.yaw(), currentPose.yaw());
    model.addEq(
        velPidYaw,
        kp * yawDifference + kd * (desiredRefVelocity.angularZ() - currentRefVelocity.angularZ()));
  }

  private void addL1NormObjectiveFunction() throws IloException {
    model.addMinimize(l1Norm);
  }

  /** {@code RatsProblemAssembler} builder static inner class. */
  public static final class Builder {
    private Pose currentPose;
    private Pose desiredPose;
    private InertialFrameVelocity currentRefVelocity;
    private InertialFrameVelocity desiredRefVelocity;
    private double kp;
    private double kd;
    private int poseValid;

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
     * Sets the {@code kp} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param val the {@code kp} to set
     * @return a reference to this Builder
     */
    public Builder withKp(double val) {
      kp = val;
      return this;
    }

    /**
     * Sets the {@code kd} and returns a reference to this Builder so that the methods can be
     * chained together.
     *
     * @param val the {@code kd} to set
     * @return a reference to this Builder
     */
    public Builder withKd(double val) {
      kd = val;
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
     * Returns a {@code RatsProblemAssembler} built from the parameters previously set.
     *
     * @return a {@code RatsProblemAssembler} built with parameters of this {@code
     *     RatsProblemAssembler.Builder}
     */
    public RatsProblemAssembler build() throws IloException {
      return new RatsProblemAssembler(this);
    }
  }
}
