package applications.parrot.bebop;

import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class TwoBebopsStraightLineFirstDroneFlight extends AbstractOneBebopFlight {
  protected TwoBebopsStraightLineFirstDroneFlight() {
    super("TwoBebopsStraightLineFirstDroneFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return new TwoBebopsStraightLineFlight().getConcreteTrajectoryForFirstBebop();
  }
}
