package pidcontroller;

import bebopcontrol.Velocity;

/**
 * @author Hoang Tung Dinh
 */
public final class CoordinateTransformer {

    /**
     * Convert velocity in global coordinate frame to velocity in local coordinate frame
     *
     * @param globalVelocity velocity in global coordinate frame
     * @return velocity in local coordinate frame
     */
    public static Velocity globalToLocalVelocity(Velocity globalVelocity) {
        // TODO test me
        // same linearZ
        final double linearZ = globalVelocity.linearZ();
        // same angularZ
        final double angularZ = globalVelocity.angularZ();

        final double theta = -angularZ;
        final double sin = StrictMath.sin(theta);
        final double cos = StrictMath.cos(theta);

        final double linearX = globalVelocity.linearX() * cos - globalVelocity.linearY() * sin;
        final double linearY = globalVelocity.linearX() * sin + globalVelocity.linearY() * cos;

        return Velocity.builder().linearX(linearX).linearY(linearY).linearZ(linearZ).angularZ(angularZ).build();
    }
}
