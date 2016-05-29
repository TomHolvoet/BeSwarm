package bebopbehavior;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Hoang Tung Dinh
 */
public final class Pose {
    private final double x;
    private final double y;
    private final double z;
    private final double yaw;

    private Pose(Builder builder) {
        x = builder.x;
        y = builder.y;
        z = builder.z;
        yaw = builder.yaw;
    }

    public static Builder builder() {
        return new Builder();
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public double yaw() {
        return yaw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pose pose = (Pose) o;
        return Double.compare(pose.x, x) == 0 &&
                Double.compare(pose.y, y) == 0 &&
                Double.compare(pose.z, z) == 0 &&
                Double.compare(pose.yaw, yaw) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y, z, yaw);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("x", x).add("y", y).add("z", z).add("yaw", yaw).toString();
    }

    /**
     * {@code Pose} builder static inner class.
     */
    public static final class Builder {
        private double x;
        private double y;
        private double z;
        private double yaw;

        private Builder() {}

        /**
         * Sets the {@code x} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code x} to set
         * @return a reference to this Builder
         */
        public Builder x(double val) {
            x = val;
            return this;
        }

        /**
         * Sets the {@code y} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code y} to set
         * @return a reference to this Builder
         */
        public Builder y(double val) {
            y = val;
            return this;
        }

        /**
         * Sets the {@code z} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code z} to set
         * @return a reference to this Builder
         */
        public Builder z(double val) {
            z = val;
            return this;
        }

        /**
         * Sets the {@code yaw} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code yaw} to set
         * @return a reference to this Builder
         */
        public Builder yaw(double val) {
            yaw = val;
            return this;
        }

        /**
         * Returns a {@code Pose} built from the parameters previously set.
         *
         * @return a {@code Pose} built with parameters of this {@code Pose.Builder}
         */
        public Pose build() {
            return new Pose(this);
        }
    }
}
