package applications.parrot.bebop;

import applications.parrot.tumsim.multidrone.TumSimulatorMultiDroneStraightLineExample1;
import applications.parrot.tumsim.multidrone.TumSimulatorMultiDroneStraightLineExample2;
import control.FiniteTrajectory4d;

/**
 * Two bebops flight straight light, apart from each other by 2 meters. First bebop starts at (0,
 * 0), yaw 0. Second bebop starts at (2, 0), yaw 0.
 *
 * @author Hoang Tung Dinh
 */
public class TwoBebopsStraightLineFlight extends AbstractTwoBebopFlight {

  protected TwoBebopsStraightLineFlight() {
    super("TwoBebopsStraightLineFlight");
  }

  @Override
  FiniteTrajectory4d getConcreteTrajectoryForFirstBebop() {
    return new TumSimulatorMultiDroneStraightLineExample1().getConcreteTrajectory();
  }

  @Override
  FiniteTrajectory4d getConcreteTrajectoryForSecondBebop() {
    return new TumSimulatorMultiDroneStraightLineExample2().getConcreteTrajectory();
  }
}
