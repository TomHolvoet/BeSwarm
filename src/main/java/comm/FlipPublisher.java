package comm;

import behavior.Direction;
import org.ros.node.topic.Publisher;
import std_msgs.UInt8;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A facade which provides the interface to publish flipping message.
 *
 * @author Hoang Tung Dinh
 */
public final class FlipPublisher {

    private final Publisher<UInt8> publisher;

    private FlipPublisher(Publisher<UInt8> publisher) {
        this.publisher = publisher;
    }

    /**
     * @param publisher the publisher associated to the "flip" topic.
     * @return an instance of this facade
     * @see <a href="http://bebop-autonomy.readthedocs.io/en/latest/piloting.html#flight-animations">Bebop
     * documentations</a>
     */
    public static FlipPublisher create(Publisher<UInt8> publisher) {
        checkArgument(publisher.getTopicName().toString().endsWith("/flip"), "Topic name must be [namespace]/flip");
        return new FlipPublisher(publisher);
    }

    /**
     * Publish a flip message.
     *
     * @param direction the flipping direction
     */
    public void publishFlipMessage(Direction direction) {
        final UInt8 message = publisher.newMessage();
        message.setData(direction.getCode());
        publisher.publish(message);
    }
}
