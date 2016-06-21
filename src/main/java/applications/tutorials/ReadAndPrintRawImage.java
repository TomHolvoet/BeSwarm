package applications.tutorials;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import sensor_msgs.CameraInfo;
import sensor_msgs.Image;
import services.ros_subscribers.ImageSubscriber;

/**
 * @author Hoang Tung Dinh
 */
public class ReadAndPrintRawImage extends AbstractNodeMain {
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ReadAndPrintRawImage");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        final Subscriber<Image> subscriber = connectedNode.newSubscriber("/bebop/image_raw", CameraInfo._TYPE);
        final ImageSubscriber imageSubscriber = ImageSubscriber.create(subscriber);
        imageSubscriber.startListeningToMessages();
        while (true) {
            try {
                Thread.sleep(50);
                System.out.println(imageSubscriber.getMostRecentImage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
