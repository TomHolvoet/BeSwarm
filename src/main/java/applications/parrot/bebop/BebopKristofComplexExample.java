package applications.parrot.bebop;

import applications.ExampleFlight;
import applications.TrajectoriesForTesting;
import applications.trajectory.Trajectories;
import applications.trajectory.points.Point3D;
import applications.trajectory.points.Point4D;
import choreo.Choreography;
import control.FiniteTrajectory4d;
import control.PidParameters;
import control.Trajectory4d;
import control.localization.BebopStateEstimatorWithPoseStampedAndOdom;
import control.localization.StateEstimator;
import geometry_msgs.PoseStamped;
import nav_msgs.Odometry;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.parrot.BebopServiceFactory;
import services.parrot.ParrotServiceFactory;
import services.rossubscribers.MessagesSubscriberService;

import java.util.concurrent.TimeUnit;

/**
 * @author Hoang Tung Dinh
 */
public class BebopKristofComplexExample extends AbstractNodeMain {
    private static final Logger logger = LoggerFactory.getLogger(BebopKristofComplexExample.class);
    private static final String DRONE_NAME = "bebop";

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopSimpleLinePattern");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final PidParameters pidLinearX = getPidParameters(connectedNode, "beswarm/pid_linear_x_kp",
                "beswarm/pid_linear_x_kd", "beswarm/pid_linear_x_ki");
        final PidParameters pidLinearY = getPidParameters(connectedNode, "beswarm/pid_linear_y_kp",
                "beswarm/pid_linear_y_kd", "beswarm/pid_linear_y_ki");
        final PidParameters pidLinearZ = getPidParameters(connectedNode, "beswarm/pid_linear_z_kp",
                "beswarm/pid_linear_z_kd", "beswarm/pid_linear_z_ki");
        final PidParameters pidAngularZ = getPidParameters(connectedNode, "beswarm/pid_angular_z_kp",
                "beswarm/pid_angular_z_kd", "beswarm/pid_angular_z_ki");

        final ParrotServiceFactory parrotServiceFactory = BebopServiceFactory.create(connectedNode, DRONE_NAME);
        final StateEstimator stateEstimator = BebopStateEstimatorWithPoseStampedAndOdom.create(
                getPoseSubscriber(connectedNode), getOdometrySubscriber(connectedNode));

        final FiniteTrajectory4d trajectory4d = TrajectoriesForTesting.getSlowIndoorPendulum();

        final ExampleFlight exampleFlight = ExampleFlight.builder()
                .withConnectedNode(connectedNode)
                .withFiniteTrajectory4d(trajectory4d)
                .withFlyingStateService(parrotServiceFactory.createFlyingStateService())
                .withLandService(parrotServiceFactory.createLandService())
                .withPidLinearX(pidLinearX)
                .withPidLinearY(pidLinearY)
                .withPidLinearZ(pidLinearZ)
                .withPidAngularZ(pidAngularZ)
                .withStateEstimator(stateEstimator)
                .withTakeOffService(parrotServiceFactory.createTakeOffService())
                .withVelocityService(parrotServiceFactory.createVelocity4dService())
                .build();

        // without this code, the take off message cannot be sent properly (I don't understand why).
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            logger.info("Warm up time is interrupted.", e);
        }

        exampleFlight.fly();
    }

    private static PidParameters getPidParameters(ConnectedNode connectedNode, String argKp, String argKd,
            String argKi) {
        final double pidLinearXKp = connectedNode.getParameterTree().getDouble(argKp);
        final double pidLinearXKd = connectedNode.getParameterTree().getDouble(argKd);
        final double pidLinearXKi = connectedNode.getParameterTree().getDouble(argKi);
        return PidParameters.builder().setKp(pidLinearXKp).setKd(pidLinearXKd).setKi(pidLinearXKi).build();
    }

    private static MessagesSubscriberService<PoseStamped> getPoseSubscriber(ConnectedNode connectedNode) {
        final String poseTopic = "/arlocros/pose";
        logger.info("Subscribed to {} for getting pose.", poseTopic);
        return MessagesSubscriberService.create(connectedNode.<PoseStamped>newSubscriber(poseTopic, PoseStamped._TYPE));
    }

    private static MessagesSubscriberService<Odometry> getOdometrySubscriber(ConnectedNode connectedNode) {
        final String odometryTopic = "/" + DRONE_NAME + "/odom";
        logger.info("Subscribed to {} for getting odometry", odometryTopic);
        return MessagesSubscriberService.create(connectedNode.<Odometry>newSubscriber(odometryTopic, Odometry._TYPE));
    }

    private static FiniteTrajectory4d getFiniteTrajectory(ConnectedNode connectedNode) {
        final String trajectoryName = connectedNode.getParameterTree().getString("beswarm/trajectory");
        if ("straight_line".equals(trajectoryName)) {
            return getStraightLineTrajectory();
        } else {
            return getComplexTrajectory();
        }
    }

    private static FiniteTrajectory4d getComplexTrajectory() {
        Trajectory4d init = Trajectories.newHoldPositionTrajectory(Point4D.create(0, 0, 1, 0));
        FiniteTrajectory4d first = Trajectories.newStraightLineTrajectory(Point4D.create(0, 0, 1, 0),
                Point4D.create(1.5, -3.0, 1.5, 0), 0.1);
        Trajectory4d inter = Trajectories.newHoldPositionTrajectory(Point4D.create(1.5, -3.0, 1.5, 0));
        Trajectory4d second = Trajectories.newCircleTrajectory4D(Point3D.create(1.0, -3.0, 1.5), 0.5, 0.05,
                Math.PI / 8);
        Trajectory4d third = Trajectories.newHoldPositionTrajectory(Point4D.create(1.5, -3.5, 1.5, 0));
        Trajectory4d fourth = Trajectories.newHoldPositionTrajectory(Point4D.create(1.5, -3.5, 1.0, 0));
        return Choreography.builder()
                .withTrajectory(init)
                .forTime(4)
                .withTrajectory(first)
                .forTime(first.getTrajectoryDuration() + 2)
                .withTrajectory(inter)
                .forTime(5)
                .withTrajectory(second)
                .forTime(40)
                .withTrajectory(third)
                .forTime(10)
                .withTrajectory(fourth)
                .forTime(5)
                .build();
    }

    private static FiniteTrajectory4d getStraightLineTrajectory() {
        return Trajectories.newStraightLineTrajectory(Point4D.create(1.5, 0.0, 1.0, 0.0),
                Point4D.create(0.0, -4.0, 2.0, 0.0), 0.25);
    }
}
