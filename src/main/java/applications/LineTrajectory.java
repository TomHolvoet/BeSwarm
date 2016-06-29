package applications;

import control.Trajectory1d;
import control.Trajectory4d;

import java.util.logging.Logger;

/**
 * @author mhct
 */
public final class LineTrajectory implements Trajectory4d {

    private final Trajectory1d trajectoryLinearX;
    private final Trajectory1d trajectoryLinearY;
    private final Trajectory1d trajectoryLinearZ;

    private final Trajectory1d trajectoryAngularZ = new TrajectoryAngularZ();

    private double startTime = -1;

    private LineTrajectory(double flightDuration, double length) {
        this.trajectoryLinearX = new TrajectoryLinearX();
        this.trajectoryLinearY = new TrajectoryLinearY(flightDuration, length);
        this.trajectoryLinearZ = new TrajectoryLinearZ();
    }

    public static LineTrajectory create(double flightDuration, double length) {
        return new LineTrajectory(flightDuration, length);
    }

    private static final Logger logger = Logger.getLogger("Trajectory");

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

    private final class TrajectoryLinearY implements Trajectory1d {

        private final double flightDuration;
        private double length;

        public TrajectoryLinearY(double flightDuration, double length) {
            this.flightDuration = flightDuration;
            this.length = length;
        }

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }
            final double currentTime = timeInSeconds - startTime;

            if (currentTime >= flightDuration) {
                return -length;
            } else {
                double position = 0 - (currentTime % flightDuration) * length/flightDuration;
                logger.info("Desired position" + position);

                return position;
            }
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            if (currentTime < flightDuration) {
                return length/flightDuration;
            } else {
                return 0.0;
            }
        }
    }

    private final class TrajectoryLinearX implements Trajectory1d {

        private TrajectoryLinearX() {}

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            return 1.5;
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            return 0.0;
        }
    }

    private final class TrajectoryLinearZ implements Trajectory1d {

        private TrajectoryLinearZ() {}

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            return 1.5;
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            return 0.0;
        }
    }

    private final class TrajectoryAngularZ implements Trajectory1d {

        private TrajectoryAngularZ() {}

        @Override
        public double getDesiredPosition(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            return 0.0;
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            return 0.0;
        }
    }
}