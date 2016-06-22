package applications;

import java.util.logging.Logger;

import org.apache.commons.logging.Log;

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
	private double length;

    private LineTrajectory(double flightDuration, double length) {
    	this.flightDuration = flightDuration;
    	this.length = length;
    	this.trajectoryLinearX = new TrajectoryLinearX(flightDuration, length);
    	this.trajectoryLinearY = new TrajectoryLinearY();
    	this.trajectoryLinearZ = new TrajectoryLinearZ();
    }

    public static LineTrajectory create(double flightDuration, double length) {
        return new LineTrajectory(flightDuration, length);
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

    private static final Logger logger = Logger.getLogger("Trajectory");

    private final class TrajectoryLinearX implements Trajectory1d {

    	private final double flightDuration;
		private double length;

		public TrajectoryLinearX(double flightDuration, double length) {
    		this.flightDuration = flightDuration;
    		this.length = length;
    	}
    	
        @Override
        public double getDesiredPosition(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }
            final double currentTime = timeInSeconds - startTime;
            double position = 0 + (currentTime % flightDuration) * length/flightDuration;
            logger.info("Desired position" + position);
            
            return position;
        }

        @Override
        public double getDesiredVelocity(double timeInSeconds) {
            if (startTime < 0) {
                startTime = timeInSeconds;
            }

            final double currentTime = timeInSeconds - startTime;
            if (currentTime < flightDuration) {
            	return StrictMath.sin((timeInSeconds % flightDuration)*length/2*flightDuration);
            } else {
            	return 0.0;
            }
        }
    }

    private final class TrajectoryLinearY implements Trajectory1d {

        private TrajectoryLinearY() {}

        @Override
        public double getDesiredPosition(double timeInSeconds) {
        	return -2.0;
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
