package applications.parrot.tumsim;

import applications.trajectory.Trajectories;
import applications.trajectory.points.Point3D;
import applications.trajectory.points.Point4D;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.Trajectory4d;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class TumSimulatorCorkscrewExample extends AbstractTumSimulatorExample {

    /**
     */
    public TumSimulatorCorkscrewExample() {
        super("TumRunCorkscrewTrajectory");
    }

    @Override
    public FiniteTrajectory4d getConcreteTrajectory() {
        double orientation = -Math.PI / 2;
        double radius = 0.5;
        double frequency = 0.1;
        double velocity = 0.1;
        Point4D start = Point4D.create(0, 0, 1, orientation);
        Point3D end = Point3D.create(1.5, -3.0, 1.5);
        Trajectory4d init = Trajectories.newHoldPositionTrajectory(start);
        FiniteTrajectory4d first = Trajectories
                .newCorkscrewTrajectory(start, end, velocity, radius, frequency, 0);
        Trajectory4d inter = Trajectories
                .newHoldPositionTrajectory(Point4D.from(end, orientation));
        return Choreography.builder()
                .withTrajectory(init)
                .forTime(4)
                .withTrajectory(first)
                .forTime(first.getTrajectoryDuration() + 2)
                .withTrajectory(inter)
                .forTime(5)
                .build();
    }
}
