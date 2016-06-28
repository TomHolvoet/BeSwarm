package applications;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import com.google.common.base.Optional;

import geometry_msgs.PoseStamped;
import services.ros_subscribers.MessagesSubscriberService;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;
import taskexecutor.TaskExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class PrintPose extends AbstractNodeMain {

	private static Logger logger = LoggerFactory.getLogger(PrintPose.class);
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("PosePrinter");
	}
	
	@Override
    public void onStart(final ConnectedNode connectedNode) {
    	String POSE_TOPIC = "/arlocros/pose";
    	MessagesSubscriberService<PoseStamped> poseSubscriber = MessagesSubscriberService.<PoseStamped>create(connectedNode.<PoseStamped>newSubscriber(POSE_TOPIC, PoseStamped._TYPE));
    	
    	try {
    		int i=0;
	    	for (;;) {
	    		Optional<PoseStamped> pose = poseSubscriber.getMostRecentMessage();
	    		if (pose.isPresent()) {
	    			double x = pose.get().getPose().getPosition().getX();
	    			double y = pose.get().getPose().getPosition().getY();
	    			double z = pose.get().getPose().getPosition().getZ();
	    			logger.info("{}, {}, {}", x, y, z);
	    		} else if (i%100 == 0){
	    			logger.info("No pose available");
	    		}
	    		i++;
	    		Thread.sleep(100);
	    	}
    	} catch (Exception e) {
    		//bla
    	}
	}


}
