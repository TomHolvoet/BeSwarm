package choreo;

import applications.trajectory.BasicTrajectory;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import control.FiniteTrajectory4d;
import control.Trajectory4d;

import java.util.List;
import java.util.Queue;

/**
 * A choreography represents a sequence of different trajectories to be
 * executed for set durations.
 * The choreography is in itself a trajectory4d instance so it can be used as
 * a single trajectory from the lower level control point-of-view.
 * Using the builder, one can create choreography instances and configure
 * them with different trajectories to be executed in sequence.
 *
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public final class Choreography extends BasicTrajectory
        implements FiniteTrajectory4d {
    private final ImmutableList<ChoreoSegment> initialSegments;
    private final Queue<ChoreoSegment> segments;
    private double timeWindowShift;

    private Choreography(List<ChoreoSegment> segmentsArg) {
        super();
        initialSegments = ImmutableList.copyOf(segmentsArg);
        segments = Queues.newArrayDeque(segmentsArg);
        timeWindowShift = 0d;
    }

    private void checkChoreoSegments(double timeInSeconds) {
        double normTime = normalize(timeInSeconds);
        if (normTime >= getCurrentSegment().getDuration()) {
            shiftSegments();
        }
    }

    private void shiftSegments() {
        if (this.segments.size() > 1) {
            this.timeWindowShift += segments.poll().getDuration();
        }
    }

    private double normalize(double timeInSeconds) {
        return timeInSeconds - timeWindowShift;
    }

    private ChoreoSegment getCurrentSegment() {
        return segments.peek();
    }

    @Override
    public double getDesiredPositionX(double timeInSeconds) {
        setStartTime(timeInSeconds);
        final double currentTime = timeInSeconds - getStartTime();
        checkChoreoSegments(currentTime);
        return getCurrentSegment().getTarget()
                .getDesiredPositionX(currentTime);
    }

    @Override
    public double getDesiredVelocityX(double timeInSeconds) {
        setStartTime(timeInSeconds);
        final double currentTime = timeInSeconds - getStartTime();
        checkChoreoSegments(currentTime);
        return getCurrentSegment().getTarget()
                .getDesiredVelocityX(currentTime);
    }

    @Override
    public double getDesiredPositionY(double timeInSeconds) {
        setStartTime(timeInSeconds);
        final double currentTime = timeInSeconds - getStartTime();
        checkChoreoSegments(currentTime);
        return getCurrentSegment().getTarget()
                .getDesiredPositionY(currentTime);
    }

    @Override
    public double getDesiredVelocityY(double timeInSeconds) {
        setStartTime(timeInSeconds);
        final double currentTime = timeInSeconds - getStartTime();
        checkChoreoSegments(currentTime);
        return getCurrentSegment().getTarget()
                .getDesiredVelocityY(currentTime);
    }

    @Override
    public double getDesiredPositionZ(double timeInSeconds) {
        setStartTime(timeInSeconds);
        final double currentTime = timeInSeconds - getStartTime();
        checkChoreoSegments(currentTime);
        return getCurrentSegment().getTarget()
                .getDesiredPositionZ(currentTime);
    }

    @Override
    public double getDesiredVelocityZ(double timeInSeconds) {
        setStartTime(timeInSeconds);
        final double currentTime = timeInSeconds - getStartTime();
        checkChoreoSegments(currentTime);
        return getCurrentSegment().getTarget()
                .getDesiredVelocityZ(currentTime);
    }

    @Override
    public double getDesiredAngleZ(double timeInSeconds) {
        setStartTime(timeInSeconds);
        final double currentTime = timeInSeconds - getStartTime();
        checkChoreoSegments(currentTime);
        return getCurrentSegment().getTarget().getDesiredAngleZ(currentTime);
    }

    @Override
    public double getDesiredAngularVelocityZ(double timeInSeconds) {
        setStartTime(timeInSeconds);
        final double currentTime = timeInSeconds - getStartTime();
        checkChoreoSegments(currentTime);
        return getCurrentSegment().getTarget()
                .getDesiredAngularVelocityZ(currentTime);
    }

    @Override
    public String toString() {
        return "Choreography{" +
                "Choreo segments=" + initialSegments +
                '}';
    }

    /**
     * @return A choreography builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public double getTrajectoryDuration() {
        double totalDuration = 0;
        for (ChoreoSegment s : initialSegments) {
            totalDuration += s.getDuration();
        }
        return totalDuration;
    }

    /**
     * A segment in the choreography specified by a target trajectory and a
     * duration for which to execute this trajectory.
     */
    @AutoValue
    abstract static class ChoreoSegment {
        /**
         * @return The trajectory to be executed in this segment.
         */
        public abstract Trajectory4d getTarget();

        /**
         * @return The duration this trajectory should be executed for.
         */
        public abstract double getDuration();
    }

    public static interface BuildableStepBuilder {
        public BuildableStepBuilder withTrajectory(FiniteTrajectory4d trajectory);

        public TimingRequiredStepBuilder withTrajectory(Trajectory4d trajectory);

        public Choreography build();
    }

    public static interface TimingRequiredStepBuilder {
        public BuildableStepBuilder forTime(double duration);
    }

    /**
     * Builder class for building choreography instances.
     */
    public static class Builder implements BuildableStepBuilder, TimingRequiredStepBuilder {

        private final List<ChoreoSegment> segments;
        private Trajectory4d tempTarget;

        /**
         * Creates a new Builder for choreographies.
         */
        private Builder() {
            this.segments = Lists.newArrayList();
        }

        /**
         * @param trajectory The trajectory to add to the choreography.
         * @return A segmentBuilder instance to specify the duration to
         * execute given trajectory with.
         */
        @Override
        public TimingRequiredStepBuilder withTrajectory(Trajectory4d trajectory) {
            this.tempTarget = trajectory;
            return this;
        }

        /**
         * @param trajectory The trajectory to add.
         * @return this builder instance.
         */
        @Override
        public BuildableStepBuilder withTrajectory(FiniteTrajectory4d trajectory) {
            addSegmentWithDuration(trajectory, trajectory.getTrajectoryDuration());
            return this;
        }

        /**
         * @return A fully built choreography instance.
         */
        @Override
        public Choreography build() {
            return new Choreography(segments);
        }

        /**
         * @param duration the duration of the previously added trajectory.
         * @return A Builder instance.
         */
        @Override
        public BuildableStepBuilder forTime(double duration) {
            addSegmentWithDuration(tempTarget, duration);
            return this;
        }

        private void addSegmentWithDuration(Trajectory4d target, double duration) {
            segments.add(new AutoValue_Choreography_ChoreoSegment(target, duration));
        }
    }
}