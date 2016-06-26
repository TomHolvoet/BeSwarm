package applications.parrot.tumsim;

import applications.trajectory.Point4D;
import applications.trajectory.Trajectories;
import choreo.Choreography;
import control.Trajectory4d;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class TumSimulatorComplexExample extends TumSimulatorExample {
    @Override
    public Trajectory4d getConcreteTrajectory() {
        Trajectory4d first = Trajectories
                .newStraightLineTrajectory(Point4D.create(0, 0, 1.5, 0),
                        Point4D.create(5, 5, 5, 0), 0.6);
        Trajectory4d second = Trajectories
                .newCircleTrajectory4D(Point4D.create(4, 5, 5, 0), 1, 0.10,
                        Math.PI / 4);
        Trajectory4d third = Trajectories
                .newHoldPositionTrajectory(Point4D.create(1, 1, 2, 0));
        return Choreography.builder().withTrajectory(first).forTime(20)
                .withTrajectory(second).forTime(40).withTrajectory(third)
                .forTime(30).build();
    }
}
