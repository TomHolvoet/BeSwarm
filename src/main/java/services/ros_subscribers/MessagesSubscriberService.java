package services.ros_subscribers;

import java.util.concurrent.atomic.AtomicReference;

import org.ros.internal.message.Message;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import com.google.common.base.Optional;

/**
 * @author mhct
 */
public class MessagesSubscriberService<T extends Message> {
    private final Subscriber<T> subscriber;
    private final MessagesListener<T> messagesListener = MessagesListener.<T>create();
    private boolean startedListeningToMessages = false;

    protected MessagesSubscriberService(Subscriber<T> subscriber) {
        this.subscriber = subscriber;
    }

    public static <Type extends Message> MessagesSubscriberService<Type> create(Subscriber<Type> subscriber) {
        return new MessagesSubscriberService<>(subscriber);
    }

    public void startListeningToMessages() {
        if (!startedListeningToMessages) {
            subscriber.addMessageListener(messagesListener);
            startedListeningToMessages = true;
        }
    }

    public Optional<T> getMostRecentMessage() {
        return messagesListener.getMostRecentMessage();
    }

    private static final class MessagesListener<K extends Message> implements MessageListener<K> {
        private final AtomicReference<K> message = new AtomicReference<>();

        private MessagesListener() {}

        public static <Type extends Message> MessagesListener<Type> create() {
            return new MessagesListener<>();
        }

        @Override
        public void onNewMessage(K t) {
            message.set(t);
        }

        Optional<K> getMostRecentMessage() {
            if (message.get() == null) {
                return Optional.<K>absent();
            } else {
                return Optional.<K>of(message.get());
            }
        }
    }
}
