package applications.parrot.bebop;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class OneBebopFastCircleFlight extends AbstractOneBebopFlight {

  /** Default constructor. */
  public OneBebopFastCircleFlight() {
    super("OneBebopFastCircleFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return TrajectoriesForTesting.getFastCircle();
  }
}
