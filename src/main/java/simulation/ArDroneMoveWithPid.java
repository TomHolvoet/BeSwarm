package simulation;

import behavior.Command;
import behavior.Hover;
import behavior.Land;
import behavior.MoveToPose;
import behavior.Pose;
import behavior.Takeoff;
import behavior.Velocity;
import com.google.common.collect.ImmutableList;
import comm.LandPublisher;
import comm.ModelStateSubscriber;
import comm.TakeoffPublisher;
import comm.VelocityPublisher;
import control.ModelStatePoseProvider;
import control.ModelStateVelocityProvider;
import control.PoseProvider;
import control.VelocityProvider;
import gazebo_msgs.ModelStates;
import geometry_msgs.Twist;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import std_msgs.Empty;
import taskexecutor.SimpleEmergencyAfterOneMinute;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is for running the simulation with the AR drone.
 *
 * @author Hoang Tung Dinh
 * @see <a href="https://github.com/dougvk/tum_simulator">The simulator</a>
 */
public final class ArDroneMoveWithPid extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ArDroneMoveWithPid");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        // FIXME refactor this code to separate concern, use atomic data structure.
        final TakeoffPublisher takeoffPublisher = TakeoffPublisher.create(
                connectedNode.<Empty>newPublisher("/ardrone/takeoff", Empty._TYPE));
        final VelocityPublisher velocityPublisher = VelocityPublisher.builder()
                .publisher(connectedNode.<Twist>newPublisher("/cmd_vel", Twist._TYPE))
                .minLinearX(-1)
                .minLinearY(-1)
                .minLinearZ(-1)
                .minAngularZ(-1)
                .maxLinearX(1)
                .maxLinearY(1)
                .maxLinearZ(1)
                .maxAngularZ(1)
                .build();
        final ModelStateSubscriber modelStateSubscriber = ModelStateSubscriber.create(
                connectedNode.<ModelStates>newSubscriber("/gazebo/model_states", ModelStates._TYPE));
        final LandPublisher landPublisher = LandPublisher.create(
                connectedNode.<Empty>newPublisher("/ardrone/land", Empty._TYPE));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO write to log
        }

        final Collection<Command> commands = new ArrayList<>();

        final Command takeOff = Takeoff.create(takeoffPublisher);
        commands.add(takeOff);

        final Command hoverFiveSecond = Hover.create(velocityPublisher, 5);
        commands.add(hoverFiveSecond);

        final String modelName = "quadrotor";
        final PoseProvider poseProvider = ModelStatePoseProvider.create(modelStateSubscriber, modelName);
        final VelocityProvider velocityProvider = ModelStateVelocityProvider.create(modelStateSubscriber, modelName);
        final Pose goalPose = Pose.builder().x(3).y(-3).z(3).yaw(1).build();
        final Velocity goalVelocity = Velocity.builder().linearX(0).linearY(0).linearZ(0).angularZ(0).build();
        final Command moveToPose = MoveToPose.builder()
                .poseProvider(poseProvider)
                .velocityProvider(velocityProvider)
                .velocityPublisher(velocityPublisher)
                .goalPose(goalPose)
                .goalVelocity(goalVelocity)
                .durationInSeconds(60)
                .build();
        commands.add(moveToPose);

        final Task flyTask = Task.create(ImmutableList.copyOf(commands), TaskType.NORMAL_TASK);
        final TaskExecutor taskExecutor = TaskExecutorService.create();

        final Command land = Land.create(landPublisher);
        final Task emergencyTask = Task.create(ImmutableList.<Command>of(land), TaskType.FIRST_ORDER_EMERGENCY);
        final SimpleEmergencyAfterOneMinute emergencySubject = SimpleEmergencyAfterOneMinute.create(emergencyTask, taskExecutor);

        taskExecutor.submitTask(flyTask);
        emergencySubject.run();
    }
}
