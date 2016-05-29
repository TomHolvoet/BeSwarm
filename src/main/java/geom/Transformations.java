package geom;

import bebopcontrol.Velocity;
import geometry_msgs.Quaternion;

/**
 * @author Hoang Tung Dinh
 */
public final class Transformations {

    private Transformations() {}

    /**
     * Convert velocity in global coordinate frame to velocity in local coordinate frame
     *
     * @param globalVelocity velocity in global coordinate frame
     * @return velocity in local coordinate frame
     */
    public static Velocity computeLocalVelocityFromGlobalVelocity(Velocity globalVelocity, double currentYaw) {
        // TODO test me
        // same linearZ
        final double linearZ = globalVelocity.linearZ();
        // same angularZ
        final double angularZ = globalVelocity.angularZ();

        final double theta = -currentYaw;
        final double sin = StrictMath.sin(theta);
        final double cos = StrictMath.cos(theta);

        final double linearX = globalVelocity.linearX() * cos - globalVelocity.linearY() * sin;
        final double linearY = globalVelocity.linearX() * sin + globalVelocity.linearY() * cos;

        return Velocity.builder().linearX(linearX).linearY(linearY).linearZ(linearZ).angularZ(angularZ).build();
    }

    public static EulerAngle computeEulerAngleFromQuaternionAngle(Quaternion quaternion) {
        final double q0 = quaternion.getW();
        final double q1 = quaternion.getX();
        final double q2 = quaternion.getY();
        final double q3 = quaternion.getZ();

        final double eulerX = StrictMath.atan2(2 * (q0 * q1 + q2 * q3), 1 - 2 * (q1 * q1 + q2 * q2));
        final double eulerY = StrictMath.asin(2 * (q0 * q2 - q3 * q1));
        final double eulerZ = StrictMath.atan2(2 * (q0 * q3 + q1 * q2), 1 - 2 * (q2 * q2 + q3 * q3));

        return EulerAngle.builder().angleX(eulerX).angleY(eulerY).angleZ(eulerZ).build();
    }
}
