package services.parrot;

import org.ros.node.topic.Publisher;
import services.TakeOffService;
import std_msgs.Empty;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A facade which provides an interface to publish taking off message.
 *
 * @author Hoang Tung Dinh
 */
final class ParrotTakeOffService implements TakeOffService {

    private final Publisher<Empty> publisher;

    private ParrotTakeOffService(Publisher<Empty> publisher) {
        this.publisher = publisher;
    }

    /**
     * @param publisher the publisher associated to the "takeoff" topic.
     * @return an instance of this facade
     * @see <a href="http://bebop-autonomy.readthedocs.io/en/latest/piloting.html#takeoff">Bebop
     * documentations</a>
     */
    public static ParrotTakeOffService create(Publisher<Empty> publisher) {
        checkArgument(publisher.getTopicName().toString().endsWith("/takeoff"),
                "Topic name must be [namespace]/takeoff");
        return new ParrotTakeOffService(publisher);
    }

    /**
     * Publish a taking off message.
     */
    @Override
    public void sendTakingOffMessage() {
        final Empty empty = publisher.newMessage();
        publisher.publish(empty);
    }

    @Override
    public void sendTakingOffMessage(double desiredAltitude) {
        throw new UnsupportedOperationException("Parrot drone does not support this service");
    }
}
