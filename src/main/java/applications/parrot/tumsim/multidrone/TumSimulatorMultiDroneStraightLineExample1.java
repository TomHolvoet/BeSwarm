package applications.parrot.tumsim.multidrone;

import applications.parrot.tumsim.AbstractTumSimulatorExample;
import applications.trajectory.Trajectories;
import applications.trajectory.geom.point.Point4D;
import control.FiniteTrajectory4d;

/**
 * Example trajectory base class with displement parameter for multiple drone instantiations. This
 * example can be used for the pingpong room.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 * @see <a href="https://github.com/dougvk/tum_simulator">The simulator</a>
 */
public class TumSimulatorMultiDroneStraightLineExample1 extends AbstractTumSimulatorExample {

  private final double displacement;

  /** Default Constructor. */
  public TumSimulatorMultiDroneStraightLineExample1() {
    this(0);
  }

  /** @param displacement the displaement in the x direction for multiple drones. */
  public TumSimulatorMultiDroneStraightLineExample1(double displacement) {
    super("TumRunStraightLineTrajectory1");
    this.displacement = displacement;
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return Trajectories.newStraightLineTrajectory(
        Point4D.create(0 + displacement, 0, 1, 0), Point4D.create(0 + displacement, -4, 1, 0), 1);
  }
}
