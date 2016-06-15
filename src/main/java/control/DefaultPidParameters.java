package control;

/**
 * @author Hoang Tung Dinh
 */
public enum DefaultPidParameters {
    DEFAULT_LINEAR_PARAMETERS {
        @Override
        public PidParameters getParameters() {
            return PidParameters.builder().kp(2).kd(1).ki(0).lagTimeInSeconds(0.2).build();
        }
    },

    DEFAULT_ANGULAR_PARAMETERS {
        @Override
        public PidParameters getParameters() {
            return PidParameters.builder().kp(1.5).kd(0.75).ki(0).lagTimeInSeconds(0.2).build();
        }
    };

    public abstract PidParameters getParameters();
}
