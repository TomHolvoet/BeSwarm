package commands;

import com.google.auto.value.AutoValue;

/**
 * A value class which stores the velocity of the drone.
 *
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class Velocity {
    public abstract double linearX();

    public abstract double linearY();

    public abstract double linearZ();

    public abstract double angularZ();

    public static Builder builder() {
        return new AutoValue_Velocity.Builder();
    }

    /**
     * Compute velocity in local coordinate frame from velocity in global coordinate frame.
     *
     * @param globalVelocity velocity in global coordinate frame
     * @return velocity in local coordinate frame
     */
    public static Velocity createLocalVelocityFromGlobalVelocity(Velocity globalVelocity, double currentYaw) {
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

        return builder().linearX(linearX).linearY(linearY).linearZ(linearZ).angularZ(angularZ).build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder linearX(double value);

        public abstract Builder linearY(double value);

        public abstract Builder linearZ(double value);

        public abstract Builder angularZ(double value);

        public abstract Velocity build();
    }
}
