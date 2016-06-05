package simulation;

import control.SinglePointTrajectory1d;
import control.Trajectory1d;
import control.Trajectory4d;

/**
 * @author Hoang Tung Dinh
 */
public final class ExampleTrajectory implements Trajectory4d {

    private final Trajectory1d trajectoryLinearX = new CosineTrajectory();
    private final Trajectory1d trajectoryLinearY = new SineTrajectory();
    private final Trajectory1d trajectoryLinearZ = new SineTrajectory();

    private final Trajectory1d trajectoryAngularZ = SinglePointTrajectory1d.create(0, 0);

    private double startTime = -1;

    private ExampleTrajectory() {}

    public static ExampleTrajectory create() {
        return new ExampleTrajectory();
    }

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

    private final class SineTrajectory implements Trajectory1d {

        private SineTrajectory() {}

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            return 2 * StrictMath.sin(0.25 * currentTime);
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            return 0.5 * StrictMath.cos(0.25 * currentTime);
        }
    }

    private final class CosineTrajectory implements Trajectory1d {

        private CosineTrajectory() {}

        ;

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            return 2 * StrictMath.cos(0.25 * currentTime);
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            return -0.5 * StrictMath.sin(0.25 * currentTime);
        }
    }
}
