package applications.parrot.bebop;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class OneBebopFastLargeCircleFlight extends AbstractOneBebopFlight {

  /** Default constructor. */
  public OneBebopFastLargeCircleFlight() {
    super("OneBebopFastLargeCircleFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return TrajectoriesForTesting.getFastLargeCircle();
  }
}
