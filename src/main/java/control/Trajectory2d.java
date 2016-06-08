package control;

/**
 * 2D Trajectory representing a trajectory in 2 planes of motion.
 * Default plane of motion is the (x,y)-plane but the (x,z), and the (y,z) plane
 * motion trajectories can also be modelled under this interface.
 *
 * @author Kristof Coninx
 */
public interface Trajectory2d {
    Trajectory1d getTrajectoryLinearX();

    Trajectory1d getTrajectoryLinearY();
}
