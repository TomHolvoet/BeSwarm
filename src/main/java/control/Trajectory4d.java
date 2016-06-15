package control;

/**
 * @author Hoang Tung Dinh
 */
public interface Trajectory4d {
    Trajectory1d getTrajectoryLinearX();

    Trajectory1d getTrajectoryLinearY();

    Trajectory1d getTrajectoryLinearZ();

    Trajectory1d getTrajectoryAngularZ();
}
