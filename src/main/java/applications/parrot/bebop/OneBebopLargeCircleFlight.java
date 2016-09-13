package applications.parrot.bebop;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class OneBebopLargeCircleFlight extends AbstractOneBebopFlight {

  /** Default constructor. */
  public OneBebopLargeCircleFlight() {
    super("OneBebopLargeCircleFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return TrajectoriesForTesting.getLargeCircle();
  }
}
