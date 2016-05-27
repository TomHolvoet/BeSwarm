package bebopcontrol;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Hoang Tung Dinh
 */
public final class Velocity {
    private final double linearX;
    private final double linearY;
    private final double linearZ;
    private final double angularZ;

    private Velocity(Builder builder) {
        linearX = builder.linearX;
        linearY = builder.linearY;
        linearZ = builder.linearZ;
        angularZ = builder.angularZ;
    }

    public static Builder builder() {
        return new Builder();
    }

    public double linearX() {
        return linearX;
    }

    public double linearY() {
        return linearY;
    }

    public double linearZ() {
        return linearZ;
    }

    public double angularZ() {
        return angularZ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Velocity velocity = (Velocity) o;
        return Double.compare(velocity.linearX, linearX) == 0 &&
                Double.compare(velocity.linearY, linearY) == 0 &&
                Double.compare(velocity.linearZ, linearZ) == 0 &&
                Double.compare(velocity.angularZ, angularZ) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(linearX, linearY, linearZ, angularZ);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("linearX", linearX)
                .add("linearY", linearY)
                .add("linearZ", linearZ)
                .add("angularZ", angularZ)
                .toString();
    }

    /**
     * {@code Velocity} builder static inner class.
     */
    public static final class Builder {
        private double linearX = 0;
        private double linearY = 0;
        private double linearZ = 0;
        private double angularZ = 0;

        private Builder() {}

        /**
         * Sets the {@code linearX} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code linearX} to set
         * @return a reference to this Builder
         */
        public Builder linearX(double val) {
            linearX = val;
            return this;
        }

        /**
         * Sets the {@code linearY} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code linearY} to set
         * @return a reference to this Builder
         */
        public Builder linearY(double val) {
            linearY = val;
            return this;
        }

        /**
         * Sets the {@code linearZ} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code linearZ} to set
         * @return a reference to this Builder
         */
        public Builder linearZ(double val) {
            linearZ = val;
            return this;
        }

        /**
         * Sets the {@code angularZ} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code angularZ} to set
         * @return a reference to this Builder
         */
        public Builder angularZ(double val) {
            angularZ = val;
            return this;
        }

        /**
         * Returns a {@code Velocity} built from the parameters previously set.
         *
         * @return a {@code Velocity} built with parameters of this {@code Velocity.Builder}
         */
        public Velocity build() {
            return new Velocity(this);
        }
    }
}
