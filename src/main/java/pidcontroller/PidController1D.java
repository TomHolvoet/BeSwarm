package pidcontroller;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
final class PidController1D {

    private final PidParameters parameters;
    private final double goalPoint;
    private final double goalVelocity;

    private double accumulatedError = 0;

    private PidController1D(Builder builder) {
        parameters = builder.parameters;
        goalPoint = builder.goalPoint;
        goalVelocity = builder.goalVelocity;

        checkArgument(goalVelocity <= parameters.maxVelocity() && goalVelocity >= parameters.minVelocity(),
                "goal velocity must be in velocity range.");
    }

    public static Builder builder() {
        return new Builder();
    }

    double compute(double currentPoint, double currentVelocity) {
        final double error = goalPoint - currentPoint;

        updateAccumulatedError(error);

        final double pTerm = parameters.kp() * error;
        final double dTerm = parameters.kd() * (goalVelocity - currentVelocity);
        final double iTerm = parameters.ki() * accumulatedError;

        double outVelocity = pTerm + dTerm + iTerm;

        if (outVelocity > parameters.maxVelocity()) {
            outVelocity = parameters.maxVelocity();
        } else if (outVelocity < parameters.minVelocity()) {
            outVelocity = parameters.minVelocity();
        }

        return outVelocity;
    }

    private void updateAccumulatedError(double error) {
        accumulatedError += error;

        if (accumulatedError > parameters.maxIntegralError()) {
            accumulatedError = parameters.maxIntegralError();
        } else if (accumulatedError < parameters.minIntegralError()) {
            accumulatedError = parameters.minIntegralError();
        }
    }

    /**
     * {@code PidController1D} builder static inner class.
     */
    public static final class Builder {
        private PidParameters parameters;
        private double goalPoint;
        private double goalVelocity;

        private Builder() {}

        /**
         * Sets the {@code parameters} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code parameters} to set
         * @return a reference to this Builder
         */
        public Builder parameters(PidParameters val) {
            parameters = val;
            return this;
        }

        /**
         * Sets the {@code goalPoint} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code goalPoint} to set
         * @return a reference to this Builder
         */
        public Builder goalPoint(double val) {
            goalPoint = val;
            return this;
        }

        /**
         * Sets the {@code goalVelocity} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code goalVelocity} to set
         * @return a reference to this Builder
         */
        public Builder goalVelocity(double val) {
            goalVelocity = val;
            return this;
        }

        /**
         * Returns a {@code PidController1D} built from the parameters previously set.
         *
         * @return a {@code PidController1D} built with parameters of this {@code PidController1D.Builder}
         */
        public PidController1D build() {
            return new PidController1D(this);
        }
    }
}
