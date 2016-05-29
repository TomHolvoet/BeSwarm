package pidcontroller;

/**
 * @author Hoang Tung Dinh
 */
public final class PidParameters {

    private final double kp;
    private final double kd;
    private final double ki;

    private final double minVelocity;
    private final double maxVelocity;

    private final double minIntegralError;
    private final double maxIntegralError;

    private PidParameters(Builder builder) {
        kp = builder.kp;
        kd = builder.kd;
        ki = builder.ki;
        minVelocity = builder.minVelocity;
        maxVelocity = builder.maxVelocity;
        minIntegralError = builder.minIntegralError;
        maxIntegralError = builder.maxIntegralError;
    }

    public static Builder builder() {
        return new Builder();
    }

    public double kp() {
        return kp;
    }

    public double kd() {
        return kd;
    }

    public double ki() {
        return ki;
    }

    public double minVelocity() {
        return minVelocity;
    }

    public double maxVelocity() {
        return maxVelocity;
    }

    public double minIntegralError() {
        return minIntegralError;
    }

    public double maxIntegralError() {
        return maxIntegralError;
    }

    /**
     * {@code PidParameters} builder static inner class.
     */
    public static final class Builder {
        private double kp;
        private double kd;
        private double ki;
        private double minVelocity = -Double.MAX_VALUE;
        private double maxVelocity = Double.MAX_VALUE;
        private double minIntegralError = -Double.MAX_VALUE;
        private double maxIntegralError = Double.MAX_VALUE;

        private Builder() {}

        /**
         * Sets the {@code kp} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code kp} to set
         * @return a reference to this Builder
         */
        public Builder kp(double val) {
            kp = val;
            return this;
        }

        /**
         * Sets the {@code kd} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code kd} to set
         * @return a reference to this Builder
         */
        public Builder kd(double val) {
            kd = val;
            return this;
        }

        /**
         * Sets the {@code ki} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code ki} to set
         * @return a reference to this Builder
         */
        public Builder ki(double val) {
            ki = val;
            return this;
        }

        /**
         * Sets the {@code minVelocity} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code minVelocity} to set
         * @return a reference to this Builder
         */
        public Builder minVelocity(double val) {
            minVelocity = val;
            return this;
        }

        /**
         * Sets the {@code maxVelocity} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code maxVelocity} to set
         * @return a reference to this Builder
         */
        public Builder maxVelocity(double val) {
            maxVelocity = val;
            return this;
        }

        /**
         * Sets the {@code minIntegralError} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code minIntegralError} to set
         * @return a reference to this Builder
         */
        public Builder minIntegralError(double val) {
            minIntegralError = val;
            return this;
        }

        /**
         * Sets the {@code maxIntegralError} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code maxIntegralError} to set
         * @return a reference to this Builder
         */
        public Builder maxIntegralError(double val) {
            maxIntegralError = val;
            return this;
        }

        /**
         * Returns a {@code PidParameters} built from the parameters previously set.
         *
         * @return a {@code PidParameters} built with parameters of this {@code PidParameters.Builder}
         */
        public PidParameters build() {
            return new PidParameters(this);
        }
    }
}
