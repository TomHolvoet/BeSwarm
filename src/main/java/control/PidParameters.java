package control;

import com.google.auto.value.AutoValue;

/**
 * A value class which stores parameters for a pid controller.
 *
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class PidParameters {

    /**
     * Returns the kp.
     */
    public abstract double kp();

    /**
     * Returns the kd.
     */
    public abstract double kd();

    /**
     * Returns the ki.
     */
    public abstract double ki();

    /**
     * Returns the lag time in seconds.
     */
    public abstract double lagTimeInSeconds();

    /**
     * Returns the minimum velocity.
     */
    public abstract double minVelocity();

    /**
     * Returns the maximum velocity.
     */
    public abstract double maxVelocity();

    /**
     * Returns the minimum integral error.
     */
    public abstract double minIntegralError();

    /**
     * Returns the maximum integral error.
     */
    public abstract double maxIntegralError();

    /**
     * {@link Builder#kp}, {@link Builder#kd()}, {@link Builder#ki} are mandatory. All other parameters are optional.
     *
     * @return a builder
     */
    public static Builder builder() {
        return new AutoValue_PidParameters.Builder().setLagTimeInSeconds(0)
                .setMinVelocity(-Double.MAX_VALUE)
                .setMaxVelocity(Double.MAX_VALUE)
                .setMinIntegralError(-Double.MAX_VALUE)
                .setMaxIntegralError(Double.MAX_VALUE);
    }

    /**
     * Builds an {@link PidParameters} instance.
     */
    @AutoValue.Builder
    public abstract static class Builder {
        /**
         * Set the kp.
         */
        public abstract Builder setKp(double value);

        /**
         * Set the kd.
         */
        public abstract Builder setKd(double value);

        /**
         * Set the ki.
         */
        public abstract Builder setKi(double value);

        /**
         * Set the kp.
         */
        public abstract Builder setLagTimeInSeconds(double value);

        /**
         * Set the lag time in seconds.
         */
        public abstract Builder setMinVelocity(double value);

        /**
         * Set the maximum velocity.
         */
        public abstract Builder setMaxVelocity(double value);

        /**
         * Set the minimum integral error.
         */
        public abstract Builder setMinIntegralError(double value);

        /**
         * Set the maximum integral error.
         */
        public abstract Builder setMaxIntegralError(double value);

        /**
         * Builds a {@link PidParameters} instance.
         */
        public abstract PidParameters build();
    }
}
