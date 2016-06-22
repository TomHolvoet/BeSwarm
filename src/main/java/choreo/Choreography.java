package choreo;

import com.google.auto.value.AutoValue;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import control.Trajectory1d;
import control.Trajectory4d;

import java.util.List;
import java.util.Queue;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 */
public class Choreography implements Trajectory4d {
    private final Queue<ChoreoSegment> segments;
    private double timeWindowShift;

    private Choreography(List<ChoreoSegment> segmentsArg) {
        segments = Queues.newArrayDeque(segmentsArg);
        timeWindowShift = 0d;
    }

    @Override
    public Trajectory1d getTrajectoryLinearX() {
        return segments.peek().getTarget().getTrajectoryLinearX();
    }

    @Override
    public Trajectory1d getTrajectoryLinearY() {
        return segments.peek().getTarget().getTrajectoryLinearY();
    }

    @Override
    public Trajectory1d getTrajectoryLinearZ() {
        return segments.peek().getTarget().getTrajectoryLinearZ();
    }

    @Override
    public Trajectory1d getTrajectoryAngularZ() {
        return segments.peek().getTarget().getTrajectoryAngularZ();
    }

    private void fixTimingHooks(List<TimingHook> hooks) {
        for (TimingHook t : hooks) {
            t.subscribeTimingListener(new TimingListener() {
                @Override
                public void notifyNewTiming(double timeInSeconds) {
                    checkChoreoSegments(timeInSeconds);
                }
            });
        }
    }

    /**
     * @return A choreography builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    private void checkChoreoSegments(double timeInSeconds) {
        double normTime = normalize(timeInSeconds);
        if (normTime > getCurrentSegment().getDuration()) {
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

    private static class TimingHook extends Trajectory4DForwardingDecorator {
        private final List<TimingListener> listeners;

        private TimingHook(Trajectory4d target) {
            super(target);
            listeners = Lists.newArrayList();
        }

        @Override
        protected void velocityDelegate(double timeInSeconds) {
            notify(timeInSeconds);
        }

        @Override
        protected void positionDelegate(double timeInSeconds) {
            notify(timeInSeconds);
        }

        private void notify(double timeInSeconds) {
            for (TimingListener t : listeners) {
                t.notifyNewTiming(timeInSeconds);
            }
        }

        void subscribeTimingListener(TimingListener t) {
            this.listeners.add(t);
        }
    }

    /**
     * A segment in the choreography specified by a target trajectory and a
     * duration for which to execute this trajectory.
     */
    @AutoValue
    public static abstract class ChoreoSegment {
        /**
         * @return The trajectory to be executed in this segment.
         */
        public abstract Trajectory4d getTarget();

        /**
         * @return The duration this trajectory should be executed for.
         */
        public abstract double getDuration();
    }

    public static class Builder {
        private final List<ChoreoSegment> segments;

        public Builder() {
            this.segments = Lists.newArrayList();
        }

        /**
         * @param trajectory The trajectory to add to the choreography.
         * @return A segmentBuilder instance to specify the duration to
         * execute given trajectory with.
         */
        public SegmentBuilder withTrajectory(Trajectory4d trajectory) {
            return new SegmentBuilder(trajectory);
        }

        private Builder getBuilder() {
            return this;
        }

        /**
         * @return A fully built choreography instance.
         */
        public Choreography build() {
            List<ChoreoSegment> newSegments = Lists.newArrayList();
            List<TimingHook> th = Lists.newArrayList();
            for (ChoreoSegment s : segments) {
                TimingHook t = new TimingHook(s.getTarget());
                th.add(t);
                newSegments.add(new AutoValue_Choreography_ChoreoSegment(t,
                        s.getDuration()));
            }
            Choreography choreography = new Choreography(newSegments);
            choreography.fixTimingHooks(th);
            return choreography;
        }

        public class SegmentBuilder {

            private final Trajectory4d target;

            private SegmentBuilder(Trajectory4d target) {
                this.target = target;
            }

            /**
             * @param duration the duration of the previously added trajectory.
             * @return A Builder instance.
             */
            public Builder forTime(double duration) {
                segments.add(new AutoValue_Choreography_ChoreoSegment(target,
                        duration));
                return getBuilder();
            }
        }
    }
}
