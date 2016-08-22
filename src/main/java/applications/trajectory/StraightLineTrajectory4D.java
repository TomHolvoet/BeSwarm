package applications.trajectory;

import applications.trajectory.geom.point.Point4D;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Trajectory represent a straight line in space between two given points at a given speed. Once the
 * destination point has been reached, the trajectory enforces to hold position at the destination
 * point. The optional parameter velocityCutoffTimePercentage represents the percentage of the
 * tragjectory ( in time) to perform at the given velocity. The default value is 1, representing the
 * trajectory will reach its destination with a positive velocity in the direction of travel. This
 * will cause overshooting behavior. Choose a value < 1 to trigger the controller to start braking
 * sooner.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
class StraightLineTrajectory4D extends AbstractUncheckedStraightLineTrajectory4D {

    StraightLineTrajectory4D(Point4D srcpoint, Point4D targetpoint, double velocity) {
        this(srcpoint, targetpoint, velocity, 1);
    }

    StraightLineTrajectory4D(
            Point4D srcpoint, Point4D targetpoint, double velocity,
            double velocityCutoffTimePercentage) {
        super(srcpoint, targetpoint, velocity, velocityCutoffTimePercentage);
        checkArgument(
                velocity <= BasicTrajectory.MAX_ABSOLUTE_VELOCITY,
                "The provided velocity should be smaller than BasicTrajectory"
                        + ".MAX_ABSOLUTE_VELOCITY");
    }
}
