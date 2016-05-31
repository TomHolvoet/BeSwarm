package geom;

import bebopbehavior.Velocity;
import geometry_msgs.Quaternion;

/**
 * A utility class which contains transformation methods.
 *
 * @author Hoang Tung Dinh
 */
public final class Transformations {

    private Transformations() {}

    /**
     * Compute velocity in local coordinate frame from velocity in global coordinate frame.
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

    /**
     * Compute euler angle from quarternion angle.
     *
     * @param quaternion the angle in quaternion representation
     * @return the angle in euler representation.
     * @see <a href="https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles">Equations</a>
     */
    public static EulerAngle computeEulerAngleFromQuaternionAngle(Quaternion quaternion) {
        // TODO improve this method to avoid ambiguity
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
