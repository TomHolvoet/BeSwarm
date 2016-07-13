package applications;

import com.google.common.collect.ImmutableList;
import commands.Command;
import commands.FollowTrajectory;
import commands.Hover;
import commands.Land;
import commands.Takeoff;
import commands.WaitForLocalizationDecorator;
import control.DefaultPidParameters;
import control.FiniteTrajectory4d;
import control.PidParameters;
import control.localization.StateEstimator;
import keyboard.Key;
import org.ros.node.ConnectedNode;
import services.FlyingStateService;
import services.LandService;
import services.TakeOffService;
import services.VelocityService;
import services.rossubscribers.KeyboardSubscriber;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;
import taskexecutor.interruptors.KeyboardEmergency;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class illustrates an example flight. The drone will take off, hover in 5 second, follow a trajectory and then
 * land.
 *
 * @author Hoang Tung Dinh
 */
public final class ExampleFlight {

    private final TakeOffService takeOffService;
    private final LandService landService;
    private final VelocityService velocityService;
    private final FlyingStateService flyingStateService;
    private final StateEstimator stateEstimator;
    private final FiniteTrajectory4d finiteTrajectory4d;
    private final ConnectedNode connectedNode;

    private final PidParameters pidLinearX;
    private final PidParameters pidLinearY;
    private final PidParameters pidLinearZ;
    private final PidParameters pidAngularZ;

