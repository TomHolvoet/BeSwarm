package applications.parrot.bebop;

import applications.trajectory.Trajectories;
import applications.trajectory.geom.point.Point3D;
import applications.trajectory.geom.point.Point4D;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.Trajectory4d;

/**
 * y-velocity = 2.5 z-velocity = 2.9
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class BebopLargeCorkscrewExample2 extends AbstractOneBebopFlight {

  /** Default constructor. */
  public BebopLargeCorkscrewExample2() {
    super("BebopLargeCorkscrewTrajectory2");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {

    double orientation = -Math.PI / 2;
    double radius = 1;
    double frequency = 0.4;
    double velocity = 1.5;
    Point4D start = Point4D.create(1, 0, 4, orientation);
    Point3D end = Point3D.create(1, -5, 1);
    Trajectory4d init = Trajectories.newHoldPositionTrajectory(start);
    FiniteTrajectory4d first =
        Trajectories.newCorkscrewTrajectory(start, end, velocity, radius, frequency, 0);
    Trajectory4d last = Trajectories.newHoldPositionTrajectory(Point4D.from(end, orientation));
    return Choreography.builder()
        .withTrajectory(init)
        .forTime(5)
        .withTrajectory(first)
        .forTime(first.getTrajectoryDuration() + 2)
        .withTrajectory(last)
        .forTime(5)
        .build();
  }
}
