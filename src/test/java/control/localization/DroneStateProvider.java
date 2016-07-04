package control.localization;

import control.dto.BodyFrameVelocity;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;

/**
 * @author Hoang Tung Dinh
 */
public final class DroneStateProvider {

    private DroneStateProvider() {}

    public static Object[] provideDroneStateValues() {
        final Pose pose1 = Pose.builder().x(0).y(0).z(10).yaw(0.7853981633974483).build();
        final BodyFrameVelocity bVel1 = Velocity.builder().linearX(1).linearY(0).linearZ(0).angularZ(0).build();
        final InertialFrameVelocity iVel1 = Velocity.builder()
                .linearX(0.707)
                .linearY(0.707)
                .linearZ(0)
                .angularZ(0)
                .build();
        final QuaternionAngle quAngle1 = QuaternionAngle.builder().w(0.92388).x(0).y(0).z(0.38268).build();

        final Pose pose2 = Pose.builder().x(0).y(0).z(10).yaw(0).build();
        final BodyFrameVelocity bVel2 = Velocity.builder().linearX(1).linearY(2).linearZ(3).angularZ(4).build();
        final InertialFrameVelocity iVel2 = Velocity.builder().linearX(1).linearY(2).linearZ(3).angularZ(4).build();
        final QuaternionAngle quAngle2 = QuaternionAngle.builder().w(1).x(0).y(0).z(0).build();

        final Pose pose3 = Pose.builder().x(0).y(0).z(10).yaw(1).build();
        final BodyFrameVelocity bVel3 = Velocity.builder().linearX(1).linearY(2).linearZ(3).angularZ(4).build();
        final InertialFrameVelocity iVel3 = Velocity.builder()
                .linearX(-1.14)
                .linearY(1.922)
                .linearZ(3)
                .angularZ(4)
                .build();
        final QuaternionAngle quAngle3 = QuaternionAngle.builder().w(0.87758).x(0).y(0).z(0.47943).build();

        return new Object[]{new Object[]{pose1, bVel1, iVel1, quAngle1},
                new Object[]{pose2, bVel2, iVel2, quAngle2},
                new Object[]{pose3, bVel3, iVel3, quAngle3}};
    }
}
