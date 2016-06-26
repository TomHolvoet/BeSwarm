package applications.trajectory;

import control.Trajectory1d;
import control.Trajectory4d;

/**
 * Forwarding decorator for trajectory4D instances with
 * inner-trajectory1D hooks.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public abstract class Trajectory4DForwardingDecorator implements Trajectory4d {
    private final Trajectory4d target;

    /**
     * Public constructor
     *
     * @param target The target trajectory to wrap.
     */
    public Trajectory4DForwardingDecorator(Trajectory4d target) {
        this.target = target;
    }

    @Override
    public Trajectory1d getTrajectoryLinearX() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                positionDelegate(timeInSeconds);
                return target.getTrajectoryLinearX()
                        .getDesiredPosition(timeInSeconds);
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                velocityDelegate(timeInSeconds);
                return target.getTrajectoryLinearX()
                        .getDesiredVelocity(timeInSeconds);
            }
        };
    }

    @Override
    public Trajectory1d getTrajectoryLinearY() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                positionDelegate(timeInSeconds);
                return target.getTrajectoryLinearY()
                        .getDesiredPosition(timeInSeconds);
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                velocityDelegate(timeInSeconds);
                return target.getTrajectoryLinearY()
                        .getDesiredVelocity(timeInSeconds);
            }
        };
    }

    @Override
    public Trajectory1d getTrajectoryLinearZ() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                positionDelegate(timeInSeconds);
                return target.getTrajectoryLinearZ()
                        .getDesiredPosition(timeInSeconds);
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                velocityDelegate(timeInSeconds);
                return target.getTrajectoryLinearZ()
                        .getDesiredVelocity(timeInSeconds);
            }
        };
    }

    @Override
    public Trajectory1d getTrajectoryAngularZ() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                positionDelegate(timeInSeconds);
                return target.getTrajectoryAngularZ()
                        .getDesiredPosition(timeInSeconds);
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                velocityDelegate(timeInSeconds);
                return target.getTrajectoryAngularZ()
                        .getDesiredVelocity(timeInSeconds);
            }
        };
    }

    protected abstract void velocityDelegate(double timeInSeconds);

    protected abstract void positionDelegate(double timeInSeconds);
}
