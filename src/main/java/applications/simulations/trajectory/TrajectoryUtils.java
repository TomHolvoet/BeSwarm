package applications.simulations.trajectory;

/**
 * Utility class for static utilities used in defining trajectories.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public final class TrajectoryUtils {
    private TrajectoryUtils() {
    }

    public static double pendulumAngleFromTime(double currentTime,
            double frequency) {
        return PeriodicTrajectory.HALFPI * StrictMath
                .cos(PeriodicTrajectory.TWOPI * frequency * currentTime);
    }
}
