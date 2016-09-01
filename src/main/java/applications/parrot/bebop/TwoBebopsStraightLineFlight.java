package applications.parrot.bebop;

import applications.trajectory.Trajectories;
import applications.trajectory.geom.point.Point4D;
import control.FiniteTrajectory4d;

/**
 * Two bebops flight straight light, apart from each other by 2 meters. First bebop starts at (0,
 * 0), yaw 0. Second bebop starts at (2, 0), yaw 0.
 *
 * @author Hoang Tung Dinh
 */
public class TwoBebopsStraightLineFlight extends AbstractTwoBebopFlight {

  private static final int XCOORD_START = 0;
  private static final int YCOORD_START = 0;
  private static final int ZCOORD_START = 1;
  private static final int ANGLE_START = 0;
  private static final int XCOORD_END = 0;
  private static final int YCOORD_END = -4;
  private static final int ZCOORD_END = 1;
  private static final int ANGLE_END = 0;
  private static final int VELOCITY = 1;

  protected TwoBebopsStraightLineFlight() {
    super("TwoBebopsStraightLineFlight");
  }

  @Override
  FiniteTrajectory4d getConcreteTrajectoryForFirstBebop() {
    return Trajectories.newStraightLineTrajectory(
        Point4D.create(XCOORD_START, YCOORD_START, ZCOORD_START, ANGLE_START),
        Point4D.create(XCOORD_END, YCOORD_END, ZCOORD_END, ANGLE_END),
        VELOCITY);
  }

  @Override
  FiniteTrajectory4d getConcreteTrajectoryForSecondBebop() {
    return Trajectories.newStraightLineTrajectory(
        Point4D.create(XCOORD_START + 2, YCOORD_START, ZCOORD_START, ANGLE_START),
        Point4D.create(XCOORD_END + 2, YCOORD_END, ZCOORD_END, ANGLE_END),
        VELOCITY);
  }
}
