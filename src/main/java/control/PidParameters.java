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
        return new AutoValue_PidParameters.Builder().minVelocity(-Double.MAX_VALUE)
                .maxVelocity(Double.MAX_VALUE)
                .minIntegralError(-Double.MAX_VALUE)
                .maxIntegralError(Double.MAX_VALUE);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder kp(double value);

        public abstract Builder kd(double value);

        public abstract Builder ki(double value);

        public abstract Builder minVelocity(double value);

        public abstract Builder maxVelocity(double value);

        public abstract Builder minIntegralError(double value);

        public abstract Builder maxIntegralError(double value);

        public abstract PidParameters build();
    }
}
