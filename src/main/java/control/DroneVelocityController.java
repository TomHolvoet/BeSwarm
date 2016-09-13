package control;

import applications.trajectory.TrajectoryUtils;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A four-dimensional PID controller for the drone. It is the composition of 4 one-dimensional PID
 * controllers {@link LinearPidController1d} (three controllers for the three linear velocities, one
 * controller for the angular velocity).
 *
 * @author Hoang Tung Dinh
 */
public final class DroneVelocityController implements VelocityController4d {

  private final VelocityController1d controllerLinearX;
  private final VelocityController1d controllerLinearY;
  private final VelocityController1d controllerLinearZ;
  private final VelocityController1d controllerAngularZ;

  private DroneVelocityController(
      VelocityController1d controllerLinearX,
      VelocityController1d controllerLinearY,
      VelocityController1d controllerLinearZ,
      VelocityController1d controllerAngularZ) {
    this.controllerLinearX = controllerLinearX;
    this.controllerLinearY = controllerLinearY;
    this.controllerLinearZ = controllerLinearZ;
    this.controllerAngularZ = controllerAngularZ;
  }

  /**
   * Creates a {@link PidBuilder} of this class. This builder takes a {@link Trajectory4d} and four
   * {@link PidParameters} as arguments. It then creates four pid controller, one for each degree of
   * freedom of the drone.
   *
   * @return a {@link PidBuilder} instance
   */
  public static PidBuilder pidBuilder() {
    return new PidBuilder();
  }

  /**
   * Creates a {@link CompositionBuilder} of this class. This builder takes four {@link
   * VelocityController1d} as arguments.
   *
   * @return a {@link CompositionBuilder} instance
   */
  public static CompositionBuilder compositionBuilder() {
    return new CompositionBuilder();
  }

  /**
   * Compute the next velocity (response) of the control loop.
   *
   * @param currentPose the current pose of the drone
   * @param currentVelocity the current velocity of the drone
   * @param currentTimeInSeconds the current time which will be used to get the desired position of
   *     the drone
   * @return the next velocity (response) of the drone
   */
  @Override
  public InertialFrameVelocity computeNextResponse(
      Pose currentPose, InertialFrameVelocity currentVelocity, double currentTimeInSeconds) {
    final double linearX =
        controllerLinearX.computeNextResponse(
            currentPose.x(), currentVelocity.linearX(), currentTimeInSeconds);
    final double linearY =
        controllerLinearY.computeNextResponse(
            currentPose.y(), currentVelocity.linearY(), currentTimeInSeconds);
    final double linearZ =
        controllerLinearZ.computeNextResponse(
            currentPose.z(), currentVelocity.linearZ(), currentTimeInSeconds);
    final double angularZ =
        controllerAngularZ.computeNextResponse(
            currentPose.yaw(), currentVelocity.angularZ(), currentTimeInSeconds);

    return Velocity.builder()
        .setLinearX(linearX)
        .setLinearY(linearY)
        .setLinearZ(linearZ)
        .setAngularZ(angularZ)
        .build();
  }

  /** {@code DroneVelocityController} builder static inner class. */
  public static final class PidBuilder {
    private PidParameters linearXParameters;
    private PidParameters linearYParameters;
    private PidParameters linearZParameters;
    private PidParameters angularZParameters;
    private Trajectory4d trajectory4d;

    private PidBuilder() {}

    /**
     * Sets the {@code withTrajectory4d} and returns a reference to this PidBuilder so that the
     * methods can be chained together.
     *
     * @param val the {@code withTrajectory4d} to set
     * @return a reference to this PidBuilder
     */
    public PidBuilder withTrajectory4d(final Trajectory4d val) {
      trajectory4d = val;
      return this;
    }

    /**
     * Sets the {@code withLinearXParameters} and returns a reference to this PidBuilder so that the
     * methods can be chained together.
     *
     * @param val the {@code withLinearXParameters} to set
     * @return a reference to this PidBuilder
     */
    public PidBuilder withLinearXParameters(PidParameters val) {
      linearXParameters = val;
      return this;
    }

    /**
     * Sets the {@code withLinearYParameters} and returns a reference to this PidBuilder so that the
     * methods can be chained together.
     *
     * @param val the {@code withLinearYParameters} to set
     * @return a reference to this PidBuilder
     */
    public PidBuilder withLinearYParameters(PidParameters val) {
      linearYParameters = val;
      return this;
    }

    /**
     * Sets the {@code withLinearZParameters} and returns a reference to this PidBuilder so that the
     * methods can be chained together.
     *
     * @param val the {@code withLinearZParameters} to set
     * @return a reference to this PidBuilder
     */
    public PidBuilder withLinearZParameters(PidParameters val) {
      linearZParameters = val;
      return this;
    }

