package simulation;

import control.SinglePointTrajectory1d;
import control.Trajectory1d;
import control.Trajectory4d;

/**
 * @author Hoang Tung Dinh
 */
public final class ExampleTrajectory implements Trajectory4d {

    private final Trajectory1d trajectoryLinearX = SinglePointTrajectory1d.create(0, 0);
    private final Trajectory1d trajectoryLinearZ = SinglePointTrajectory1d.create(1, 0);
    private final Trajectory1d trajectoryAngularZ = SinglePointTrajectory1d.create(0, 0);

    private final Trajectory1d trajectoryLinearY = SineTrajectory.create();

    private ExampleTrajectory() {}

    public static ExampleTrajectory create() {return new ExampleTrajectory();}

    @Override
    public Trajectory1d getTrajectoryLinearX() {
        return trajectoryLinearX;
    }

    @Override
    public Trajectory1d getTrajectoryLinearY() {
        return trajectoryLinearY;
    }

    @Override
    public Trajectory1d getTrajectoryLinearZ() {
        return trajectoryLinearZ;
    }

    @Override
    public Trajectory1d getTrajectoryAngularZ() {
        return trajectoryAngularZ;
    }

    private static final class SineTrajectory implements Trajectory1d {
        private double startTime = -1;

        private SineTrajectory() {}

        public static SineTrajectory create() {
            return new SineTrajectory();
        }

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            return StrictMath.sin(0.5 * currentTime);
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            return 0.5 * StrictMath.cos(0.5 * currentTime);
        }
    }
}
