package control;

import com.google.auto.value.AutoValue;

/**
 * A value class which stores parameters for a pid controller.
 *
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class PidParameters {

    public abstract double kp();

    public abstract double kd();

    public abstract double ki();

    public abstract double lagTimeInSeconds();

    public abstract double minVelocity();

    public abstract double maxVelocity();

    public abstract double minIntegralError();

    public abstract double maxIntegralError();

    /**
     * {@link Builder#kp}, {@link Builder#kd()}, {@link Builder#ki} are mandatory. All other parameters are optional.
     *
     * @return a builder
     */
    public static Builder builder() {
        return new AutoValue_PidParameters.Builder()
                .setLagTimeInSeconds(0)
                .setMinVelocity(-Double.MAX_VALUE)
                .setMaxVelocity(Double.MAX_VALUE)
                .setMinIntegralError(-Double.MAX_VALUE)
                .setMaxIntegralError(Double.MAX_VALUE);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setKp(double value);

        public abstract Builder setKd(double value);

        public abstract Builder setKi(double value);

        public abstract Builder setLagTimeInSeconds(double value);

        public abstract Builder setMinVelocity(double value);

        public abstract Builder setMaxVelocity(double value);

        public abstract Builder setMinIntegralError(double value);

        public abstract Builder setMaxIntegralError(double value);

        public abstract PidParameters build();
    }
}
