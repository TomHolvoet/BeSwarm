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
        Trajectory4d second = Trajectories.circleTrajectoryBuilder()
                .setLocation(Point3D.create(1, -2, 1.5))
                .setRadius(0.5)
                .setFrequency(0.05)
                .fixYawAt(-Math.PI / 2)
                .build();
        return Choreography.builder().withTrajectory(second).forTime(120).build();
    }

    public static FiniteTrajectory4d getFastCircle() {
        Trajectory4d second = Trajectories.circleTrajectoryBuilder()
                .setLocation(Point3D.create(1, -2, 1.5))
                .setRadius(0.5)
                .setFrequency(0.2)
                .fixYawAt(-Math.PI / 2)
                .build();
        return Choreography.builder().withTrajectory(second).forTime(120).build();
    }

    public static FiniteTrajectory4d getIndoorPendulum() {
        double yawAngle = 0;
        Trajectory4d target1 = Trajectories.swingTrajectoryBuilder()
                .setOrigin(Point4D.create(1.5, -2, 2.5, yawAngle))
                .setFrequency(0.067)
                .setRadius(1.5)
                .build();
        return Choreography.builder().withTrajectory(target1).forTime(120).build();
    }
}
