package bebopcontrol;

import geometry_msgs.Twist;
import org.ros.node.topic.Publisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class Hover implements Command {

    private final Publisher<Twist> publisher;
    private final double durationInSeconds;

    private Hover(Publisher<Twist> publisher, double durationInSeconds) {
        this.publisher = publisher;
        this.durationInSeconds = durationInSeconds;
    }

    public static Hover create(Publisher<Twist> publisher, double durationInSeconds) {
        checkArgument(publisher.getTopicName().toString().endsWith("/cmd_vel"),
                "Topic name must be [namespace]/cmd_vel");
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        return new Hover(publisher, durationInSeconds);
    }

    @Override
    public void execute() {
        final Command stopMoving = StopMoving.create(publisher);
        stopMoving.execute();
        final long durationInMilliSeconds = (long) (durationInSeconds * 1000);
        try {
            Thread.sleep(durationInMilliSeconds);
        } catch (InterruptedException e) {
            // TODO write to log
        }
    }
}
