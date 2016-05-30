package bebopbehavior;

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

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder linearX(double value);

        public abstract Builder linearY(double value);

        public abstract Builder linearZ(double value);

        public abstract Builder angularZ(double value);

        public abstract Velocity build();
    }
}
