package tutorials;

import comm.CameraInfoSubscriber;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import sensor_msgs.CameraInfo;

import java.util.Arrays;

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
        final CameraInfoSubscriber cameraInfoSubscriber = CameraInfoSubscriber.create(subscriber);
        cameraInfoSubscriber.startListeningToCameraInfo();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.toString(cameraInfoSubscriber.getCameraInfo().getD()));
    }
}
