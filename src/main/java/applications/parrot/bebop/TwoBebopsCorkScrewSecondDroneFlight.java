package applications.parrot.bebop;

import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class TwoBebopsCorkScrewSecondDroneFlight extends AbstractOneBebopFlight {
  protected TwoBebopsCorkScrewSecondDroneFlight() {
    super("TwoBebopsCorkScrewSecondDroneFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return new TwoBebopsCorkScrewFlight().getConcreteTrajectoryForSecondBebop();
  }
}
