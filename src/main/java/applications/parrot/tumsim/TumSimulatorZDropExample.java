package applications.parrot.tumsim;

import applications.trajectory.Trajectories;
import applications.trajectory.points.Point4D;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.Trajectory4d;

/** @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be> */
public class TumSimulatorZDropExample extends AbstractTumSimulatorExample {

  /** Default Constructor. */
  public TumSimulatorZDropExample() {
    super("TumRunZDropTrajectory2");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    Point4D start = Point4D.create(0, 0, 10, 0);
    Point4D end = Point4D.create(0, 15, 10, 0);
    FiniteTrajectory4d target1 = Trajectories.newZDropLineTrajectory(start, end, 0.5, 4, 2);
    Trajectory4d hold1 = Trajectories.newHoldPositionTrajectory(start);
    return Choreography.builder().withTrajectory(hold1).forTime(30).withTrajectory(target1).build();
  }
}
