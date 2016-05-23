package bebopcontrol;

import geometry_msgs.Twist;
import org.ros.node.topic.Publisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class StopMoving implements Command {

    private final Publisher<Twist> publisher;

    private StopMoving(Publisher<Twist> publisher) {
        this.publisher = publisher;
    }

    public static StopMoving create(Publisher<Twist> publisher) {
        checkArgument(publisher.getTopicName().toString().endsWith("/cmd_vel"),
                "Topic name must be [namespace]/cmd_vel");
        return new StopMoving(publisher);
    }

    @Override
    public void execute() {
        final Twist twist = publisher.newMessage();
        twist.getAngular().setZ(0);
        twist.getLinear().setX(0);
        twist.getLinear().setY(0);
        twist.getLinear().setZ(0);
        publisher.publish(twist);
    }
}
