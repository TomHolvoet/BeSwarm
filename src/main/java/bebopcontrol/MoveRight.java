package bebopcontrol;

import geometry_msgs.Twist;
import org.ros.node.topic.Publisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class MoveRight implements Command {

    private final Publisher<Twist> publisher;
    private final double speed;
    private final double durationInSeconds;

    private MoveRight(Publisher<Twist> publisher, double speed, double durationInSeconds) {
        this.publisher = publisher;
        this.speed = speed;
        this.durationInSeconds = durationInSeconds;
    }

    public static MoveRight create(Publisher<Twist> publisher, double speed, double durationInSeconds) {
        checkArgument(publisher.getTopicName().toString().endsWith("/cmd_vel"),
                "Topic name must be [namespace]/cmd_vel");
        checkArgument(speed > 0 && speed <= 1, "speed must be in range (0, 1]");
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        return new MoveRight(publisher, speed, durationInSeconds);
    }

    @Override
    public void execute() {
        final Velocity velocity = Velocity.builder().linearY(-speed).build();
        final Command move = Move.create(publisher, velocity, durationInSeconds);
        move.execute();
    }
}
