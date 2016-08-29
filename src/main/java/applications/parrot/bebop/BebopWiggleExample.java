package applications.parrot.bebop;

import applications.trajectory.Trajectories;
import applications.trajectory.geom.point.Point4D;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.Trajectory4d;

/** @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be> */
public class BebopWiggleExample extends AbstractBebopExample {

  /** Default Constructor. */
  public BebopWiggleExample() {
    super("WiggleTrajectory");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    Point4D start = Point4D.create(1, -2, 2, 0);
    FiniteTrajectory4d target1 = Trajectories.newWiggleTrajectory(start, 2, 0.75);
    Trajectory4d hold1 = Trajectories.newHoldPositionTrajectory(start);
    return Choreography.builder()
        .withTrajectory(hold1)
        .forTime(10)
        .withTrajectory(target1)
        .withTrajectory(hold1)
        .forTime(10)
        .build();
  }
}
