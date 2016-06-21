package applications;

import control.Trajectory1d;
import control.Trajectory4d;

/**
 * @author mhct
 */
public final class LineTrajectory implements Trajectory4d {

    private final Trajectory1d trajectoryLinearX;
    private final Trajectory1d trajectoryLinearY;
    private final Trajectory1d trajectoryLinearZ;

    private final Trajectory1d trajectoryAngularZ = new TrajectoryAngularZ();

    private double startTime = -1;
	private double flightDuration;

    private LineTrajectory(double flightDuration) {
    	this.flightDuration = flightDuration;
    	this.trajectoryLinearX = new TrajectoryLinearX(flightDuration);
    	this.trajectoryLinearY = new TrajectoryLinearY(flightDuration);
    	this.trajectoryLinearZ = new TrajectoryLinearZ(flightDuration);
    }

    public static LineTrajectory create(double flightDuration) {
        return new LineTrajectory(flightDuration);
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


        @Override
        public double getDesiredPosition(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            return ;
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
        	return 2.0;
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
