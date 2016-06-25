package applications.trajectory;

import control.Trajectory1d;
import control.Trajectory4d;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Trajectory represent a straight line in space using a given speed.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class StraightLineTrajectory4D implements Trajectory4d {
    private final Trajectory1d xComp;
    private final Trajectory1d yComp;
    private final Trajectory1d zComp;
    private final Trajectory1d angleComp;

    StraightLineTrajectory4D(Point4D srcpoint, Point4D targetpoint,
            double velocity) {
        checkArgument(velocity > 0,
                "The provided velocity should be strictly greater than 0.");
        checkArgument(velocity <= BasicTrajectory.MAX_ABSOLUTE_VELOCITY,
                "The provided velocity should be smaller than BasicTrajectory"
                        + ".MAX_ABSOLUTE_VELOCITY");
        double speed = velocity;
        Point4D diff = Point4D.minus(targetpoint, srcpoint);
        double totalDistance = StrictMath.sqrt(StrictMath.pow(diff.getX(), 2) +
                StrictMath.pow(diff.getY(), 2) + StrictMath
                .pow(diff.getZ(), 2));
        double endTime = totalDistance / speed;
        checkArgument(totalDistance > 0, "Distance to travel cannot be zero.");
        Point4D speedComponent = Point4D
                .create(velocity * (diff.getX() / totalDistance),
                        velocity * (diff.getY() / totalDistance),
                        velocity * (diff.getZ() / totalDistance),
                        diff.getAngle() / endTime);
        this.xComp = new LinearTrajectory1D(srcpoint.getX(),
                speedComponent.getX());
        this.yComp = new LinearTrajectory1D(srcpoint.getY(),
                speedComponent.getY());
        this.zComp = new LinearTrajectory1D(srcpoint.getZ(),
                speedComponent.getZ());
        this.angleComp = new LinearTrajectory1D(srcpoint.getAngle(),
                speedComponent.getAngle());
    }

    @Override
    public Trajectory1d getTrajectoryLinearX() {
        return this.xComp;
    }

    @Override
    public Trajectory1d getTrajectoryLinearY() {
        return this.yComp;
    }

    @Override
    public Trajectory1d getTrajectoryLinearZ() {
        return this.zComp;
    }

    @Override
    public Trajectory1d getTrajectoryAngularZ() {
        return this.angleComp;
    }

}
