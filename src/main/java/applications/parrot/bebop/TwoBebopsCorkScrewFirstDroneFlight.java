package applications.parrot.bebop;

import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class TwoBebopsCorkScrewFirstDroneFlight extends AbstractOneBebopFlight {
  protected TwoBebopsCorkScrewFirstDroneFlight() {
    super("TwoBebopsCorkScrewFirstDroneFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return new TwoBebopsCorkScrewFlight().getConcreteTrajectoryForFirstBebop();
  }
}
