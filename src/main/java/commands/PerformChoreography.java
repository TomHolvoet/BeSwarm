package commands;

import control.FiniteTrajectory4d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hoang Tung Dinh
 */
public final class PerformChoreography implements Command {

    private static final Logger logger = LoggerFactory.getLogger(PerformChoreography.class);
    private final FollowTrajectory followTrajectoryCommand;

    private PerformChoreography(FollowTrajectory followTrajectoryCommand) {
        this.followTrajectoryCommand = followTrajectoryCommand;
    }

    /**
     * Returns a builder of this class.
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void execute() {
        logger.debug("Execute perform choreography command.");
        followTrajectoryCommand.execute();
    }

    /**
     * Builder for {@link PerformChoreography}.
     */
    public static final class Builder extends AbstractFollowTrajectoryBuilder<Builder> {

        private FiniteTrajectory4d finiteTrajectory4d;

        private Builder() {
            super();
        }

        @Override
        Builder self() {
            return this;
        }

        /**
         * Sets the finite trajectory.
         */
        public Builder withFiniteTrajectory4d(FiniteTrajectory4d val) {
            finiteTrajectory4d = val;
            return this;
        }

        /**
         * Builds a {@link PerformChoreography} instance.
         */
        public PerformChoreography build() {
            final FollowTrajectory followTrajectory = FollowTrajectory.copyBuilder(this)
                    .withTrajectory4d(finiteTrajectory4d)
                    .withDurationInSeconds(finiteTrajectory4d.getTrajectoryDuration())
                    .build();
            return new PerformChoreography(followTrajectory);
        }
    }
}
