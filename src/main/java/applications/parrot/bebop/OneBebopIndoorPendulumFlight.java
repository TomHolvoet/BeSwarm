package applications.parrot.bebop;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class OneBebopIndoorPendulumFlight extends AbstractOneBebopFlight {

  /** Default constructor. */
  public OneBebopIndoorPendulumFlight() {
    super("OneBebopIndoorPendulumFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return TrajectoriesForTesting.getSlowIndoorPendulum();
  }
}
