package control;

/**
 * Representation of the trajectory function.
 *
 * @author Hoang Tung Dinh
 */
public interface Trajectory1d {
    double getDesiredPosition(double timeInSeconds);

    double getDesiredVelocity(double timeInSeconds);
}
