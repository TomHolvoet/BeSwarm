package utils.math;

import control.dto.BodyFrameVelocity;
import control.dto.InertialFrameVelocity;
import geometry_msgs.Quaternion;

/**
 * @author Hoang Tung Dinh
 */
public final class Transformations {

    private Transformations() {}

    /**
     * Compute euler angle from quarternion angle. The resulting angles are always in range [-pi, pi]
     *
     * @param quaternion the angle in quaternion representation
     * @return the angle in euler representation.
     * @see <a href="https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles">Equations</a>
     */
    public static EulerAngle quaternionToEulerAngle(Quaternion quaternion) {
        final double q0 = quaternion.getW();
        final double q1 = quaternion.getX();
        final double q2 = quaternion.getY();
        final double q3 = quaternion.getZ();

        final double eulerX = StrictMath.atan2(2 * (q0 * q1 + q2 * q3), 1 - 2 * (q1 * q1 + q2 * q2));
        final double eulerY = StrictMath.asin(2 * (q0 * q2 - q3 * q1));
        final double eulerZ = StrictMath.atan2(2 * (q0 * q3 + q1 * q2), 1 - 2 * (q2 * q2 + q3 * q3));

        return EulerAngle.builder().angleX(eulerX).angleY(eulerY).angleZ(eulerZ).build();
    }

    /**
     * @param inertialFrameVelocity velocity in inertial frame
     * @return velocity in body frame
     */
    public static BodyFrameVelocity globalVelocityToLocalVelocity(InertialFrameVelocity inertialFrameVelocity) {
        // TODO test me
        // same linearZ
        final double linearZ = inertialFrameVelocity.linearZ();
        // same angularZ
        final double angularZ = inertialFrameVelocity.angularZ();

        final double theta = -inertialFrameVelocity.pose().yaw();
        final double sin = StrictMath.sin(theta);
        final double cos = StrictMath.cos(theta);

        final double linearX = inertialFrameVelocity.linearX() * cos - inertialFrameVelocity.linearY() * sin;
        final double linearY = inertialFrameVelocity.linearX() * sin + inertialFrameVelocity.linearY() * cos;

        return BodyFrameVelocity.builder()
                .linearX(linearX)
                .linearY(linearY)
                .linearZ(linearZ)
                .angularZ(angularZ)
                .build();
    }
}
