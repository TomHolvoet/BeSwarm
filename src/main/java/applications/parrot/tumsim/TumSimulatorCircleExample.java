package applications.parrot.tumsim;

import applications.trajectory.Trajectories;
import applications.trajectory.points.Point3D;
import applications.trajectory.points.Point4D;
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

    public static FiniteTrajectory4d getConcreteTrajectory() {
        Trajectory4d init = Trajectories.newHoldPositionTrajectory(Point4D.create(2, -2, 1.5, 0));
        Trajectory4d second = Trajectories
                .newConstantYawCircleTrajectory4D(Point3D.create(1, -2, 1.5), 0.5, 0.05, 0, 0);
        //Alternatively, the builder api can also be used like this, to create circle trajectory.
        Trajectory4d alternativeSecond = Trajectories.CircleTrajectoryBuilder()
                .setLocation(Point3D.create(1, -2, 1.5)).setRadius(0.5).setFrequency(0.05)
                .fixYawAt(0).build();
        Trajectory4d third = Trajectories
                .newHoldPositionTrajectory(Point4D.create(2, -2, 1, 0));
        return Choreography.builder().withTrajectory(init).forTime(10).withTrajectory(second)
                .forTime(40).withTrajectory(third)
                .forTime(10).build();
    }
}
