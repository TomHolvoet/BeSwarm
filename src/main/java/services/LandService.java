package services;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import std_msgs.Empty;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A facade which provides an interface to publish landing message.
 *
 * @author Hoang Tung Dinh
 */
public final class LandService {

    private final Publisher<Empty> publisher;

    private LandService(Publisher<Empty> publisher) {
        this.publisher = publisher;
    }

    /**
     * @param publisher the publisher associated to the "land" topic.
     * @return an instance of this facade
     * @see <a href="http://bebop-autonomy.readthedocs.io/en/latest/piloting.html#land">Bebop
     * documentations</a>
     */
    public static LandService create(Publisher<Empty> publisher) {
        checkArgument(publisher.getTopicName().toString().endsWith("/land"), "Topic name must be [namespace]/land");
        return new LandService(publisher);
    }

    public static LandService createService(String droneName, ConnectedNode connectedNode) {
    	return LandService.create(connectedNode.<Empty>newPublisher(droneName + "/land", Empty._TYPE));
    }
    
    /**
     * Publish a landing message.
     */
    public void publishLandingMessage() {
        final Empty empty = publisher.newMessage();
        publisher.publish(empty);
    }
}
