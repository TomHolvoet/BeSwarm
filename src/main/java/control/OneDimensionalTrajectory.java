package control;

/**
 * Representation of the trajectory function.
 *
 * @author Hoang Tung Dinh
 */
public interface OneDimensionalTrajectory {
    double getDesiredPosition(double timeInSeconds);

    double getDesiredVelocity(double timeInSeconds);
}
