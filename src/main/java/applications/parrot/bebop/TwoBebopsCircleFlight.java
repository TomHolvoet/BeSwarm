package applications.parrot.bebop;

import applications.trajectory.Trajectories;
import applications.trajectory.geom.point.Point3D;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.Trajectory4d;

/**
 * First bebop starts at x=1.5, y=-1, yaw = -pi/2. Second bebop starts at x=1.5, y =-4, yaw = -pi/2.
 * TODO check if these numbers are correct.
 *
 * @author Hoang Tung Dinh
 */
public class TwoBebopsCircleFlight extends AbstractTwoBebopFlight {
  protected TwoBebopsCircleFlight() {
    super("TwoBebopsCircleFlight");
  }

  @Override
  FiniteTrajectory4d getConcreteTrajectoryForFirstBebop() {
    Trajectory4d second =
        Trajectories.circleTrajectoryBuilder()
            .setLocation(Point3D.create(1.5, -2.5, 1.5))
            .setRadius(1.5)
            .setFrequency(0.1)
            .fixYawAt(-Math.PI / 2)
            .setPhase(0)
            .build();
    return Choreography.builder().withTrajectory(second).forTime(120).build();
  }

  @Override
  FiniteTrajectory4d getConcreteTrajectoryForSecondBebop() {
    Trajectory4d second =
        Trajectories.circleTrajectoryBuilder()
            .setLocation(Point3D.create(1.5, -2.5, 1.5))
            .setRadius(1.5)
            .setFrequency(0.1)
            .fixYawAt(-Math.PI / 2)
            .setPhase(Math.PI)
            .build();
    return Choreography.builder().withTrajectory(second).forTime(120).build();
  }
}
