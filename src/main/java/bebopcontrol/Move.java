package bebopcontrol;

import geometry_msgs.Twist;
import org.ros.node.topic.Publisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class Move implements Command {

    private final Publisher<Twist> publisher;
    private final Velocity velocity;
    private final double durationInSeconds;

    private Move(Publisher<Twist> publisher, Velocity velocity, double durationInSeconds) {
        this.publisher = publisher;
        this.velocity = velocity;
        this.durationInSeconds = durationInSeconds;
    }

    public static Move create(Publisher<Twist> publisher, Velocity velocity, double durationInSeconds) {
        checkArgument(publisher.getTopicName().toString().endsWith("/cmd_vel"),
                "Topic name must be [namespace]/cmd_vel");
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        return new Move(publisher, velocity, durationInSeconds);
    }

    @Override
    public void execute() {
        final long durationInMilliSeconds = (long) (durationInSeconds * 1000);
        final Twist twist = publisher.newMessage();
        twist.getAngular().setZ(velocity.angularZ());
        twist.getLinear().setX(velocity.linearX());
        twist.getLinear().setY(velocity.linearY());
        twist.getLinear().setZ(velocity.linearZ());

        publisher.publish(twist);
        try {
            Thread.sleep(durationInMilliSeconds);
        } catch (InterruptedException e) {
            // TODO write to log
        }

        final Command stopMoving = StopMoving.create(publisher);
        stopMoving.execute();
    }
}
