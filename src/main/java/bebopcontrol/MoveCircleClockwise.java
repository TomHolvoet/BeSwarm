package bebopcontrol;

import geometry_msgs.Twist;
import org.ros.node.topic.Publisher;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Hoang Tung Dinh
 */
public final class MoveCircleClockwise implements Command {

    private final Publisher<Twist> publisher;
    private final double forwardSpeed;
    private final double rotationSpeed;
    private final double durationInSeconds;

    private MoveCircleClockwise(Publisher<Twist> publisher, double forwardSpeed, double rotationSpeed,
            double durationInSeconds) {
        this.publisher = publisher;
        this.forwardSpeed = forwardSpeed;
        this.rotationSpeed = rotationSpeed;
        this.durationInSeconds = durationInSeconds;
    }

    public static MoveCircleClockwise create(Publisher<Twist> publisher, double forwardSpeed, double rotationSpeed,
            double durationInSeconds) {
        checkArgument(publisher.getTopicName().toString().endsWith("/cmd_vel"),
                "Topic name must be [namespace]/cmd_vel");
        checkArgument(forwardSpeed > 0 && forwardSpeed <= 1, "forwardSpeed must be in range (0, 1]");
        checkArgument(rotationSpeed > 0 && rotationSpeed <= 1, "rotationSpeed must be in range (0, 1]");
        checkArgument(durationInSeconds > 0, "Duration must be a positive value");
        return new MoveCircleClockwise(publisher, forwardSpeed, rotationSpeed, durationInSeconds);
    }

    @Override
    public void execute() {
        final Velocity velocity = Velocity.builder().linearX(forwardSpeed).angularZ(-rotationSpeed).build();
        final Command move = Move.create(publisher, velocity, durationInSeconds);
        move.execute();
    }
}
