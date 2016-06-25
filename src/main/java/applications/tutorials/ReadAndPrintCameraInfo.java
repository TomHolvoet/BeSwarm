package applications.tutorials;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import sensor_msgs.CameraInfo;
import services.ros_subscribers.MessagesSubscriberService;

/**
 * @author Hoang Tung Dinh
 */
public class ReadAndPrintCameraInfo extends AbstractNodeMain {
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ReadAndPrintCameraInfo");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        final Subscriber<CameraInfo> subscriber = connectedNode.newSubscriber("/bebop/camera_info", CameraInfo._TYPE);
        final MessagesSubscriberService<CameraInfo> cameraInfoSubscriber = MessagesSubscriberService.<CameraInfo>create(subscriber);
        while (true) {
            try {
                Thread.sleep(50);
                System.out.println(cameraInfoSubscriber.getMostRecentMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
