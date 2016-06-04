package control;

/**
 * @author Hoang Tung Dinh
 */
public interface FourDimensionalTrajectory {
    OneDimensionalTrajectory getTrajectoryLinearX();

    OneDimensionalTrajectory getTrajectoryLinearY();

    OneDimensionalTrajectory getTrajectoryLinearZ();

    OneDimensionalTrajectory getTrajectoryAngularZ();
}
