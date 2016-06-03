package control;

import commands.Velocity;
import com.google.common.base.Optional;
import comm.ModelStateSubscriber;
import gazebo_msgs.ModelStates;
import geometry_msgs.Twist;

/**
 * @author Hoang Tung Dinh
 */
public final class ModelStateVelocityProvider implements VelocityProvider {
    private final ModelStateSubscriber modelStateSubscriber;
    private final String modelName;

    private ModelStateVelocityProvider(ModelStateSubscriber modelStateSubscriber, String modelName) {
        this.modelStateSubscriber = modelStateSubscriber;
        this.modelName = modelName;
        this.modelStateSubscriber.startListeningToMessages();
    }

    public static ModelStateVelocityProvider create(ModelStateSubscriber modelStateSubscriber, String modelName) {
        return new ModelStateVelocityProvider(modelStateSubscriber, modelName);
    }

    @Override
    public Optional<Velocity> getCurrentVelocity() {
        final Optional<ModelStates> modelStateOptional = modelStateSubscriber.getMostRecentModelStates();
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
