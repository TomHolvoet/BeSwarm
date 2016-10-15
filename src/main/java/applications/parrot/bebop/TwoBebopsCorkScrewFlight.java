package applications.parrot.bebop;

import applications.parrot.tumsim.multidrone.TumSimulatorMultiDroneCorkscrewExample1;
import applications.parrot.tumsim.multidrone.TumSimulatorMultiDroneCorkscrewExample2;
import control.FiniteTrajectory4d;

/**
 * First bebop starts at (0, 0), yaw -pi/2, second bebop starts at (
 *
 * @author Hoang Tung Dinh
 */
public class TwoBebopsCorkScrewFlight extends AbstractTwoBebopFlight {
  protected TwoBebopsCorkScrewFlight() {
    super("TwoBebopsCorkScrewFlight");
  }

  @Override
  FiniteTrajectory4d getConcreteTrajectoryForFirstBebop() {
    return new TumSimulatorMultiDroneCorkscrewExample1().getConcreteTrajectory();
  }

  @Override
  FiniteTrajectory4d getConcreteTrajectoryForSecondBebop() {
    return new TumSimulatorMultiDroneCorkscrewExample2().getConcreteTrajectory();
  }
}
