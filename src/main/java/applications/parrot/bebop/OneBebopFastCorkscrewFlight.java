package applications.parrot.bebop;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

/**
 * An implementation of {@link AbstractOneBebopFlight} with the {@link
 * TrajectoriesForTesting#getCorkscrew()} trajectory.
 *
 * @author Hoang Tung Dinh
 */
public class OneBebopFastCorkscrewFlight extends AbstractOneBebopFlight {

  /** Default constructor. */
  public OneBebopFastCorkscrewFlight() {
    super("OneBebopFastCorkscrewFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return TrajectoriesForTesting.getFastCorkscrew();
  }
}
