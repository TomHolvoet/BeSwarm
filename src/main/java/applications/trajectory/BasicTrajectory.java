package applications.trajectory;

/**
 * Basic trajectories to be executed and synced at the time set by the user.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class BasicTrajectory {
    public static final double MAX_ABSOLUTE_VELOCITY = 1;
    protected final Point4D linearDisplacement;
    private double startTime = -1;

    protected BasicTrajectory(Point4D displacement) {
        this.linearDisplacement = displacement;
    }

    protected BasicTrajectory() {
        this.linearDisplacement = Point4D.origin();
    }

    protected void setStartTime(double timeInSeconds) {
        if (startTime < 0) {
            startTime = timeInSeconds;
        }
    }

    protected double getStartTime() {
        return this.startTime;
    }

    /**
     * Return the origin point for linear displacement in 4D.
     *
     * @return
     */
    protected Point4D getLinearDisplacement() {
        return this.linearDisplacement;
    }
}
