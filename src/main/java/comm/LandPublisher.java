package comm;

import org.ros.node.topic.Publisher;
import std_msgs.Empty;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public class LandPublisher {
    private final Publisher<Empty> publisher;

    private LandPublisher(Publisher<Empty> publisher) {
        this.publisher = publisher;
    }

    public static LandPublisher create(Publisher<Empty> publisher) {
        checkArgument(publisher.getTopicName().toString().endsWith("/land"), "Topic name must be [namespace]/land");
        return new LandPublisher(publisher);
    }

    public void publishLandCommand() {
        final Empty empty = publisher.newMessage();
        publisher.publish(empty);
    }
}