    private ExampleFlight(Builder builder) {
        takeOffService = builder.takeOffService;
        landService = builder.landService;
        velocityService = builder.velocityService;
        flyingStateService = builder.flyingStateService;
        stateEstimator = builder.stateEstimator;
        finiteTrajectory4d = builder.finiteTrajectory4d;
        connectedNode = builder.connectedNode;
        pidLinearX = builder.pidLinearX;
        pidLinearY = builder.pidLinearY;
        pidLinearZ = builder.pidLinearZ;
        pidAngularZ = builder.pidAngularZ;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void fly() {
        // task to execute in case of emergency
        final Task emergencyTask = createEmergencyTask();
        final KeyboardEmergency keyboardEmergencyNotifier = createKeyboardEmergencyNotifier(emergencyTask);

        final TaskExecutor taskExecutor = TaskExecutorService.create();
        keyboardEmergencyNotifier.registerTaskExecutor(taskExecutor);

        // normal fly task
        final Task flyTask = createFlyTask();
        taskExecutor.submitTask(flyTask);
    }

    private Task createFlyTask() {
        final Collection<Command> commands = new ArrayList<>();

        final Command takeOff = Takeoff.create(takeOffService);
        commands.add(takeOff);

        final Command hoverFiveSecond = Hover.create(velocityService, stateEstimator, 5);
        commands.add(hoverFiveSecond);

        final Command followTrajectory = FollowTrajectory.builder()
                .withVelocityService(velocityService)
                .withStateEstimator(stateEstimator)
                .withTrajectory4d(finiteTrajectory4d)
                .withDurationInSeconds(finiteTrajectory4d.getTrajectoryDuration())
                .withPidLinearXParameters(pidLinearX)
                .withPidLinearYParameters(pidLinearY)
                .withPidLinearZParameters(pidLinearZ)
                .withPidAngularZParameters(pidAngularZ)
                .build();

        final Command waitForLocalizationThenFollowTrajectory = WaitForLocalizationDecorator.create(stateEstimator,
                followTrajectory);

        commands.add(waitForLocalizationThenFollowTrajectory);

        final Command land = Land.create(landService, flyingStateService);
        commands.add(land);

        return Task.create(ImmutableList.copyOf(commands), TaskType.NORMAL_TASK);
    }

    private Task createEmergencyTask() {
        final Command land = Land.create(landService, flyingStateService);
        return Task.create(ImmutableList.of(land), TaskType.FIRST_ORDER_EMERGENCY);
    }

    private KeyboardEmergency createKeyboardEmergencyNotifier(Task emergencyTask) {
        final KeyboardSubscriber keyboardSubscriber = KeyboardSubscriber.createKeyboardSubscriber(
                connectedNode.<Key>newSubscriber("/keyboard/keydown", Key._TYPE));
        final KeyboardEmergency keyboardEmergency = KeyboardEmergency.create(emergencyTask);
        keyboardSubscriber.registerObserver(keyboardEmergency);
        return keyboardEmergency;
    }

    /**
     * {@code ExampleFlight} builder static inner class.
     */
    public static final class Builder {
        private TakeOffService takeOffService;
        private LandService landService;
        private VelocityService velocityService;
        private FlyingStateService flyingStateService;
        private StateEstimator stateEstimator;
        private FiniteTrajectory4d finiteTrajectory4d;
        private ConnectedNode connectedNode;
        private PidParameters pidLinearX;
        private PidParameters pidLinearY;
        private PidParameters pidLinearZ;
        private PidParameters pidAngularZ;

        private Builder() {
            pidLinearX = DefaultPidParameters.LINEAR_X.getParameters();
            pidLinearY = DefaultPidParameters.LINEAR_Y.getParameters();
            pidLinearZ = DefaultPidParameters.LINEAR_Z.getParameters();
            pidAngularZ = DefaultPidParameters.ANGULAR_Z.getParameters();
        }

        /**
         * Sets the {@code takeOffService} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code takeOffService} to set
         * @return a reference to this Builder
         */
        public Builder withTakeOffService(TakeOffService val) {
            takeOffService = val;
            return this;
        }

        /**
         * Sets the {@code landService} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code landService} to set
         * @return a reference to this Builder
         */
        public Builder withLandService(LandService val) {
            landService = val;
            return this;
        }

        /**
         * Sets the {@code velocityService} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code velocityService} to set
         * @return a reference to this Builder
         */
        public Builder withVelocityService(VelocityService val) {
            velocityService = val;
            return this;
        }

        /**
         * Sets the {@code flyingStateService} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code flyingStateService} to set
         * @return a reference to this Builder
         */
        public Builder withFlyingStateService(FlyingStateService val) {
            flyingStateService = val;
            return this;
        }

        /**
         * Sets the {@code stateEstimator} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code stateEstimator} to set
         * @return a reference to this Builder
         */
        public Builder withStateEstimator(StateEstimator val) {
            stateEstimator = val;
            return this;
        }

        /**
         * Sets the {@code finiteTrajectory4d} and returns a reference to this Builder so that the methods can be
         * chained together.
         *
         * @param val the {@code finiteTrajectory4d} to set
         * @return a reference to this Builder
         */
        public Builder withFiniteTrajectory4d(FiniteTrajectory4d val) {
            finiteTrajectory4d = val;
            return this;
        }

        /**
         * Sets the {@code connectedNode} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code connectedNode} to set
         * @return a reference to this Builder
         */
        public Builder withConnectedNode(ConnectedNode val) {
            connectedNode = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearX} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code pidLinearX} to set
         * @return a reference to this Builder
         */
        public Builder withPidLinearX(PidParameters val) {
            pidLinearX = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearY} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code pidLinearY} to set
         * @return a reference to this Builder
         */
        public Builder withPidLinearY(PidParameters val) {
            pidLinearY = val;
            return this;
        }

        /**
         * Sets the {@code pidLinearZ} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code pidLinearZ} to set
         * @return a reference to this Builder
         */
        public Builder withPidLinearZ(PidParameters val) {
            pidLinearZ = val;
            return this;
        }

        /**
         * Sets the {@code pidAngularZ} and returns a reference to this Builder so that the methods can be chained
         * together.
         *
         * @param val the {@code pidAngularZ} to set
         * @return a reference to this Builder
         */
        public Builder withPidAngularZ(PidParameters val) {
            pidAngularZ = val;
            return this;
        }

        /**
         * Returns a {@code ExampleFlight} built from the parameters previously set.
         *
         * @return a {@code ExampleFlight} built with parameters of this {@code ExampleFlight.Builder}
         */
        public ExampleFlight build() {
            checkNotNull(takeOffService);
            checkNotNull(landService);
            checkNotNull(velocityService);
            checkNotNull(flyingStateService);
            checkNotNull(stateEstimator);
            checkNotNull(finiteTrajectory4d);
            checkNotNull(connectedNode);
            checkNotNull(pidLinearX);
            checkNotNull(pidLinearY);
            checkNotNull(pidLinearZ);
            checkNotNull(pidAngularZ);
            return new ExampleFlight(this);
        }
    }
}
