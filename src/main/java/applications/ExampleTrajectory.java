package applications;

import control.Trajectory1d;
import control.Trajectory4d;

/**
 * @author Hoang Tung Dinh
 */
public final class ExampleTrajectory implements Trajectory4d {

    private final Trajectory1d trajectoryLinearX = new TrajectoryLinearX();
    private final Trajectory1d trajectoryLinearY = new TrajectoryLinearY();
    private final Trajectory1d trajectoryLinearZ = new TrajectoryLinearZ();

    private final Trajectory1d trajectoryAngularZ = new TrajectoryAngularZ();

    private double startTime = -1;

    private ExampleTrajectory() {}

    public static ExampleTrajectory create() {
        return new ExampleTrajectory();
    }

    @Override
    public double getDesiredPositionX(double timeInSeconds) {
        return trajectoryLinearX.getDesiredPosition(timeInSeconds);
    }

    @Override
    public double getDesiredVelocityX(double timeInSeconds) {
        return trajectoryLinearX.getDesiredVelocity(timeInSeconds);
    }

    @Override
    public double getDesiredPositionY(double timeInSeconds) {
        return trajectoryLinearY.getDesiredPosition(timeInSeconds);
    }

    @Override
    public double getDesiredVelocityY(double timeInSeconds) {
        return trajectoryLinearY.getDesiredVelocity(timeInSeconds);
    }

    @Override
    public double getDesiredPositionZ(double timeInSeconds) {
        return trajectoryLinearZ.getDesiredPosition(timeInSeconds);
    }

    @Override
    public double getDesiredVelocityZ(double timeInSeconds) {
        return trajectoryLinearZ.getDesiredVelocity(timeInSeconds);
    }

    @Override
    public double getDesiredAngleZ(double timeInSeconds) {
        return trajectoryAngularZ.getDesiredPosition(timeInSeconds);
    }

    @Override
    public double getDesiredAngularVelocityZ(double timeInSeconds) {
        return trajectoryAngularZ.getDesiredVelocity(timeInSeconds);
    }

    private final class TrajectoryLinearX implements Trajectory1d {

        private TrajectoryLinearX() {}

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

    private final class TrajectoryLinearY implements Trajectory1d {

        private TrajectoryLinearY() {}

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

    private final class TrajectoryLinearZ implements Trajectory1d {

        private TrajectoryLinearZ() {}

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            return 4 + 2 * StrictMath.cos(0.25 * currentTime);
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

    private final class TrajectoryAngularZ implements Trajectory1d {

        private TrajectoryAngularZ() {}

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            return 3.14 * StrictMath.sin(0.25 * currentTime);
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            return 0.785 * StrictMath.cos(0.25 * currentTime);
        }
    }
}
