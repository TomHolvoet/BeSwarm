package services.rossubscribers;

import keyboard.Key;
import org.ros.node.topic.Subscriber;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Hoang Tung Dinh
 */
public final class KeyboardSubscriber extends MessagesSubscriberService<Key> implements RosKeySubject {
    private final Collection<RosKeyObserver> rosKeyObservers = new ArrayList<>();
    private static final int MESSAGE_QUEUE_SIZE = 1;

    private KeyboardSubscriber(Subscriber<Key> subscriber) {
        super(subscriber, MESSAGE_QUEUE_SIZE);
    }

    /**
     * Creates a keyboard subscriber.
     *
     * @param subscriber the rostopic subscriber for the keyboard topic
     * @return a keyboard subscriber
     */
    public static KeyboardSubscriber createKeyboardSubscriber(Subscriber<Key> subscriber) {
        return new KeyboardSubscriber(subscriber);
    }

    @Override
    public void registerObserver(RosKeyObserver rosKeyObserver) {
        rosKeyObservers.add(rosKeyObserver);
    }

    @Override
    public void removeObserver(RosKeyObserver rosKeyObserver) {
        rosKeyObservers.remove(rosKeyObserver);
    }
}
