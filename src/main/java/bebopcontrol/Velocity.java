package bebopcontrol;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public class Velocity {
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

    /**
     * {@code Velocity} builder static inner class.
     */
    public static final class Builder {
        private double linearX = 0;
        private double linearY = 0;
        private double linearZ = 0;
        private double angularZ = 0;

        private static final String errorMessage = "Input must be in range [-1..1]";

        private Builder() {}

        /**
         * Sets the {@code linearX} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code linearX} to set
         * @return a reference to this Builder
         */
        public Builder linearX(double val) {
            checkArgument(val >= -1 && val <= 1, errorMessage);
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
            checkArgument(val >= -1 && val <= 1, errorMessage);
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
            checkArgument(val >= -1 && val <= 1, errorMessage);
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
            checkArgument(val >= -1 && val <= 1, errorMessage);
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
