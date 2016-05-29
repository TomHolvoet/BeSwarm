package geom;

/**
 * @author Hoang Tung Dinh
 */
public final class EulerAngle {
    private final double angleX;
    private final double angleY;
    private final double angleZ;

    private EulerAngle(Builder builder) {
        angleX = builder.angleX;
        angleY = builder.angleY;
        angleZ = builder.angleZ;
    }

    public static Builder builder() {
        return new Builder();
    }

    public double angleX() {
        return angleX;
    }

    public double angleY() {
        return angleY;
    }

    public double angleZ() {
        return angleZ;
    }

    /**
     * {@code EulerAngle} builder static inner class.
     */
    public static final class Builder {
        private double angleX;
        private double angleY;
        private double angleZ;

        private Builder() {}

        /**
         * Sets the {@code angleX} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code angleX} to set
         * @return a reference to this Builder
         */
        public Builder angleX(double val) {
            angleX = val;
            return this;
        }

        /**
         * Sets the {@code angleY} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code angleY} to set
         * @return a reference to this Builder
         */
        public Builder angleY(double val) {
            angleY = val;
            return this;
        }

        /**
         * Sets the {@code angleZ} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code angleZ} to set
         * @return a reference to this Builder
         */
        public Builder angleZ(double val) {
            angleZ = val;
            return this;
        }

        /**
         * Returns a {@code EulerAngle} built from the parameters previously set.
         *
         * @return a {@code EulerAngle} built with parameters of this {@code EulerAngle.Builder}
         */
        public EulerAngle build() {
            return new EulerAngle(this);
        }
    }
}
