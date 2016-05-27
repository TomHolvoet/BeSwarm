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

    private static final String errorMessage = "Input must be in range [-1..1]";

    private Move(Publisher<Twist> publisher, Velocity velocity, double durationInSeconds) {
        this.publisher = publisher;
        this.velocity = velocity;
        this.durationInSeconds = durationInSeconds;
    }

    public static Move create(Publisher<Twist> publisher, Velocity velocity, double durationInSeconds) {
        checkArgument(publisher.getTopicName().toString().endsWith("/cmd_vel"),
                "Topic name must be [namespace]/cmd_vel");
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        checkArgument(velocity.linearX() >= -1 && velocity.linearX() <= 1, errorMessage);
        checkArgument(velocity.linearY() >= -1 && velocity.linearY() <= 1, errorMessage);
        checkArgument(velocity.linearZ() >= -1 && velocity.linearZ() <= 1, errorMessage);
        checkArgument(velocity.angularZ() >= -1 && velocity.angularZ() <= 1, errorMessage);
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

        final long startTime = System.currentTimeMillis();

        while (true) {
            publisher.publish(twist);
            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
                // TODO write to log
            }

            final long duration = System.currentTimeMillis() - startTime;
            if (duration >= durationInMilliSeconds) {
                break;
            }
        }

        final Command stopMoving = StopMoving.create(publisher);
        stopMoving.execute();
    }
}
