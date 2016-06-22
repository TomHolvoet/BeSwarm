package control;

import com.google.common.base.Optional;

import control.dto.Velocity;
import gazebo_msgs.ModelStates;
import geometry_msgs.Twist;
import services.ros_subscribers.MessagesSubscriberService;

/**
 * @author Hoang Tung Dinh
 */
public final class ModelStateVelocityEstimator implements VelocityEstimator {
    private final MessagesSubscriberService<ModelStates> modelStateSubscriber;
    private final String modelName;

    private ModelStateVelocityEstimator(MessagesSubscriberService<ModelStates> modelStateSubscriber, String modelName) {
        this.modelStateSubscriber = modelStateSubscriber;
        this.modelName = modelName;
        this.modelStateSubscriber.startListeningToMessages();
    }

    public static ModelStateVelocityEstimator create(MessagesSubscriberService<ModelStates> modelStateSubscriber, String modelName) {
        return new ModelStateVelocityEstimator(modelStateSubscriber, modelName);
    }

    @Override
    public Optional<Velocity> getCurrentVelocity() {
        final Optional<ModelStates> modelStateOptional = modelStateSubscriber.getMostRecentMessage();
        if (modelStateOptional.isPresent()) {
            final ModelStates modelStates = modelStateOptional.get();
            final int index = modelStates.getName().indexOf(modelName);
            if (index == -1) {
                return Optional.absent();
            }
            final Twist gazeboTwist = modelStates.getTwist().get(index);
            final Velocity currentVelocity = Velocity.builder()
                    .linearX(gazeboTwist.getLinear().getX())
                    .linearY(gazeboTwist.getLinear().getY())
                    .linearZ(gazeboTwist.getLinear().getZ())
                    .angularZ(gazeboTwist.getAngular().getZ())
                    .build();
            return Optional.of(currentVelocity);
        } else {
            return Optional.absent();
        }
    }
}
