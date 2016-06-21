package services.ros_subscribers;

import com.google.common.base.Optional;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;
import sensor_msgs.CameraInfo;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is to get the camera info. Camera info doesn't change during execution so we only want to get it once.
 *
 * @author Hoang Tung Dinh
 */
public final class CameraInfoSubscriber implements SubscriberFacade {
    private final Subscriber<CameraInfo> subscriber;
    private final CameraInfoListener cameraInfoListener = CameraInfoListener.create();
    private boolean startedListening = false;

    private CameraInfoSubscriber(Subscriber<CameraInfo> subscriber) {
        this.subscriber = subscriber;
    }

    public static CameraInfoSubscriber create(Subscriber<CameraInfo> subscriber) {
        return new CameraInfoSubscriber(subscriber);
    }

    @Override
    public void startListeningToMessages() {
        if (!startedListening) {
            subscriber.addMessageListener(cameraInfoListener);
            startedListening = true;
        }
    }

    public Optional<CameraInfo> getCameraInfo() {
        return cameraInfoListener.getCameraInfo();
    }

    private static final class CameraInfoListener implements MessageListener<CameraInfo> {
        private final AtomicReference<CameraInfo> cameraInfo = new AtomicReference<>();

        private CameraInfoListener() {}

        public static CameraInfoListener create() {
            return new CameraInfoListener();
        }

        @Override
        public void onNewMessage(CameraInfo t) {
            if (cameraInfo.get() == null) {
                cameraInfo.set(t);
            }
        }

        Optional<CameraInfo> getCameraInfo() {
            if (cameraInfo.get() == null) {
                return Optional.absent();
            } else {
                return Optional.of(cameraInfo.get());
            }
        }
    }
}
