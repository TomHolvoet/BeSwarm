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

    public static CommandBuilders.VelocityServiceStep<FiniteTrajectory4dStep> builder() {
        return new Builder();
    }

    @Override
    public void execute() {
        logger.debug("Execute perform choreography command.");
        followTrajectoryCommand.execute();
    }

    public interface FiniteTrajectory4dStep {
        CommandBuilders.BuildStep<PerformChoreography> withFiniteTrajectory4d(FiniteTrajectory4d val);
    }

    public static final class Builder extends CommandBuilders.AbstractFollowTrajectoryBuilder<FiniteTrajectory4dStep,
            PerformChoreography> implements FiniteTrajectory4dStep {

        private FiniteTrajectory4d finiteTrajectory4d;

        private Builder() {
            super();
        }

        @Override
        FiniteTrajectory4dStep nextInterfaceInBuilderChain() {
            return this;
        }

        @Override
        public CommandBuilders.BuildStep<PerformChoreography> withFiniteTrajectory4d(FiniteTrajectory4d val) {
            finiteTrajectory4d = val;
            return null;
        }

        @Override
        public PerformChoreography build() {
            final FollowTrajectory followTrajectory = FollowTrajectory.copyBuilder(this)
                    .withTrajectory4d(finiteTrajectory4d)
                    .withDurationInSeconds(finiteTrajectory4d.getTrajectoryDuration())
                    .build();
            return new PerformChoreography(followTrajectory);
        }
    }
}
