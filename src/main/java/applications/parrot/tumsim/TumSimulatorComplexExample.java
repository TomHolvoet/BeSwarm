package applications.parrot.tumsim;

import applications.trajectory.Point4D;
import applications.trajectory.Trajectories;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.Trajectory4d;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import static applications.trajectory.Trajectories.newStraightLineTrajectory;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class TumSimulatorComplexExample extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("TumRunExampleTrajectory2");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final FiniteTrajectory4d trajectory = getConcreteTrajectory();
        final TumExampleFlightFacade flight = TumExampleFlightFacade
                .create(trajectory, connectedNode);
        flight.fly();
    }

    private FiniteTrajectory4d getConcreteTrajectory() {
        Trajectory4d init = Trajectories
                .newHoldPositionTrajectory(Point4D.create(0, 0, 1, 0));
        FiniteTrajectory4d first =
                newStraightLineTrajectory(Point4D.create(0, 0, 1, 0),
                        Point4D.create(1.5, -3.0, 1.5, 0), 0.1);
        Trajectory4d inter = Trajectories
                .newHoldPositionTrajectory(Point4D.create(1.5, -3.0, 1.5, 0));
        Trajectory4d second = Trajectories
                .newCircleTrajectory4D(Point4D.create(1.0, -3.0, 1.5, 0), 0.5,
                        -0.20,
                        Math.PI / 8);
        Trajectory4d third = Trajectories
                .newHoldPositionTrajectory(Point4D.create(1.5, -3.5, 1.5, 0));
        Trajectory4d fourth = Trajectories
                .newHoldPositionTrajectory(Point4D.create(1.5, -3.5, 1.0, 0));
        return Choreography.builder().withTrajectory(init).forTime(4)
                .withTrajectory(first)
                .forTime(first.getTrajectoryDuration() + 2)
                .withTrajectory(inter)
                .forTime(5)
                .withTrajectory(second).forTime(40).withTrajectory(third)
                .forTime(10).withTrajectory(fourth).forTime(5).build();
    }
}
