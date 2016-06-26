package utils.math;

import control.dto.BodyFrameVelocity;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import control.dto.VelocityDto;
import geometry_msgs.Quaternion;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hoang Tung Dinh
 */
@RunWith(JUnitParamsRunner.class)
public class TransformationsTest {

    private static final double DELTA = 0.01;

    private Object[] eulerAndCorrespondingQuaternions() {
        return new Object[]{new Object[]{1, 0, 0, 0, 0, 0, 0},
                new Object[]{0.7071, 0, 0.7071, 0, 0, 1.5707963267948966, 0},
                new Object[]{0.7071, 0, -0.7071, 0, 0, -1.5707963267948966, 0}};
    }

    @Test
    @Parameters(method = "eulerAndCorrespondingQuaternions")
    public void testComputeEulerAngleFromQuaternionAngle(double quaternionW, double quaternionX, double quaternionY,
            double quaternionZ, double eulerX, double eulerY, double eulerZ) {
        final Quaternion mockQuaternion = mock(Quaternion.class);
        when(mockQuaternion.getW()).thenReturn(quaternionW);
        when(mockQuaternion.getX()).thenReturn(quaternionX);
        when(mockQuaternion.getY()).thenReturn(quaternionY);
        when(mockQuaternion.getZ()).thenReturn(quaternionZ);

        final EulerAngle eulerAngle = Transformations.quaternionToEulerAngle(mockQuaternion);

        assertThat(eulerAngle.angleX()).isWithin(DELTA).of(eulerX);
        assertThat(eulerAngle.angleY()).isWithin(DELTA).of(eulerY);
        assertThat(eulerAngle.angleZ()).isWithin(DELTA).of(eulerZ);
    }

    private Object[] velocityValues() {
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

    @Test
    @Parameters(method = "velocityValues")
    public void testBodyFrameVelocityToInertialFrameVelocity(Pose pose, BodyFrameVelocity bodyFrameVelocity,
            InertialFrameVelocity inertialFrameVelocity) {
        assertVelocityEqual(Transformations.bodyFrameVelocityToInertialFrameVelocity(bodyFrameVelocity, pose),
                inertialFrameVelocity);
    }

    @Test
    @Parameters(method = "velocityValues")
    public void testInertialFrameVelocityToBodyFrameVelocity(Pose pose, BodyFrameVelocity bodyFrameVelocity,
            InertialFrameVelocity inertialFrameVelocity) {
        assertVelocityEqual(Transformations.inertialFrameVelocityToBodyFrameVelocity(inertialFrameVelocity, pose),
                bodyFrameVelocity);
    }

    private static void assertVelocityEqual(VelocityDto vel1, VelocityDto vel2) {
        assertThat(vel1.linearX()).isWithin(DELTA).of(vel2.linearX());
        assertThat(vel1.linearY()).isWithin(DELTA).of(vel2.linearY());
        assertThat(vel1.linearZ()).isWithin(DELTA).of(vel2.linearZ());
        assertThat(vel1.angularZ()).isWithin(DELTA).of(vel2.angularZ());
    }
}