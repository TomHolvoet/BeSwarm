package comm;

import bebopbehavior.Direction;
import org.ros.node.topic.Publisher;
import std_msgs.UInt8;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class FlipPublisher {

    private final Publisher<UInt8> publisher;

    private FlipPublisher(Publisher<UInt8> publisher) {
        this.publisher = publisher;
    }

    public static FlipPublisher create(Publisher<UInt8> publisher) {
        checkArgument(publisher.getTopicName().toString().endsWith("/flip"), "Topic name must be [namespace]/flip");
        return new FlipPublisher(publisher);
    }

    public void publishFlipCommand(Direction direction) {
        final UInt8 message = publisher.newMessage();
        message.setData(direction.getCode());
        publisher.publish(message);
    }
}
