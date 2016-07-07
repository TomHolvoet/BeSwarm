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
        final TumExampleFlightFacade flight = TumExampleFlightFacade.create(trajectory, connectedNode);
        flight.fly();
    }

    public static FiniteTrajectory4d getConcreteTrajectory() {
//        final Trajectory4d init = Trajectories.newHoldPositionTrajectory(Point4D.create(2, -2, 1.5, 0));
        //Alternatively, the builder api can also be used like this, to create circle trajectory.
        final Trajectory4d second = Trajectories.CircleTrajectoryBuilder()
                .setLocation(Point4D.create(1, -2, 1.5, 0))
                .setRadius(0.5)
                .setFrequency(0.05)
                .fixYawAt(-Math.PI / 2)
                .build();
//        final Trajectory4d third = Trajectories.newHoldPositionTrajectory(Point4D.create(2, -2, 1, 0));
        return Choreography.builder()
//                .withTrajectory(init)
//                .forTime(10)
                .withTrajectory(second)
                .forTime(120)
//                .withTrajectory(third)
//                .forTime(10)
                .build();
    }
}
