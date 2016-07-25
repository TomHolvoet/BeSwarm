package commands;

import applications.trajectory.Trajectories;
import applications.trajectory.points.Point4D;
import control.Trajectory4d;
import control.dto.Pose;

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

    /**
     * Gets a builder of this class.
     *
     * @return a builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void execute() {
        followTrajectoryCommand.execute();
    }

    /**
     * Builder for this class.
     */
    public static final class Builder extends AbstractFollowTrajectoryBuilder<Builder> {
        private Pose goalPose;
        private double durationInSeconds;

        private Builder() {}

        @Override
        Builder self() {
            return this;
        }

        /**
         * Sets the goal pose needed to be reached.
         *
         * @param val the value to set
         * @return a reference to this Builder
         */
        public Builder withGoalPose(Pose val) {
            goalPose = val;
            return this;
        }

        /**
         * Set the duration for executing this command.
         *
         * @param val the value to set
         * @return a reference to this Builder
         */
        public Builder withDurationInSeconds(double val) {
            durationInSeconds = val;
            return this;
        }

        /**
         * Builds a {@link MoveToPose} instance.
         *
         * @return a built {@link MoveToPose} instance
         */
        public MoveToPose build() {
            final Trajectory4d trajectory4d = Trajectories.newHoldPositionTrajectory(Point4D.from(goalPose));
            final FollowTrajectory followTrajectory = FollowTrajectory.copyBuilder(this)
                    .withTrajectory4d(trajectory4d)
                    .withDurationInSeconds(durationInSeconds)
                    .build();
            return new MoveToPose(followTrajectory);
        }
    }
}
