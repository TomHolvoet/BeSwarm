package geom;

import geometry_msgs.Quaternion;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import utils.math.EulerAngle;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Hoang Tung Dinh
 */
@RunWith(JUnitParamsRunner.class)
public class EulerAngleTest {
    private static final double DELTA = 0.01;
    private static final double PI = Math.PI;

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

        final EulerAngle eulerAngle = EulerAngle.createFromQuaternion(mockQuaternion);

        assertThat(eulerAngle.angleX()).isWithin(DELTA).of(eulerX);
        assertThat(eulerAngle.angleY()).isWithin(DELTA).of(eulerY);
        assertThat(eulerAngle.angleZ()).isWithin(DELTA).of(eulerZ);
    }

    private Object[] parametersForComputeAngleDistance() {
        return new Object[]{new Object[]{PI, -PI, 0},
                new Object[]{1, -1, -2},
                new Object[]{-1, 1, 2},
                new Object[]{-2 * PI / 3, 2 * PI / 3, -2 * PI / 3},
                new Object[]{2 * PI / 3, -2 * PI / 3, 2 * PI / 3},
                new Object[]{PI / 3, -PI / 3, -2 * PI / 3},
                new Object[]{-PI / 3, PI / 3, 2 * PI / 3}};
    }

    @Test
    @Parameters(method = "parametersForComputeAngleDistance")
    public void testComputeAngleDistance(double firstAngle, double secondAngle, double distance) {
        assertThat(EulerAngle.computeAngleDistance(firstAngle, secondAngle)).isWithin(DELTA).of(distance);
    }
}