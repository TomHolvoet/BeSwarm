package services.ros_subscribers;

import com.google.common.base.Optional;
import gazebo_msgs.ModelStates;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Hoang Tung Dinh
 */
public final class ModelStateSubscriber implements SubscriberFacade {
    private final Subscriber<ModelStates> subscriber;
    private final ModelStateListener modelStateListener = ModelStateListener.create();
    private boolean startedListeningToModelStates = false;

    private ModelStateSubscriber(Subscriber<ModelStates> subscriber) {
        this.subscriber = subscriber;
    }

    public static ModelStateSubscriber create(Subscriber<ModelStates> subscriber) {
        return new ModelStateSubscriber(subscriber);
    }

    @Override
    public void startListeningToMessages() {
        if (!startedListeningToModelStates) {
            subscriber.addMessageListener(modelStateListener);
            startedListeningToModelStates = true;
        }
    }

    public Optional<ModelStates> getMostRecentModelStates() {
        return modelStateListener.getMostRecentModelStates();
    }

    private static final class ModelStateListener implements MessageListener<ModelStates> {
        private final AtomicReference<ModelStates> modelStates = new AtomicReference<>();

        private ModelStateListener() {}

        public static ModelStateListener create() {
            return new ModelStateListener();
        }

        @Override
        public void onNewMessage(ModelStates t) {
            modelStates.set(t);
        }

        Optional<ModelStates> getMostRecentModelStates() {
            if (modelStates.get() == null) {
                return Optional.absent();
            } else {
                return Optional.of(modelStates.get());
            }
        }
    }
}
