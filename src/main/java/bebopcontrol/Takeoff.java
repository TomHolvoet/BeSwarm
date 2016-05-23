package bebopcontrol;

import org.ros.node.topic.Publisher;
import std_msgs.Empty;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Command for taking off.
 *
 * @author Hoang Tung Dinh
 */
public final class Takeoff implements Command {

    private final Publisher<Empty> publisher;

    private Takeoff(Publisher<Empty> publisher) {
        this.publisher = publisher;
    }

    public static Takeoff create(Publisher<Empty> publisher) {
        checkArgument(publisher.getTopicName().toString().endsWith("/takeoff"),
                "Topic name must be [namespace]/takeoff");
        return new Takeoff(publisher);
    }

    @Override
    public void execute() {
        final Empty empty = publisher.newMessage();
        publisher.publish(empty);
    }
}
