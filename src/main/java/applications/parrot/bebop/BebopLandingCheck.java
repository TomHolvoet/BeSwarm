package applications.parrot.bebop;

import java.util.concurrent.TimeUnit;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import commands.Command;
import commands.Land;
import commands.MoveToPose;
import commands.Takeoff;
import control.PidParameters;
import control.dto.BodyFrameVelocity;
import control.dto.InertialFrameVelocity;
import control.dto.Pose;
import control.dto.Velocity;
import control.localization.StateEstimator;
import geometry_msgs.PoseStamped;
import nav_msgs.Odometry;
import services.LandService;
import services.ServiceFactory;
import services.TakeOffService;
import services.VelocityService;
import services.parrot.BebopServiceFactory;
import services.ros_subscribers.MessagesSubscriberService;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import taskexecutor.TaskType;
import utils.math.Transformations;
import control.dto.DroneStateStamped;

/**
 * @author Hoang Tung Dinh
 */
public class BebopLandingCheck extends AbstractNodeMain {
    private static final Logger logger = LoggerFactory.getLogger(BebopLandingCheck.class);
    private static final String DRONE_NAME = "bebop";

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("BebopSimpleLinePattern");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        boolean lateDelay = connectedNode.getParameterTree().getBoolean("/beswarm/late_delay");
        long lateDelayMilliseconds = connectedNode.getParameterTree().getInteger("/beswarm/late_delay_milliseconds");
        
    	try {
	        final ServiceFactory serviceFactory = BebopServiceFactory.create(connectedNode, DRONE_NAME);
	        TakeOffService takeoffService = serviceFactory.createTakeOffService();
	        VelocityService velocityService = serviceFactory.createVelocityService();
	        LandService landService = serviceFactory.createLandService();
	
	    	TimeUnit.SECONDS.sleep(3);
	
	        takeoffService.sendTakingOffMessage();
        	TimeUnit.SECONDS.sleep(10);
	        InertialFrameVelocity nulVel = Velocity.createZeroVelocity();
	        Pose pose = Pose.createZeroPose();
	        for (int i=0; i<50; i++) {
	        	TimeUnit.MILLISECONDS.sleep(50);
	        	velocityService.sendVelocityMessage(nulVel, pose);
	        }
	        if (lateDelay) {
	        	TimeUnit.MILLISECONDS.sleep(lateDelayMilliseconds);
	        }
	        landService.sendLandingMessage();
	        velocityService.sendVelocityMessage(nulVel, pose);
	        velocityService.sendVelocityMessage(nulVel, pose);
	        velocityService.sendVelocityMessage(nulVel, pose);
	        velocityService.sendVelocityMessage(nulVel, pose);
    	} catch (InterruptedException e) {
    		logger.info("Warm up time is interrupted.", e);
    	}
    }
}
