package control;

/**
 * The one-dimensional PID controller.
 *
 * @author Hoang Tung Dinh
 * @see <a href="https://en.wikipedia.org/wiki/PID_controller">Equation</a>
 */
final class PidController1d {

    private final PidParameters parameters;
    private final Trajectory1d trajectory;

    private double lastTimeInSeconds = -1;
    private double accumulatedError = 0;

    private PidController1d(PidParameters parameters, Trajectory1d trajectory) {
        this.parameters = parameters;
        this.trajectory = trajectory;
    }

    public static PidController1d create(PidParameters parameters, Trajectory1d trajectory) {
        return new PidController1d(parameters, trajectory);
    }

    /**
     * Compute the next velocity (response) of the control loop.
     *
     * @param currentPoint         the current position of the drone
     * @param currentVelocity      the current velocity of the drone
     * @param currentTimeInSeconds the current time
     * @return the next velocity (response) of the drone
     * @see <a href="https://en.wikipedia.org/wiki/PID_controller">Equation</a>
     */
    double compute(double currentPoint, double currentVelocity, double currentTimeInSeconds) {
        final double desiredTimeInSeconds = currentTimeInSeconds + parameters.lagTimeInSeconds();
        final double error = trajectory.getDesiredPosition(desiredTimeInSeconds) - currentPoint;

        updateAccumulatedError(desiredTimeInSeconds, error);

        final double pTerm = parameters.kp() * error;
        final double dTerm = parameters.kd() * (trajectory.getDesiredVelocity(desiredTimeInSeconds) - currentVelocity);
        final double iTerm = parameters.ki() * accumulatedError;

        double outVelocity = pTerm + dTerm + iTerm;

        if (outVelocity > parameters.maxVelocity()) {
            outVelocity = parameters.maxVelocity();
        } else if (outVelocity < parameters.minVelocity()) {
            outVelocity = parameters.minVelocity();
        }

        return outVelocity;
    }

    private void updateAccumulatedError(double currentTimeInSeconds, double error) {
        final double dtInSeconds;
        if (lastTimeInSeconds < 0) {
            lastTimeInSeconds = currentTimeInSeconds;
            dtInSeconds = 0;
        } else {
            dtInSeconds = currentTimeInSeconds - lastTimeInSeconds;
            lastTimeInSeconds = currentTimeInSeconds;
        }

        accumulatedError += error * dtInSeconds;

        if (accumulatedError > parameters.maxIntegralError()) {
            accumulatedError = parameters.maxIntegralError();
        } else if (accumulatedError < parameters.minIntegralError()) {
            accumulatedError = parameters.minIntegralError();
        }
    }

}
