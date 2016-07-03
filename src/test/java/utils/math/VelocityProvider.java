package utils.math;

import control.dto.BodyFrameVelocity;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;

/**
 * @author Hoang Tung Dinh
 */
public final class VelocityProvider {

    private VelocityProvider() {}

    public static Object[] provideVelocityValues() {
        final Pose pose1 = Pose.builder().x(0).y(0).z(10).yaw(0.7853981633974483).build();
        final BodyFrameVelocity bVel1 = Velocity.builder().linearX(1).linearY(0).linearZ(0).angularZ(0).build();
        final InertialFrameVelocity iVel1 = Velocity.builder()
                .linearX(0.707)
                .linearY(0.707)
                .linearZ(0)
                .angularZ(0)
                .build();

        final Pose pose2 = Pose.builder().x(0).y(0).z(10).yaw(0).build();
        final BodyFrameVelocity bVel2 = Velocity.builder().linearX(1).linearY(2).linearZ(3).angularZ(4).build();
        final InertialFrameVelocity iVel2 = Velocity.builder().linearX(1).linearY(2).linearZ(3).angularZ(4).build();

        final Pose pose3 = Pose.builder().x(0).y(0).z(10).yaw(1).build();
        final BodyFrameVelocity bVel3 = Velocity.builder().linearX(1).linearY(2).linearZ(3).angularZ(4).build();
        final InertialFrameVelocity iVel3 = Velocity.builder()
                .linearX(-1.14)
                .linearY(1.922)
                .linearZ(3)
                .angularZ(4)
                .build();
        return new Object[]{new Object[]{pose1, bVel1, iVel1},
                new Object[]{pose2, bVel2, iVel2},
                new Object[]{pose3, bVel3, iVel3}};
    }
}
