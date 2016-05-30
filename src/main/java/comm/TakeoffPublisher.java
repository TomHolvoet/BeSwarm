package comm;

import org.ros.node.topic.Publisher;
import std_msgs.Empty;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A facade which provides an interface to publish taking off message.
 *
 * @author Hoang Tung Dinh
 */
public final class TakeoffPublisher {

    private final Publisher<Empty> publisher;

    private TakeoffPublisher(Publisher<Empty> publisher) {
        this.publisher = publisher;
    }

    /**
     * @param publisher the publisher associated to the "takeoff" topic.
     * @return an instance of this facade
     * @see <a href="http://bebop-autonomy.readthedocs.io/en/latest/piloting.html#takeoff">Bebop
     * documentations</a>
     */
    public static TakeoffPublisher create(Publisher<Empty> publisher) {
        checkArgument(publisher.getTopicName().toString().endsWith("/takeoff"),
                "Topic name must be [namespace]/takeoff");
        return new TakeoffPublisher(publisher);
    }

    /**
     * Publish a taking off message.
     */
    public void publishTakingOffMessage() {
        final Empty empty = publisher.newMessage();
        publisher.publish(empty);
    }
}
