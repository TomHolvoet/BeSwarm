package services.parrot;

import org.ros.node.topic.Publisher;
import services.LandService;
import std_msgs.Empty;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A facade which provides an interface to publish landing message.
 *
 * @author Hoang Tung Dinh
 */
public final class ParrotLandService implements LandService {

    private final Publisher<Empty> publisher;

    private ParrotLandService(Publisher<Empty> publisher) {
        this.publisher = publisher;
    }

    /**
     * @param publisher the publisher associated to the "land" topic.
     * @return an instance of this facade
     * @see <a href="http://bebop-autonomy.readthedocs.io/en/latest/piloting.html#land">Bebop
     * documentations</a>
     */
    public static ParrotLandService create(Publisher<Empty> publisher) {
        checkArgument(publisher.getTopicName().toString().endsWith("/land"), "Topic name must be [namespace]/land");
        return new ParrotLandService(publisher);
    }

    /**
     * Publish a landing message.
     */
    @Override
    public void sendLandingMessage() {
        final Empty empty = publisher.newMessage();
        publisher.publish(empty);
    }
}
