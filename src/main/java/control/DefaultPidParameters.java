package control;

/**
 * @author Hoang Tung Dinh
 */
public enum DefaultPidParameters {
    DEFAULT_LINEAR_PARAMETERS {
        @Override
        public PidParameters getParameters() {
            return PidParameters.builder().kp(2).kd(1).ki(0).build();
        }
    },

    DEFAULT_ANGULAR_PARAMETERS {
        @Override
        public PidParameters getParameters() {
            return PidParameters.builder().kp(0.5).kd(0.5).ki(0).build();
        }
    };

    public abstract PidParameters getParameters();
}
