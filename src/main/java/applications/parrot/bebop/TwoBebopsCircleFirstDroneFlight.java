package applications.parrot.bebop;

import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class TwoBebopsCircleFirstDroneFlight extends AbstractOneBebopFlight {
  protected TwoBebopsCircleFirstDroneFlight() {
    super("TwoBebopsCircleFirstDroneFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return new TwoBebopsCircleFlight().getConcreteTrajectoryForFirstBebop();
  }
}
