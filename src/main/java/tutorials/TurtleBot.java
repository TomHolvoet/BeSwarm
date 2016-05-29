package tutorials;

import geometry_msgs.Twist;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

/**
 * @author Hoang Tung Dinh
 */
public class TurtleBot extends AbstractNodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("TungsTurtleBot");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final Publisher<Twist> publisher = connectedNode.newPublisher("/turtle1/cmd_vel", Twist._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {
            private int sequenceNumber;

            @Override
            protected void setup() {
                sequenceNumber = 0;
            }

            @Override
            protected void loop() throws InterruptedException {
                Twist twist = publisher.newMessage();
                sequenceNumber++;

                if (sequenceNumber == 3) {
                    sequenceNumber = 0;
                    twist.getAngular().setZ(Math.PI / 2);
                } else {
                    twist.getLinear().setX(2);
                }

                publisher.publish(twist);

                Thread.sleep(1000);
            }
        });
    }
}
