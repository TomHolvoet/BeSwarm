package control;

/**
 * 2D Trajectory representing a trajectory in 2 planes of motion.
 * Default plane of motion is the (x,y)-plane but the (x,z), and the (y,z) plane
 * motion trajectories can also be modelled under this interface.
 *
 * @author Kristof Coninx
 */
public interface Trajectory2d {
    //    /**
    //     * @return A 1d trajectory function for the abscissa (or commonly
    // x-axis).
    //     */
    //    Trajectory1d getTrajectoryLinearAbscissa();
    //
    //    /**
    //     * @return A 1d trajectory function for the abscissa (or commonly
    // y-axis).
    //     */
    //    Trajectory1d getTrajectoryLinearOrdinate();

    double getDesiredPositionAbscissa(double timeInSeconds);

    double getDesiredVelocityAbscissa(double timeInSeconds);

    double getDesiredPositionOrdinate(double timeInSeconds);

    double getDesiredVelocityOrdinate(double timeInSeconds);
}
