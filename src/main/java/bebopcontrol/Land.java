package bebopcontrol;

import org.ros.node.topic.Publisher;
import std_msgs.Empty;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Command for landing
 *
 * @author Hoang Tung Dinh
 */
public final class Land implements Command {

    private final Publisher<Empty> publisher;

    private Land(Publisher<Empty> publisher) {
        this.publisher = publisher;
    }

    public static Land create(Publisher<Empty> publisher) {
        checkArgument(publisher.getTopicName().toString().endsWith("/land"),
                "Topic name must be [namespace]/land");
        return new Land(publisher);
    }

    @Override
    public void execute() {
        final Empty empty = publisher.newMessage();
        publisher.publish(empty);
    }
}
