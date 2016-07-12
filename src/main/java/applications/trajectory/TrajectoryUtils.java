package applications.trajectory;

import control.Trajectory1d;
import control.Trajectory4d;

/**
 * Utility class for static utilities used in defining trajectories.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public final class TrajectoryUtils {
    private TrajectoryUtils() {
    }

    /**
     * @param currentTime The time point in the motion.
     * @param frequency   The frequency of the pendulum movement.
     * @return The angle of the pendulum string with the z-axis for a given
     * time if the pendulum moves with given frequency.
     */
    public static double pendulumAngleFromTime(double currentTime, double frequency) {
        return PeriodicTrajectory.HALFPI * StrictMath.cos(PeriodicTrajectory.TWOPI * frequency * currentTime);
    }

    public static Trajectory1d getTrajectoryLinearX(final Trajectory4d trajectory4d) {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                return trajectory4d.getDesiredPositionX(timeInSeconds);
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                return trajectory4d.getDesiredVelocityX(timeInSeconds);
            }
        };
    }

    public static Trajectory1d getTrajectoryLinearY(final Trajectory4d trajectory4d) {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                return trajectory4d.getDesiredPositionY(timeInSeconds);
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                return trajectory4d.getDesiredVelocityY(timeInSeconds);
            }
        };
    }

    public static Trajectory1d getTrajectoryLinearZ(final Trajectory4d trajectory4d) {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                return trajectory4d.getDesiredPositionZ(timeInSeconds);
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                return trajectory4d.getDesiredVelocityZ(timeInSeconds);
            }
        };
    }

    public static Trajectory1d getTrajectoryAngularZ(final Trajectory4d trajectory4d) {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                return trajectory4d.getDesiredAngleZ(timeInSeconds);
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                return trajectory4d.getDesiredAngularVelocityZ(timeInSeconds);
            }
        };
    }
}
