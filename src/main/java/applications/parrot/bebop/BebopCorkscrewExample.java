package applications.parrot.bebop;

import applications.TrajectoriesForTesting;
import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class BebopCorkscrewExample extends AbstractBebopExample {

  /** Default constructor. */
  public BebopCorkscrewExample() {
    super("BebopCorkscrewExample");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return TrajectoriesForTesting.getCorkscrew();
  }
}
