package simulation.trajectory;

/**
 * Abstract class for periodic trajectory commonalities.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public abstract class PeriodicTrajectory {

    public static final double MAX_ABSOLUTE_SPEED = 1;
    protected static final double TWOPI = Math.PI * 2;
    protected static final double HALFPI = Math.PI / 2;
    protected static final double PISQUARED = Math.PI * Math.PI;
    private double phaseDisplacement;

    protected PeriodicTrajectory() {
        this.phaseDisplacement = 0;
    }

    protected PeriodicTrajectory(double phase) {
        this.phaseDisplacement = phase;
    }

    /**
     * @return Displacement in phase in radians.
     */
    protected double getPhaseDisplacement() {
        return phaseDisplacement;
    }
}