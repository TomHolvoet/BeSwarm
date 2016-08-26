package applications.parrot.bebop;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class OneBebopSlowCircleFlight extends AbstractOneBebopFlight {

  /** Default constructor. */
  public OneBebopSlowCircleFlight() {
    super("OneBebopSlowCircleFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return TrajectoriesForTesting.getSlowCircle();
  }
}
