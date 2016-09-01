package applications.parrot.bebop;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class OneBebopFastIndoorPendulumFlight extends AbstractOneBebopFlight {

  /** Default constructor. */
  public OneBebopFastIndoorPendulumFlight() {
    super("OneBebopIndoorPendulumFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return TrajectoriesForTesting.getFastIndoorPendulum();
  }
}
