package applications.simulations.trajectory;

/**
 * Basic trajectories to be executed and synced at the time set by the user.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class BasicTrajectory {
    public static final double MAX_ABSOLUTE_VELOCITY = 1;
    private double startTime = -1;

    protected void setStartTime(double timeInSeconds) {
        if (startTime < 0) {
            startTime = timeInSeconds;
        }
    }

    protected double getStartTime() {
        return this.startTime;
    }
}
