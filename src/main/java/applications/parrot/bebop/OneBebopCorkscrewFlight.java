package applications.parrot.bebop;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

/**
 * An implementation of {@link AbstractOneBebopFlight} with the {@link
 * TrajectoriesForTesting#getCorkscrew()} trajectory.
 *
 * @author Hoang Tung Dinh
 */
public class OneBebopCorkscrewFlight extends AbstractOneBebopFlight {

  /** Default constructor. */
  public OneBebopCorkscrewFlight() {
    super("OneBebopCorkscrewFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return TrajectoriesForTesting.getCorkscrew();
  }
}
