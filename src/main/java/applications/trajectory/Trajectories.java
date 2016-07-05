package applications.trajectory;

import control.FiniteTrajectory4d;
import control.Trajectory2d;
import control.Trajectory4d;

/**
 * Utility factory class for creating motion primitives.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public final class Trajectories {
    private Trajectories() {
    }

    /**
     * @return a simple example trajectory of a circle around the origin with
     * default radius(1) and frequency (5/s).
     */
    public static Trajectory4d newExampleCircleTrajectory4D() {
        return CircleTrajectory4D.builder().build();
    }

    /**
     * @param origin    the linear displacement in space.
     * @param radius    the radius of the circle.
     * @param frequency the frequency in time of completing the circle.
     * @return a simple flat circle trajectory in the xy-plane in a
     * Trajectory4D format.
     */
    public static Trajectory4d newFlatCircleTrajectory4D(Point4D origin,
            double radius,
            double frequency) {
        return CircleTrajectory4D.builder().setRadius(radius)
                .setFrequency(frequency).setLocation(origin).build();
    }

    /**
     * @param origin     the linear displacement in space.
     * @param radius     the radius of the circle.
     * @param frequency  the frequency in time of completing the circle.
     * @param planeAngle The angle of the circle trajectory plane with the
     *                   xy-plane.
     * @return a circle trajectory in space as a Trajectory4d object.
     */
    public static Trajectory4d newCircleTrajectory4D(Point4D origin,
            double radius,
            double frequency, double planeAngle) {
        return CircleTrajectory4D.builder().setRadius(radius)
                .setFrequency(frequency).setPlaneAngle(planeAngle)
                .setLocation(origin).build();
    }

    public static Trajectory4d newConstantYawCircleTrajectory4D(Point4D origin,
            double radius,
            double frequency, double planeAngle) {
        return CircleTrajectory4D.builder()
                .setLocation(origin).setRadius(radius)
                .setFrequency(0.1).setPlaneAngle(Math.PI / 2).setAngularMovement(false).build();
    }

    /**
     * @return A trajectory2d object representing an example circle
     * trajectory with default radius(1) and frequency(5) around the origin.
     */
    public static Trajectory2d newExampleCircleTrajectory2D() {
        return CircleTrajectory2D.builder().build();
    }

    /**
     * @param origin    the linear displacement in space.
     * @param radius    the radius of the circle.
     * @param frequency the frequency in time of completing the circle.
     * @param clockwise clockwise rotation if true.
     * @return A new circle  trajectory in 2d format.
     */
    public static Trajectory2d newCircleTrajectory2D(Point4D origin,
            double radius, double frequency, boolean clockwise) {
        return CircleTrajectory2D.builder().setRadius(radius)
                .setFrequency(frequency).setClockwise(clockwise)
                .setOrigin(origin).build();
    }

    /**
     * A trajectory2d object representing an example circle
     * trajectory.
     *
     * @param radius    the radius of the circle.
     * @param frequency the frequency in time of completing the circle.
     * @return Simple 2d circle trajectory.
     */
    public static Trajectory2d newSimpleCircleTrajectory2D(double radius,
            double frequency) {
        return CircleTrajectory2D.builder().setRadius(radius)
                .setFrequency(frequency).build();
    }

    /**
     * @return An example trajectory4d object representing a pendulum swing
     * motion
     * using default radius and frequency.
     */
    public static Trajectory4d newExamplePendulumSwingTrajectory() {
        return SwingTrajectory4D.builder().build();
    }

    /**
     * @param origin    the linear displacement in space.
     * @param radius    the radius of the pendulum motion (or length of
     *                  virtual string).
     * @param frequency the frequency in time of completing the motion.
     * @return A new simple pendulum trajectory in the xz plane.
     */
    public static Trajectory4d newSimplePendulumSwingTrajectory(Point4D origin,
            double radius, double frequency) {
        return SwingTrajectory4D.builder().setRadius(radius)
                .setFrequency(frequency).setOrigin(origin).setXzPlaneAngle(0)
                .build();
    }

    /**
     * @param origin     the linear displacement in space.
     * @param radius     the radius of the pendulum motion (or length of
     *                   virtual string).
     * @param frequency  the frequency in time of completing the motion.
     * @param planeAngle the angle between the plane of motion and the xz plane.
     * @return A new simple pendulum trajectory in the xz plane.
     */
    public static Trajectory4d newPendulumSwingTrajectory(Point4D origin,
            double radius, double frequency, double planeAngle) {
        return SwingTrajectory4D.builder().setRadius(radius)
                .setFrequency(frequency).setOrigin(origin)
                .setXzPlaneAngle(planeAngle)
                .build();
    }

    /**
     * @param target The target location to hold as a trajectory;
     * @return A new trajectory that specifies a constant point in space to
     * follow.
     */
    public static Trajectory4d newHoldPositionTrajectory(Point4D target) {
        return new HoldPositionTrajectory4D(target);
    }

    /**
     * @param sourcePoint origin point of motion.
     * @param targetPoint destination point of motion.
     * @param velocity    the velocity to move with.
     * @return A new trajectory instance representing a straight line in
     * space between two given points at a given velocity.
     */
    public static FiniteTrajectory4d newStraightLineTrajectory(
            Point4D sourcePoint,
            Point4D targetPoint, double velocity) {
        return new StraightLineTrajectory4D(sourcePoint, targetPoint, velocity);
    }
}
