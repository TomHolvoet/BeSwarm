package applications.parrot.bebop;

import control.FiniteTrajectory4d;

/** @author Hoang Tung Dinh */
public class TwoBebopsCircleSecondDroneFlight extends AbstractOneBebopFlight {
  protected TwoBebopsCircleSecondDroneFlight() {
    super("TwoBebopsCircleSecondDroneFlight");
  }

  @Override
  public FiniteTrajectory4d getConcreteTrajectory() {
    return new TwoBebopsCircleFlight().getConcreteTrajectoryForSecondBebop();
  }
}
