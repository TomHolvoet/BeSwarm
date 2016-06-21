package services.ros_subscribers;

import keyboard.Key;

import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Hoang Tung Dinh
 */
public final class KeyboardSubscriber implements SubscriberFacade, RosKeySubject {
    private final Subscriber<Key> subscriber;
    private final Collection<RosKeyObserver> rosKeyObservers = new ArrayList<>();
    private final MessageListener<Key> rosKeyListener = new RosKeyListener();
    private boolean startedListening = false;

    private KeyboardSubscriber(Subscriber<Key> subscriber) {
        this.subscriber = subscriber;
    }

    public static KeyboardSubscriber create(Subscriber<Key> subscriber) {
        return new KeyboardSubscriber(subscriber);
    }

    @Override
    public void startListeningToMessages() {
        if (!startedListening) {
            subscriber.addMessageListener(rosKeyListener);
            startedListening = true;
        }
    }

    @Override
    public void registerObserver(RosKeyObserver rosKeyObserver) {
        rosKeyObservers.add(rosKeyObserver);
    }

    @Override
    public void removeObserver(RosKeyObserver rosKeyObserver) {
        rosKeyObservers.remove(rosKeyObserver);
    }

    private final class RosKeyListener implements MessageListener<Key> {
        @Override
        public void onNewMessage(Key key) {
            for (final RosKeyObserver rosKeyObserver : rosKeyObservers) {
                rosKeyObserver.update(key);
            }
        }
    }
}
