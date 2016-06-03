package comm;

import com.google.common.base.Optional;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;
import sensor_msgs.Image;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Hoang Tung Dinh
 */
public final class ImageSubscriber implements SubscriberFacade {
    private final Subscriber<Image> subscriber;
    private final ImageListener imageListener = ImageListener.create();
    private boolean startedListening = false;

    private ImageSubscriber(Subscriber<Image> subscriber) {
        this.subscriber = subscriber;
    }

    public static ImageSubscriber create(Subscriber<Image> subscriber) {
        return new ImageSubscriber(subscriber);
    }

    @Override
    public void startListeningToMessages() {
        if (!startedListening) {
            subscriber.addMessageListener(imageListener);
            startedListening = true;
        }
    }

    public Optional<Image> getMostRecentImage() {
        return imageListener.getMostRecentImage();
    }

    private static final class ImageListener implements MessageListener<Image> {
        private final AtomicReference<Image> mostRecentImage = new AtomicReference<>();

        private ImageListener() {}

        public static ImageListener create() {
            return new ImageListener();
        }

        @Override
        public void onNewMessage(Image image) {
            mostRecentImage.set(image);
        }

        Optional<Image> getMostRecentImage() {
            if (mostRecentImage.get() == null) {
                return Optional.absent();
            } else {
                return Optional.of(mostRecentImage.get());
            }
        }
    }
}
