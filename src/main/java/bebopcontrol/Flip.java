package bebopcontrol;

import org.ros.node.topic.Publisher;
import std_msgs.UInt8;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class Flip implements Command {

    private final Publisher<UInt8> publisher;
    private final Direction direction;

    private Flip(Publisher<UInt8> publisher, Direction direction) {
        this.publisher = publisher;
        this.direction = direction;
    }

    public static Flip create(Publisher<UInt8> publisher, Direction direction) {
        checkArgument(publisher.getTopicName().toString().endsWith("/flip"), "Topic name must be [namespace]/flip");
        return new Flip(publisher, direction);
    }

    @Override
    public void execute() {
        final UInt8 message = publisher.newMessage();
        message.setData(direction.getCode());
        publisher.publish(message);
    }
}
