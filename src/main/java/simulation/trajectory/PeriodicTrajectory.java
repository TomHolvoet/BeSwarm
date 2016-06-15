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
    private Point4D linearDisplacement;

    protected PeriodicTrajectory() {
        this(0, Point4D.origin());
    }

    protected PeriodicTrajectory(double phase) {
        this(phase, Point4D.origin());
    }

    protected PeriodicTrajectory(double phase, Point4D displacement) {
        this.phaseDisplacement = phase;
        this.linearDisplacement = displacement;
    }

    /**
     * @return Displacement in phase in radians.
     */
    protected double getPhaseDisplacement() {
        return phaseDisplacement;
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