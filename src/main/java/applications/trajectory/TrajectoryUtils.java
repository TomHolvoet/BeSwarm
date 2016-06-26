package applications.trajectory;

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
    public static double pendulumAngleFromTime(double currentTime,
            double frequency) {
        return PeriodicTrajectory.HALFPI * StrictMath
                .cos(PeriodicTrajectory.TWOPI * frequency * currentTime);
    }
}
