package applications.parrot.bebop;

import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class TwoBebopsStraightLineSecondDroneFlight extends AbstractOneBebopFlight {

  protected TwoBebopsStraightLineSecondDroneFlight() {
    super("TwoBebopsStraightLineSecondDroneFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return new TwoBebopsStraightLineFlight().getConcreteTrajectoryForSecondBebop();
  }
}
