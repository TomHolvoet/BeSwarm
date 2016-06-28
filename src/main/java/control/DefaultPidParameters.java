package control;

/**
 * @author Hoang Tung Dinh
 */
public enum DefaultPidParameters {
    LINEAR_X {
        @Override
        public PidParameters getParameters() {
            return PidParameters.builder().kp(0.6).kd(0.3).ki(0).lagTimeInSeconds(0.2).build();
        }
    },

    LINEAR_Y {
        @Override
        public PidParameters getParameters() {
            return PidParameters.builder().kp(0.6).kd(0.3).ki(0).lagTimeInSeconds(0.2).build();
        }
    },

    LINEAR_Z {
        @Override
        public PidParameters getParameters() {
            return PidParameters.builder().kp(0.6).kd(0.3).ki(0).lagTimeInSeconds(0.2).build();
        }
    },

    ANGULAR_Z {
        @Override
        public PidParameters getParameters() {
            return PidParameters.builder().kp(0.5).kd(0.25).ki(0).lagTimeInSeconds(0.2).build();
        }
    };

    public abstract PidParameters getParameters();
}
