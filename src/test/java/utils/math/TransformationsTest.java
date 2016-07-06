package utils.math;

import control.dto.BodyFrameVelocity;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import geometry_msgs.Quaternion;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import utils.TestUtils;

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
    public void testQuaternionToEulerAngle(double quaternionW, double quaternionX, double quaternionY,
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

    @Test
    @Parameters(source = VelocityProvider.class)
    public void testBodyFrameVelocityToInertialFrameVelocity(Pose pose, BodyFrameVelocity bodyFrameVelocity,
            InertialFrameVelocity inertialFrameVelocity) {
        TestUtils.assertVelocityEqual(Transformations.bodyFrameVelocityToInertialFrameVelocity(bodyFrameVelocity, pose),
                inertialFrameVelocity);
    }

    @Test
    @Parameters(source = VelocityProvider.class)
    public void testInertialFrameVelocityToBodyFrameVelocity(Pose pose, BodyFrameVelocity bodyFrameVelocity,
            InertialFrameVelocity inertialFrameVelocity) {
        TestUtils.assertVelocityEqual(
                Transformations.inertialFrameVelocityToBodyFrameVelocity(inertialFrameVelocity, pose),
                bodyFrameVelocity);
    }
}