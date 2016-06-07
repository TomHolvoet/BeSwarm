package simulation;

import control.SinglePointTrajectory1d;
import control.Trajectory1d;
import control.Trajectory4d;

/**
 * @author Hoang Tung Dinh
 */
public final class ExampleTrajectory1 implements Trajectory4d {

    private final Trajectory1d trajectoryLinearX = new TrajectoryLinearX();
    private final Trajectory1d trajectoryLinearY = new TrajectoryLinearY();
    private final Trajectory1d trajectoryLinearZ = new TrajectoryLinearZ();

    private final Trajectory1d trajectoryAngularZ = SinglePointTrajectory1d
            .create(0, 0);

    private double startTime = -1;

    private ExampleTrajectory1() {
    }

    public static ExampleTrajectory1 create() {
        return new ExampleTrajectory1();
    }

    private void setStartTime(double timeInSeconds) {
        if (startTime < 0) {
            startTime = timeInSeconds;
        }
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

    private final class TrajectoryLinearX implements Trajectory1d {

        private TrajectoryLinearX() {
        }

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            setStartTime(timeInSeconds);

            final double currentTime = timeInSeconds - startTime;
            return 2 * StrictMath.cos(0.25 * currentTime);
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            setStartTime(timeInSeconds);

            final double currentTime = timeInSeconds - startTime;
            return -0.5 * StrictMath.sin(0.25 * currentTime);
        }
    }

    private final class TrajectoryLinearY implements Trajectory1d {

        private TrajectoryLinearY() {
        }

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            setStartTime(timeInSeconds);

            final double currentTime = timeInSeconds - startTime;
            return 2 * StrictMath.sin(0.25 * currentTime);
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            setStartTime(timeInSeconds);

            final double currentTime = timeInSeconds - startTime;
            return 0.5 * StrictMath.cos(0.25 * currentTime);
        }
    }

    private final class TrajectoryLinearZ implements Trajectory1d {

        private TrajectoryLinearZ() {
        }

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            setStartTime(timeInSeconds);

            final double currentTime = timeInSeconds - startTime;
            return 4 + 2 * StrictMath.cos(0.25 * currentTime);
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            setStartTime(timeInSeconds);

            final double currentTime = timeInSeconds - startTime;
            return -0.5 * StrictMath.sin(0.25 * currentTime);
        }
    }
}
