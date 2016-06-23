package applications.simulations.trajectory;

import control.Trajectory1d;
import control.Trajectory4d;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class HoldPositionTrajectory4D implements Trajectory4d {
    private final Point4D targetpoint;

    HoldPositionTrajectory4D(Point4D targetpoint) {
        this.targetpoint = targetpoint;
    }

    @Override
    public Trajectory1d getTrajectoryLinearX() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                return targetpoint.getX();
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                return 0;
            }
        };
    }

    @Override
    public Trajectory1d getTrajectoryLinearY() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                return targetpoint.getY();
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                return 0;
            }
        };
    }

    @Override
    public Trajectory1d getTrajectoryLinearZ() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                return targetpoint.getZ();
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                return 0;
            }
        };
    }

    @Override
    public Trajectory1d getTrajectoryAngularZ() {
        return new Trajectory1d() {
            @Override
            public double getDesiredPosition(double timeInSeconds) {
                return targetpoint.getAngle();
            }

            @Override
            public double getDesiredVelocity(double timeInSeconds) {
                return 0;
            }
        };
    }
}
