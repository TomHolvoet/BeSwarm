package applications.trajectory;

import applications.trajectory.points.Point4D;
import choreo.Choreography;
import com.google.common.collect.Lists;
import control.FiniteTrajectory4d;
import control.Trajectory1d;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A straight line trajectory in xy plane with sudden drops in the z dimension.
 * Source and destination point shoudl be in the same z plane.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class ZDropLineTrajectoryAlternative extends BasicTrajectory implements FiniteTrajectory4d {

    //    private final StraightLineTrajectory4D concreteTarget;
    private Trajectory1d zComp;
    //    private final double segmentLength;
    private boolean atEnd;
    private static final double EPS = 0.001;
    private final FiniteTrajectory4d target;
    private Point4D src;
    private Point4D dst;
    private double velocity;

    ZDropLineTrajectoryAlternative(Point4D before, Point4D after, double speed, double drops,
            double dropDistance) {
        checkArgument(before.getZ() == after.getZ(),
                "Origin and destination should be in the same horizontal plane.");
        //        this.concreteTarget = new StraightLineTrajectory4D(before, after, speed, 1);
        //        this.segmentLength = concreteTarget.getTotalDistance() / drops;
        //        this.zComp = new ObservingRingForwarder(new LinearTrajectory1D(after.getZ() -
        // dropDistance,
        //                dropDistance / segmentLength * concreteTarget.getVelocity()),
        //                concreteTarget.getTrajectoryDuration());
        //        this.atEnd = false;
        this.src = before;
        this.src = after;
        this.velocity = speed;
        Choreography.Builder builder = Choreography.builder();

        List<Point4D> points = Lists.newArrayList();
        List<Point4D> dropPoints = Lists.newArrayList();
        FiniteTrajectory4d traj = Trajectories.newStraightLineTrajectory(before, after, 1);
        double duration = traj.getTrajectoryDuration();
        traj.getDesiredVelocityX(0);
        traj.getDesiredVelocityY(0);
        traj.getDesiredVelocityZ(0);
        for (int i = 0; i < drops; i++) {
            double mark = (duration / drops) * i;
            points.add(
                    Point4D.create(traj.getDesiredPositionX(mark), traj.getDesiredPositionY(mark),
                            traj.getDesiredPositionZ(mark), traj.getDesiredAngleZ(mark)));
        }
        points.add(after);
        for (Point4D p : points) {
            dropPoints
                    .add(Point4D.create(p.getX(), p.getY(), p.getZ() - dropDistance, p.getAngle()));
        }
        for (int i = 0; i < drops; i++) {
            builder.withTrajectory(
                    Trajectories.newStraightLineTrajectory(points.get(i), dropPoints.get(i), 1));
            builder.withTrajectory(Trajectories
                    .newStraightLineTrajectory(dropPoints.get(i), points.get(i + 1), 1));
        }
        target = builder.build();
    }

    @Override
    public double getDesiredPositionX(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredPositionX(currentTime);
    }

    @Override
    public double getDesiredVelocityX(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredVelocityX(currentTime);
    }

    @Override
    public double getDesiredPositionY(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredPositionY(currentTime);
    }

    @Override
    public double getDesiredVelocityY(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredVelocityY(currentTime);
    }

    @Override
    public double getDesiredPositionZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredPositionZ(currentTime);
    }

    @Override
    public double getDesiredVelocityZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredVelocityZ(currentTime);
    }

    @Override
    public double getDesiredAngleZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredAngleZ(currentTime);
    }

    @Override
    public double getDesiredAngularVelocityZ(double timeInSeconds) {
        final double currentTime = getRelativeTime(timeInSeconds);
        return getTargetTrajectory().getDesiredAngularVelocityZ(currentTime);
    }

    @Override
    public String toString() {
        return "ZDropLineTrajectory{" + "velocity=" + velocity
                + ", src point=" + src + ", target point="
                + dst + '}';
    }

    @Override
    public double getTrajectoryDuration() {
        return getTargetTrajectory().getTrajectoryDuration();
    }

    private FiniteTrajectory4d getTargetTrajectory() {
        return target;
    }

    private Trajectory1d getZcomponent() {
        return this.zComp;
    }
}
