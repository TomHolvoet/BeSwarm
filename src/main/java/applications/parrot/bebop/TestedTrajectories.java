package applications.parrot.bebop;

import applications.trajectory.Trajectories;
import applications.trajectory.points.Point3D;
import applications.trajectory.points.Point4D;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.Trajectory4d;

/**
 * @author Hoang Tung Dinh
 */
public final class TestedTrajectories {

    private TestedTrajectories() {
    }

    /**
     * This trajectory has been tested and worked well.
     */
    public static FiniteTrajectory4d getSlowCircle() {
        Trajectory4d second = Trajectories.CircleTrajectoryBuilder()
                .setLocation(Point3D.create(1, -2, 1.5))
                .setRadius(0.5)
                .setFrequency(0.05)
                .fixYawAt(-Math.PI / 2)
                .build();
        return Choreography.builder().withTrajectory(second).forTime(120).build();
    }

    /**
     * This trajectory has been tested and worked well with pid params 0.3 1.0 for x y, 0.3 0.6 for z and 0.3 0.4 for
     * yaw.
     */
    public static FiniteTrajectory4d getFastCircle() {
        Trajectory4d second = Trajectories.CircleTrajectoryBuilder()
                .setLocation(Point3D.create(1, -2, 1.5))
                .setRadius(0.5)
                .setFrequency(0.2)
                .fixYawAt(-Math.PI / 2)
                .build();
        return Choreography.builder().withTrajectory(second).forTime(120).build();
    }

    /**
     * This trajectory can work with pid params: 0.5 0.8 for x, 0.2 1.0 for y, 1.0 0.3 for z, 0.3 0.4 for yaw
     */
    public static FiniteTrajectory4d getSlowIndoorPendulum() {
        double yawAngle = -Math.PI / 2;
        Trajectory4d target1 = Trajectories.SwingTrajectoryBuilder()
                .setOrigin(Point4D.create(1.5, -2, 2.0, yawAngle))
                .setFrequency(0.03)
                .setRadius(1.5)
                .build();
        return Choreography.builder().withTrajectory(target1).forTime(120).build();
    }
}
