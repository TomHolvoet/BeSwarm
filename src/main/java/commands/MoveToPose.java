package commands;

import control.Trajectory4d;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;

/**
 * Command for moving to a predefined pose. It is a facade which uses {@link FollowTrajectory}.
 *
 * @author Hoang Tung Dinh
 */
public final class MoveToPose implements Command {
    private final FollowTrajectory followTrajectoryCommand;

    private MoveToPose(FollowTrajectory followTrajectoryCommand) {
        this.followTrajectoryCommand = followTrajectoryCommand;
    }

    public static CommandBuilders.VelocityServiceStep<GoalPoseStep> builder() {
        return new Builder();
    }

    @Override
    public void execute() {
        followTrajectoryCommand.execute();
    }

    public interface GoalPoseStep {
        DurationInSecondsStep withGoalPose(Pose val);
    }

    public interface DurationInSecondsStep {
        CommandBuilders.BuildStep<MoveToPose> withDurationInSeconds(double val);
    }

    public static final class Builder extends CommandBuilders.AbstractFollowTrajectoryBuilder<GoalPoseStep,
            MoveToPose> implements GoalPoseStep, DurationInSecondsStep {
        private Pose goalPose;
        private double durationInSeconds;

        private Builder() {
            super();
        }

        @Override
        GoalPoseStep nextInterfaceInBuilderChain() {
            return this;
        }

        @Override
        public DurationInSecondsStep withGoalPose(Pose val) {
            goalPose = val;
            return this;
        }

        @Override
        public CommandBuilders.BuildStep<MoveToPose> withDurationInSeconds(double val) {
            durationInSeconds = val;
            return this;
        }

        @Override
        public MoveToPose build() {
            final InertialFrameVelocity zeroVelocity = Velocity.createZeroVelocity();
            final Trajectory4d trajectory4d = SinglePointTrajectory4d.create(goalPose, zeroVelocity);
            final FollowTrajectory followTrajectory = FollowTrajectory.copyBuilder(this)
                    .withTrajectory4d(trajectory4d)
                    .withDurationInSeconds(durationInSeconds)
                    .build();
            return new MoveToPose(followTrajectory);
        }
    }
}
