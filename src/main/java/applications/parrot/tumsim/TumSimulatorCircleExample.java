package applications.parrot.tumsim;

import applications.trajectory.Point4D;
import applications.trajectory.Trajectories;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.Trajectory4d;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class TumSimulatorCircleExample extends AbstractNodeMain {

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
                .newHoldPositionTrajectory(Point4D.create(2, -2, 1.5, 0));
        Trajectory4d second = Trajectories
                .newConstantYawCircleTrajectory4D(Point4D.create(1, -2, 1.5, 0),
                        1, 0.05, 0);
        Trajectory4d third = Trajectories
                .newHoldPositionTrajectory(Point4D.create(1, -2, 1, 0));
        return Choreography.builder().withTrajectory(init).forTime(5).withTrajectory(second)
                .forTime(40).withTrajectory(third)
                .forTime(10).build();
    }
}
