package comm;

import org.ros.node.topic.Publisher;
import std_msgs.Empty;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class TakeoffPublisher {
    final Publisher<Empty> publisher;

    private TakeoffPublisher(Publisher<Empty> publisher) {
        this.publisher = publisher;
    }

    public static TakeoffPublisher create(Publisher<Empty> publisher) {
        checkArgument(publisher.getTopicName().toString().endsWith("/publishTakeoffCommand"),
                "Topic name must be [namespace]/publishTakeoffCommand");
        return new TakeoffPublisher(publisher);
    }

    public void publishTakeoffCommand() {
        final Empty empty = publisher.newMessage();
        publisher.publish(empty);
    }
}
