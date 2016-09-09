package applications.parrot.tumsim;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

/** @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be> */
public class TumSimulatorLargeCircleExample extends AbstractTumSimulatorExample {

  /** Default Constructor. */
  public TumSimulatorLargeCircleExample() {
    super("TumSimulatorLargeCircleExample");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return TrajectoriesForTesting.getLargeCircle();
  }
}
