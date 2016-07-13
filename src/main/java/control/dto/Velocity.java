package control.dto;

import com.google.auto.value.AutoValue;
import geometry_msgs.Twist;

/**
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class Velocity implements InertialFrameVelocity, BodyFrameVelocity {
    protected Velocity() {}

    @Override
    public abstract double linearX();

    @Override
    public abstract double linearY();

    @Override
    public abstract double linearZ();

    @Override
    public abstract double angularZ();

    public static Builder builder() {
        return new AutoValue_Velocity.Builder();
    }

    public static Velocity createZeroVelocity() {
        return builder().setLinearX(0).setLinearY(0).setLinearZ(0).setAngularZ(0).build();
    }

    /**
     * Converts a Twist velocity (given in NED coordinates) to a local velocity using XYZ frame
     * TODO fix frame of reference for local velocity
     *
     * @return
     */
    public static Velocity createLocalVelocityFrom(Twist twist) {
        final double twistX = twist.getLinear().getX();
        final double twistY = twist.getLinear().getY();
        final double twistZ = twist.getLinear().getZ();
        final double twistAngularZ = twist.getAngular().getZ();

        return builder().setLinearX(twistX).setLinearY(twistY).setLinearZ(twistZ).setAngularZ(twistAngularZ).build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setLinearX(double value);

        public abstract Builder setLinearY(double value);

        public abstract Builder setLinearZ(double value);

        public abstract Builder setAngularZ(double value);

        public abstract Velocity build();
    }
}
