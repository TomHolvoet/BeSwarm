package comm;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;
import sensor_msgs.Image;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Hoang Tung Dinh
 */
public final class ImageSubscriber {
    private final Subscriber<Image> subscriber;
    private final ImageListener imageListener = ImageListener.create();
    private boolean startedListening = false;

    private ImageSubscriber(Subscriber<Image> subscriber) {
        this.subscriber = subscriber;
    }

    public static ImageSubscriber create(Subscriber<Image> subscriber) {
        return new ImageSubscriber(subscriber);
    }

    public void startListeningToImage() {
        if (!startedListening) {
            subscriber.addMessageListener(imageListener);
        }
    }

    @Nullable
    public Image getMostRecentImage() {
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

        @Nullable
        Image getMostRecentImage() {
            return mostRecentImage.get();
        }
    }
}
