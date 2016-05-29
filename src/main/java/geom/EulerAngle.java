package geom;

import com.google.auto.value.AutoValue;

/**
 * @author Hoang Tung Dinh
 */
@AutoValue
public abstract class EulerAngle {
    public abstract double angleX();

    public abstract double angleY();

    public abstract double angleZ();

    public static Builder builder() {
        return new AutoValue_EulerAngle.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder angleX(double value);

        public abstract Builder angleY(double value);

        public abstract Builder angleZ(double value);

        public abstract EulerAngle build();
    }
}