    /**
     * Sets the {@code withAngularZParameters} and returns a reference to this PidBuilder so that
     * the methods can be chained together.
     *
     * @param val the {@code withAngularZParameters} to set
     * @return a reference to this PidBuilder
     */
    public PidBuilder withAngularZParameters(PidParameters val) {
      angularZParameters = val;
      return this;
    }

    /**
     * Returns a {@code DroneVelocityController} built from the parameters previously set.
     *
     * @return a {@code DroneVelocityController} built with parameters of this {@code
     *     DroneVelocityController.Builder}
     */
    public DroneVelocityController build() {
      checkNotNull(linearXParameters, "missing withLinearXParameters");
      checkNotNull(linearYParameters, "missing withLinearYParameters");
      checkNotNull(linearZParameters, "missing withLinearZParameters");
      checkNotNull(angularZParameters, "missing withAngularZParameters");
      checkNotNull(trajectory4d, "missing withTrajectory4d");

      final Trajectory1d linearTrajectoryX = TrajectoryUtils.getTrajectoryLinearX(trajectory4d);
      final Trajectory1d linearTrajectoryY = TrajectoryUtils.getTrajectoryLinearY(trajectory4d);
      final Trajectory1d linearTrajectoryZ = TrajectoryUtils.getTrajectoryLinearZ(trajectory4d);
      final Trajectory1d angularTrajectoryZ = TrajectoryUtils.getTrajectoryAngularZ(trajectory4d);

      final VelocityController1d ctrlLinearX =
          LinearPidController1d.create(linearXParameters, linearTrajectoryX);
      final VelocityController1d ctrlLinearY =
          LinearPidController1d.create(linearYParameters, linearTrajectoryY);
      final VelocityController1d ctrlLinearZ =
          LinearPidController1d.create(linearZParameters, linearTrajectoryZ);
      final VelocityController1d ctrlAngularZ =
          AngularPidController1d.create(angularZParameters, angularTrajectoryZ);

      return new DroneVelocityController(ctrlLinearX, ctrlLinearY, ctrlLinearZ, ctrlAngularZ);
    }
  }

  /** {@code DroneVelocityController} builder static inner class. */
  public static final class CompositionBuilder {
    private VelocityController1d controllerLinearX;
    private VelocityController1d controllerLinearY;
    private VelocityController1d controllerLinearZ;
    private VelocityController1d controllerAngularZ;

    private CompositionBuilder() {}

    /**
     * Sets the {@code controllerLinearX} and returns a reference to this CompositionBuilder so that
     * the methods can be chained together.
     *
     * @param val the {@code controllerLinearX} to set
     * @return a reference to this CompositionBuilder
     */
    public CompositionBuilder withControllerLinearX(VelocityController1d val) {
      controllerLinearX = val;
      return this;
    }

    /**
     * Sets the {@code controllerLinearY} and returns a reference to this CompositionBuilder so that
     * the methods can be chained together.
     *
     * @param val the {@code controllerLinearY} to set
     * @return a reference to this CompositionBuilder
     */
    public CompositionBuilder withControllerLinearY(VelocityController1d val) {
      controllerLinearY = val;
      return this;
    }

    /**
     * Sets the {@code controllerLinearZ} and returns a reference to this CompositionBuilder so that
     * the methods can be chained together.
     *
     * @param val the {@code controllerLinearZ} to set
     * @return a reference to this CompositionBuilder
     */
    public CompositionBuilder withControllerLinearZ(VelocityController1d val) {
      controllerLinearZ = val;
      return this;
    }

    /**
     * Sets the {@code controllerAngularZ} and returns a reference to this CompositionBuilder so
     * that the methods can be chained together.
     *
     * @param val the {@code controllerAngularZ} to set
     * @return a reference to this CompositionBuilder
     */
    public CompositionBuilder withControllerAngularZ(VelocityController1d val) {
      controllerAngularZ = val;
      return this;
    }

    /**
     * Returns a {@code DroneVelocityController} built from the parameters previously set.
     *
     * @return a {@code DroneVelocityController} built with parameters of this {@code
     *     DroneVelocityController.Builder}
     */
    public DroneVelocityController build() {
      checkNotNull(controllerLinearX, "missing controllerLinearX");
      checkNotNull(controllerLinearY, "missing controllerLinearY");
      checkNotNull(controllerLinearZ, "missing controllerLinearZ");
      checkNotNull(controllerAngularZ, "missing controllerAngularZ");
      return new DroneVelocityController(
          controllerLinearX, controllerLinearY, controllerLinearZ, controllerAngularZ);
    }
  }
}
